package com.twoheart.dailyhotel.screen.hotel.filter;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.PlaceCuration;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.StayCuration;
import com.twoheart.dailyhotel.model.StayCurationOption;
import com.twoheart.dailyhotel.model.StayFilter;
import com.twoheart.dailyhotel.model.StayParams;
import com.twoheart.dailyhotel.place.activity.PlaceCurationActivity;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyTextView;
import com.twoheart.dailyhotel.widget.DailyToast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class StayCurationActivity extends PlaceCurationActivity implements RadioGroup.OnCheckedChangeListener
{
    public static final String INTENT_EXTRA_DATA_VIEWTYPE = "viewType";

    protected StayCuration mStayCuration;
    protected StayParams mLastParams;
    protected ViewType mViewType;

    protected BaseNetworkController mNetworkController;

    protected RadioGroup mSortRadioGroup;
    protected android.support.v7.widget.GridLayout mGridLayout;

    private View mMinusPersonView;
    private View mPlusPersonView;
    private TextView mPersonCountView;
    protected ViewGroup mBedTypeLayout;

    public static Intent newInstance(Context context, ViewType viewType, StayCuration stayCuration)
    {
        Intent intent = new Intent(context, StayCurationActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_VIEWTYPE, viewType.name());
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACECURATION, stayCuration);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        if (intent == null)
        {
            finish();
            return;
        }

        initIntent(intent);

        mNetworkController = getNetworkController(this);

        initLayout();

        mAnimationLayout.setVisibility(View.INVISIBLE);
        mAnimationLayout.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                showAnimation();
            }
        }, 20);
    }

    protected void initIntent(Intent intent)
    {
        mViewType = ViewType.valueOf(intent.getStringExtra(INTENT_EXTRA_DATA_VIEWTYPE));
        mStayCuration = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PLACECURATION);
    }

    @Override
    protected void initContentLayout(ViewGroup contentLayout)
    {
        StayCurationOption stayCurationOption = (StayCurationOption) mStayCuration.getCurationOption();

        View sortLayout = LayoutInflater.from(this).inflate(R.layout.layout_hotel_sort, null);
        initSortLayout(sortLayout, mViewType, stayCurationOption);

        contentLayout.addView(sortLayout);

        if (mStayCuration.getProvince().isOverseas == false)
        {
            View filterLayout = LayoutInflater.from(this).inflate(R.layout.layout_hotel_filter, null);
            initFilterLayout(filterLayout, stayCurationOption);

            initAmenitiesLayout(filterLayout, stayCurationOption);

            contentLayout.addView(filterLayout);
        } else
        {
            requestUpdateResult();
        }
    }

    protected void initSortLayout(View view, ViewType viewType, StayCurationOption stayCurationOption)
    {
        mSortRadioGroup = (RadioGroup) view.findViewById(R.id.sortLayout);

        if (mStayCuration.getProvince().isOverseas == true)
        {
            View satisfactionCheckView = view.findViewById(R.id.satisfactionCheckView);
            satisfactionCheckView.setVisibility(View.INVISIBLE);
        }

        if (viewType == ViewType.MAP)
        {
            setDisabledSortLayout(view, mSortRadioGroup);
            return;
        }

        switch (stayCurationOption.getSortType())
        {
            case DEFAULT:
                mSortRadioGroup.check(R.id.regionCheckView);
                break;

            case DISTANCE:
                mSortRadioGroup.check(R.id.distanceCheckView);

                searchMyLocation();
                break;

            case LOW_PRICE:
                mSortRadioGroup.check(R.id.lowPriceCheckView);
                break;

            case HIGH_PRICE:
                mSortRadioGroup.check(R.id.highPriceCheckView);
                break;

            case SATISFACTION:
                mSortRadioGroup.check(R.id.satisfactionCheckView);
                break;
        }

        mSortRadioGroup.setOnCheckedChangeListener(this);
    }

    protected void initFilterLayout(View view, StayCurationOption stayCurationOption)
    {
        // 인원
        mMinusPersonView = view.findViewById(R.id.minusPersonView);
        mPlusPersonView = view.findViewById(R.id.plusPersonView);

        mPersonCountView = (TextView) view.findViewById(R.id.personCountView);

        mMinusPersonView.setOnClickListener(this);
        mPlusPersonView.setOnClickListener(this);

        updatePersonFilter(stayCurationOption.person);

        // 베드타입
        mBedTypeLayout = (ViewGroup) view.findViewById(R.id.bedTypeLayout);
        DailyTextView doubleCheckView = (DailyTextView) view.findViewById(R.id.doubleCheckView);
        DailyTextView twinCheckView = (DailyTextView) view.findViewById(R.id.twinCheckView);
        DailyTextView heatedFloorsCheckView = (DailyTextView) view.findViewById(R.id.heatedFloorsCheckView);

        doubleCheckView.setOnClickListener(this);
        twinCheckView.setOnClickListener(this);
        heatedFloorsCheckView.setOnClickListener(this);

        if ((stayCurationOption.flagBedTypeFilters & StayFilter.FLAG_HOTEL_FILTER_BED_DOUBLE) == StayFilter.FLAG_HOTEL_FILTER_BED_DOUBLE)
        {
            updateBedTypeFilter(doubleCheckView, StayFilter.FLAG_HOTEL_FILTER_BED_DOUBLE);
        }

        if ((stayCurationOption.flagBedTypeFilters & StayFilter.FLAG_HOTEL_FILTER_BED_TWIN) == StayFilter.FLAG_HOTEL_FILTER_BED_TWIN)
        {
            updateBedTypeFilter(twinCheckView, StayFilter.FLAG_HOTEL_FILTER_BED_TWIN);
        }

        if ((stayCurationOption.flagBedTypeFilters & StayFilter.FLAG_HOTEL_FILTER_BED_HEATEDFLOORS) == StayFilter.FLAG_HOTEL_FILTER_BED_HEATEDFLOORS)
        {
            updateBedTypeFilter(heatedFloorsCheckView, StayFilter.FLAG_HOTEL_FILTER_BED_HEATEDFLOORS);
        }
    }

    protected void initAmenitiesLayout(View view, StayCurationOption stayCurationOption)
    {
        mGridLayout = (android.support.v7.widget.GridLayout) view.findViewById(R.id.amenitiesGridLayout);

        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Integer flag = (Integer) v.getTag();

                if (flag == null)
                {
                    v.setSelected(false);
                    return;
                }

                StayCurationOption stayCurationOption = (StayCurationOption) mStayCuration.getCurationOption();

                if (v.isSelected() == true)
                {
                    v.setSelected(false);
                    stayCurationOption.flagAmenitiesFilters ^= flag;

                    AnalyticsManager.getInstance(StayCurationActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                        , AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_UNCLICKED, (String) v.getTag(v.getId()), null);
                } else
                {
                    v.setSelected(true);
                    stayCurationOption.flagAmenitiesFilters |= flag;

                    AnalyticsManager.getInstance(StayCurationActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                        , AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_CLICKED, (String) v.getTag(v.getId()), null);
                }

                requestUpdateResultDelayed();
            }
        };

        final String[] amenities = new String[]{getString(R.string.label_wifi)//
            , getString(R.string.label_breakfast)//
            , getString(R.string.label_cooking)//
            , getString(R.string.label_beth)//
            , getString(R.string.label_parking)//
            , getString(R.string.label_pool)//
            , getString(R.string.label_fitness)//
            , getString(R.string.label_allowed_pet)//
            , getString(R.string.label_allowed_barbecue)};

        final String[] analytics = new String[]{AnalyticsManager.Label.SORTFILTER_WIFI//
            , AnalyticsManager.Label.SORTFILTER_FREEBREAKFAST//
            , AnalyticsManager.Label.SORTFILTER_KITCHEN//
            , AnalyticsManager.Label.SORTFILTER_BATHTUB//
            , AnalyticsManager.Label.SORTFILTER_PARKINGAVAILABLE//
            , AnalyticsManager.Label.SORTFILTER_POOL//
            , AnalyticsManager.Label.SORTFILTER_FITNESS//
            , AnalyticsManager.Label.SORTFILTER_PET//
            , AnalyticsManager.Label.SORTFILTER_BBQ};

        final int[] amenitiesResId = new int[]{R.drawable.f_ic_hotel_04_facilities_01//
            , R.drawable.f_ic_hotel_04_facilities_02//
            , R.drawable.f_ic_hotel_04_facilities_03//
            , R.drawable.f_ic_hotel_04_facilities_04//
            , R.drawable.f_ic_hotel_04_facilities_05//
            , R.drawable.f_ic_hotel_04_facilities_06//
            , R.drawable.f_ic_hotel_04_facilities_07//
            , R.drawable.ic_detail_facilities_05_pet//
            , R.drawable.ic_detail_facilities_06_bbq};

        final int[] amenitiesFlag = new int[]{StayFilter.FLAG_HOTEL_FILTER_AMENITIES_WIFI//
            , StayFilter.FLAG_HOTEL_FILTER_AMENITIES_BREAKFAST//
            , StayFilter.FLAG_HOTEL_FILTER_AMENITIES_COOKING//
            , StayFilter.FLAG_HOTEL_FILTER_AMENITIES_BATH//
            , StayFilter.FLAG_HOTEL_FILTER_AMENITIES_PARKING//
            , StayFilter.FLAG_HOTEL_FILTER_AMENITIES_POOL//
            , StayFilter.FLAG_HOTEL_FILTER_AMENITIES_FITNESS//
            , StayFilter.FLAG_HOTEL_FILTER_AMENITIES_PET//
            , StayFilter.FLAG_HOTEL_FILTER_AMENITIES_SHAREDBBQ};

        int length = amenities.length;

        for (int i = 0; i < length; i++)
        {
            DailyTextView amenitiesView = getGridLayoutItemView(amenities[i], amenitiesResId[i], false);
            amenitiesView.setOnClickListener(onClickListener);
            amenitiesView.setTag(amenitiesFlag[i]);
            amenitiesView.setTag(amenitiesView.getId(), analytics[i]);
            amenitiesView.setDrawableVectorTintList(R.color.selector_svg_color_d929292_s900034_eeaeaea);

            if ((stayCurationOption.flagAmenitiesFilters & amenitiesFlag[i]) == amenitiesFlag[i])
            {
                amenitiesView.setSelected(true);
            }

            mGridLayout.addView(amenitiesView);
        }

        mGridLayout.setPadding(Util.dpToPx(this, 10), 0, Util.dpToPx(this, 10), Util.dpToPx(this, 10));
    }

    protected void updatePersonFilter(int person)
    {
        if (person < StayFilter.MIN_PERSON)
        {
            person = StayFilter.MIN_PERSON;
        } else if (person > StayFilter.MAX_PERSON)
        {
            person = StayFilter.MAX_PERSON;
        }

        StayCurationOption stayCurationOption = (StayCurationOption) mStayCuration.getCurationOption();
        stayCurationOption.person = person;

        mPersonCountView.setText(getString(R.string.label_more_person, person));

        if (person == StayFilter.MIN_PERSON)
        {
            mMinusPersonView.setEnabled(false);
            mPlusPersonView.setEnabled(true);
        } else if (person == StayFilter.MAX_PERSON)
        {
            mMinusPersonView.setEnabled(true);
            mPlusPersonView.setEnabled(false);
        } else
        {
            mMinusPersonView.setEnabled(true);
            mPlusPersonView.setEnabled(true);
        }

        requestUpdateResultDelayed();
    }

    private void updateBedTypeFilter(View view, int flag)
    {
        StayCurationOption stayCurationOption = (StayCurationOption) mStayCuration.getCurationOption();

        if (view.isSelected() == true)
        {
            view.setSelected(false);
            stayCurationOption.flagBedTypeFilters ^= flag;
        } else
        {
            view.setSelected(true);
            stayCurationOption.flagBedTypeFilters |= flag;
        }

        requestUpdateResultDelayed();
    }

    protected void resetCuration()
    {
        mStayCuration.getCurationOption().clear();

        if (mViewType == ViewType.LIST)
        {
            mSortRadioGroup.setOnCheckedChangeListener(null);
            mSortRadioGroup.check(R.id.regionCheckView);
            mSortRadioGroup.setOnCheckedChangeListener(this);
        }

        if (mStayCuration.getProvince().isOverseas == false)
        {
            updatePersonFilter(StayFilter.MIN_PERSON);

            resetLayout(mBedTypeLayout);
            resetLayout(mGridLayout);
        }

        requestUpdateResultDelayed();
    }

    @Override
    protected void requestUpdateResult()
    {
        setResultMessage(getResources().getString(R.string.label_searching));

        if (mStayCuration == null || mStayCuration.getCheckInSaleTime() == null)
        {
            Util.restartApp(StayCurationActivity.this);
            return;
        }

        setLastStayParams(mStayCuration);

        super.requestUpdateResult();
    }

    @Override
    protected void requestUpdateResultDelayed()
    {
        setResultMessage(getResources().getString(R.string.label_searching));

        if (mStayCuration == null || mStayCuration.getCheckInSaleTime() == null)
        {
            Util.restartApp(StayCurationActivity.this);
            return;
        }

        setLastStayParams(mStayCuration);

        super.requestUpdateResultDelayed();
    }

    @Override
    protected void updateResultMessage()
    {
        setConfirmOnClickListener(null);

        if (mLastParams != null && Constants.SortType.DISTANCE == mLastParams.getSortType() && mLastParams.hasLocation() == false)
        {
            onSearchLocationResult(null);
            return;
        }

        ((StayCurationNetworkController) mNetworkController).requestStayList(mLastParams);
    }

    protected void setLastStayParams(StayCuration stayCuration)
    {
        if (stayCuration == null)
        {
            return;
        }

        if (mLastParams == null)
        {
            mLastParams = new StayParams(stayCuration);
        } else
        {
            mLastParams.setPlaceParams(stayCuration);
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        AnalyticsManager.getInstance(this).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_CURATION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        unLockUI();

        switch (requestCode)
        {
            case Constants.CODE_REQUEST_ACTIVITY_PERMISSION_MANAGER:
            {
                if (resultCode == RESULT_OK)
                {
                    //                    checkedChangedDistance();
                    searchMyLocation();
                } else
                {
                    StayCurationOption stayCurationOption = (StayCurationOption) mStayCuration.getCurationOption();
                    SortType sortType;

                    if (stayCurationOption == null)
                    {
                        sortType = SortType.DEFAULT;
                    } else
                    {
                        sortType = stayCurationOption.getSortType();
                    }

                    switch (sortType)
                    {
                        case DEFAULT:
                            mSortRadioGroup.check(R.id.regionCheckView);
                            break;

                        case LOW_PRICE:
                            mSortRadioGroup.check(R.id.lowPriceCheckView);
                            break;

                        case HIGH_PRICE:
                            mSortRadioGroup.check(R.id.highPriceCheckView);
                            break;

                        case SATISFACTION:
                            mSortRadioGroup.check(R.id.satisfactionCheckView);
                            break;

                        // 거리 소트을 요청하였으나 동의를 하지 않는 경우 다시 거리 소트로 돌아오는 경우 종료시킨다.
                        case DISTANCE:
                            finish();
                            break;
                    }
                }
                break;
            }

            case Constants.CODE_RESULT_ACTIVITY_SETTING_LOCATION:
            {
                searchMyLocation();
                break;
            }
        }
    }

    // Sort filter
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId)
    {
        RadioButton radioButton = (RadioButton) group.findViewById(checkedId);

        if (radioButton == null)
        {
            return;
        }

        String label;

        boolean isChecked = radioButton.isChecked();

        if (isChecked == false)
        {
            return;
        }

        StayCurationOption stayCurationOption = (StayCurationOption) mStayCuration.getCurationOption();

        switch (checkedId)
        {
            case R.id.regionCheckView:
                stayCurationOption.setSortType(SortType.DEFAULT);
                label = AnalyticsManager.Label.SORTFILTER_DISTRICT;
                break;

            case R.id.distanceCheckView:
                searchMyLocation();
                return;

            case R.id.lowPriceCheckView:
                stayCurationOption.setSortType(SortType.LOW_PRICE);
                label = AnalyticsManager.Label.SORTFILTER_LOWTOHIGHPRICE;
                break;

            case R.id.highPriceCheckView:
                stayCurationOption.setSortType(SortType.HIGH_PRICE);
                label = AnalyticsManager.Label.SORTFILTER_HIGHTOLOWPRICE;
                break;

            case R.id.satisfactionCheckView:
                stayCurationOption.setSortType(SortType.SATISFACTION);
                label = AnalyticsManager.Label.SORTFILTER_RATING;
                break;

            default:
                return;
        }

        try
        {
            Province province = mStayCuration.getProvince();
            Map<String, String> eventParams = new HashMap<>();

            if (province != null)
            {
                if (province instanceof Area)
                {
                    Area area = (Area) province;
                    eventParams.put(AnalyticsManager.KeyType.COUNTRY, area.getProvince().isOverseas ? AnalyticsManager.KeyType.OVERSEAS : AnalyticsManager.KeyType.DOMESTIC);
                    eventParams.put(AnalyticsManager.KeyType.PROVINCE, area.getProvince().name);
                    eventParams.put(AnalyticsManager.KeyType.DISTRICT, area.name);
                } else
                {
                    eventParams.put(AnalyticsManager.KeyType.COUNTRY, province.isOverseas ? AnalyticsManager.KeyType.OVERSEAS : AnalyticsManager.KeyType.DOMESTIC);
                    eventParams.put(AnalyticsManager.KeyType.PROVINCE, province.name);
                    eventParams.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.ALL_LOCALE_KR);
                }
            }

            AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                , AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_CLICKED, label, eventParams);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    @Override
    public void onClick(View v)
    {
        super.onClick(v);

        StayCurationOption stayCurationOption = (StayCurationOption) mStayCuration.getCurationOption();

        switch (v.getId())
        {
            case R.id.minusPersonView:
                updatePersonFilter(stayCurationOption.person - 1);
                break;

            case R.id.plusPersonView:
                updatePersonFilter(stayCurationOption.person + 1);
                break;

            case R.id.doubleCheckView:
                updateBedTypeFilter(v, StayFilter.FLAG_HOTEL_FILTER_BED_DOUBLE);

                AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                    , v.isSelected() ? AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_CLICKED : AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_UNCLICKED//
                    , AnalyticsManager.Label.SORTFILTER_DOUBLE, null);
                break;

            case R.id.twinCheckView:
                updateBedTypeFilter(v, StayFilter.FLAG_HOTEL_FILTER_BED_TWIN);

                AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                    , v.isSelected() ? AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_CLICKED : AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_UNCLICKED//
                    , AnalyticsManager.Label.SORTFILTER_TWIN, null);
                break;

            case R.id.heatedFloorsCheckView:
                updateBedTypeFilter(v, StayFilter.FLAG_HOTEL_FILTER_BED_HEATEDFLOORS);

                AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                    , v.isSelected() ? AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_CLICKED : AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_UNCLICKED//
                    , AnalyticsManager.Label.SORTFILTER_ONDOL, null);
                break;
        }
    }

    @Override
    protected void onComplete()
    {
        StayCurationOption stayCurationOption = (StayCurationOption) mStayCuration.getCurationOption();

        Intent intent = new Intent();
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACECURATION, mStayCuration);

        setResult(RESULT_OK, intent);
        hideAnimation();

        Province province = mStayCuration.getProvince();
        Map<String, String> eventParams = new HashMap<>();

        eventParams.put(AnalyticsManager.KeyType.SORTING, stayCurationOption.getSortType().name());

        if (province != null)
        {
            if (province instanceof Area)
            {
                Area area = (Area) province;
                eventParams.put(AnalyticsManager.KeyType.COUNTRY, area.getProvince().isOverseas ? AnalyticsManager.KeyType.OVERSEAS : AnalyticsManager.KeyType.DOMESTIC);
                eventParams.put(AnalyticsManager.KeyType.PROVINCE, area.getProvince().name);
                eventParams.put(AnalyticsManager.KeyType.DISTRICT, area.name);
            } else
            {
                eventParams.put(AnalyticsManager.KeyType.COUNTRY, province.isOverseas ? AnalyticsManager.KeyType.OVERSEAS : AnalyticsManager.KeyType.DOMESTIC);
                eventParams.put(AnalyticsManager.KeyType.PROVINCE, province.name);
                eventParams.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.ALL_LOCALE_KR);
            }
        }

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
            , AnalyticsManager.Action.HOTEL_SORT_FILTER_APPLY_BUTTON_CLICKED, stayCurationOption.toString(), eventParams);

        if (Constants.DEBUG == true)
        {
            ExLog.d(stayCurationOption.toString());
        }
    }

    @Override
    protected void onCancel()
    {
        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
            , AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_CLICKED, AnalyticsManager.Label.CLOSE_BUTTON_CLICKED, null);

        hideAnimation();
    }

    @Override
    protected void onReset()
    {
        resetCuration();

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
            , AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_CLICKED, AnalyticsManager.Label.RESET_BUTTON_CLICKED, null);
    }

    @Override
    protected void onSearchLocationResult(Location location)
    {
        mStayCuration.setLocation(location);

        if (location == null)
        {
            DailyToast.showToast(StayCurationActivity.this, R.string.message_failed_mylocation, Toast.LENGTH_SHORT);
            mSortRadioGroup.check(R.id.regionCheckView);
        } else
        {
            checkedChangedDistance();
        }
    }

    @Override
    protected BaseNetworkController getNetworkController(Context context)
    {
        return new StayCurationNetworkController(context, mNetworkTag, mNetworkControllerListener);
    }

    @Override
    protected PlaceCuration getPlaceCuration()
    {
        return mStayCuration;
    }

    private void checkedChangedDistance()
    {
        StayCurationOption stayCurationOption = (StayCurationOption) mStayCuration.getCurationOption();
        stayCurationOption.setSortType(SortType.DISTANCE);

        Province province = mStayCuration.getProvince();
        Map<String, String> eventParams = new HashMap<>();
        String label = AnalyticsManager.Label.SORTFILTER_DISTANCE;

        if (province != null)
        {
            if (province instanceof Area)
            {
                Area area = (Area) province;
                eventParams.put(AnalyticsManager.KeyType.COUNTRY, area.getProvince().isOverseas ? AnalyticsManager.KeyType.OVERSEAS : AnalyticsManager.KeyType.DOMESTIC);
                eventParams.put(AnalyticsManager.KeyType.PROVINCE, area.getProvince().name);
                eventParams.put(AnalyticsManager.KeyType.DISTRICT, area.name);
            } else
            {
                eventParams.put(AnalyticsManager.KeyType.COUNTRY, province.isOverseas ? AnalyticsManager.KeyType.OVERSEAS : AnalyticsManager.KeyType.DOMESTIC);
                eventParams.put(AnalyticsManager.KeyType.PROVINCE, province.name);
                eventParams.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.ALL_LOCALE_KR);
            }
        }

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
            , AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_CLICKED, label, eventParams);
    }

    private StayCurationNetworkController.OnNetworkControllerListener mNetworkControllerListener = new StayCurationNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onStayCount(String url, int hotelSaleCount)
        {
            if (Util.isTextEmpty(url) == true && hotelSaleCount == -1)
            {
                // OnNetworkControllerListener onErrorResponse
                setResultMessage(getString(R.string.label_hotel_filter_result_empty));

                setConfirmOnClickListener(StayCurationActivity.this);
                setConfirmEnable(false);
                return;
            }

            String requestParams = null;
            try
            {
                Uri requestUrl = Uri.parse(url);
                requestParams = requestUrl.getQuery();
            } catch (Exception e)
            {
                // do nothing!
            }

            String lastParams = mLastParams.toParamsString();
            if (lastParams.equalsIgnoreCase(requestParams) == false)
            {
                // already running another request!
                return;
            }

            if (hotelSaleCount <= 0)
            {
                setResultMessage(getString(R.string.label_hotel_filter_result_empty));
            } else
            {
                setResultMessage(getString(R.string.label_hotel_filter_result_count, hotelSaleCount));
            }

            setConfirmOnClickListener(StayCurationActivity.this);
            setConfirmEnable(hotelSaleCount != 0);
        }

        @Override
        public void onError(Throwable e)
        {
            StayCurationActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            StayCurationActivity.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            StayCurationActivity.this.onErrorToastMessage(message);
        }

        @Override
        public void onErrorResponse(Call call, Response response)
        {
            StayCurationActivity.this.onErrorResponse(call, response);
        }
    };
}