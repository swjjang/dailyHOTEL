package com.twoheart.dailyhotel.screen.hotel.filter;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Category;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.StayCurationOption;
import com.twoheart.dailyhotel.model.StayFilter;
import com.twoheart.dailyhotel.model.StayParams;
import com.twoheart.dailyhotel.place.activity.PlaceCurationActivity;
import com.twoheart.dailyhotel.screen.common.PermissionManagerActivity;
import com.twoheart.dailyhotel.screen.hotel.list.StayCurationManager;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyTextView;

import java.util.HashMap;
import java.util.Map;

public class StayCurationActivity extends PlaceCurationActivity implements RadioGroup.OnCheckedChangeListener
{
    public static final String INTENT_EXTRA_DATA_CURATION_OPTIONS = "curationOptions";
    public static final String INTENT_EXTRA_DATA_VIEWTYPE = "viewType";
    public static final String INTENT_EXTRA_DATA_CATEGORY = "category";
    public static final String INTENT_EXTRA_DATA_PROVINCE = "province";

    private StayCurationOption mStayCurationOption;
    private Category mCategory;
    private Province mProvince;

    private boolean mIsGlobal;
    private ViewType mViewType;

    private StayCurationNetworkController mNetworkController;

    private RadioGroup mSortRadioGroup;
    private android.support.v7.widget.GridLayout mGridLayout;

    private View mMinusPersonView;
    private View mPlusPersonView;
    private TextView mPersonCountView;
    private ViewGroup mBedTypeLayout;

    public static Intent newInstance(Context context, boolean isGlobal, ViewType viewType, //
                                     StayCurationOption stayCurationOption, Category category, Province province)
    {
        Intent intent = new Intent(context, StayCurationActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_REGION, isGlobal);
        intent.putExtra(INTENT_EXTRA_DATA_VIEWTYPE, viewType.name());
        intent.putExtra(INTENT_EXTRA_DATA_CURATION_OPTIONS, stayCurationOption);
        intent.putExtra(INTENT_EXTRA_DATA_CATEGORY, category);
        intent.putExtra(INTENT_EXTRA_DATA_PROVINCE, province);

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

        mIsGlobal = intent.getBooleanExtra(NAME_INTENT_EXTRA_DATA_REGION, false);
        mViewType = ViewType.valueOf(intent.getStringExtra(INTENT_EXTRA_DATA_VIEWTYPE));
        mStayCurationOption = intent.getParcelableExtra(INTENT_EXTRA_DATA_CURATION_OPTIONS);
        mCategory = intent.getParcelableExtra(INTENT_EXTRA_DATA_CATEGORY);
        mProvince = intent.getParcelableExtra(INTENT_EXTRA_DATA_PROVINCE);

        mNetworkController = new StayCurationNetworkController(this, mNetworkTag, mNetworkControllerListener);

        initLayout();

        mAnimationLayout.setVisibility(View.INVISIBLE);
        mAnimationLayout.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                showAnimation();
            }
        }, 50);
    }

    @Override
    protected void initContentLayout(ViewGroup contentLayout)
    {
        View sortLayout = LayoutInflater.from(this).inflate(R.layout.layout_hotel_sort, null);
        initSortLayout(sortLayout, mViewType, mStayCurationOption);

        contentLayout.addView(sortLayout);

        if (mIsGlobal == false)
        {
            View filterLayout = LayoutInflater.from(this).inflate(R.layout.layout_hotel_filter, null);
            initFilterLayout(filterLayout, mStayCurationOption);

            contentLayout.addView(filterLayout);

            initAmenitiesLayout(filterLayout, mStayCurationOption);
        } else
        {
            requestUpdateResult();
        }
    }

    private void initSortLayout(View view, ViewType viewType, StayCurationOption stayCurationOption)
    {
        mSortRadioGroup = (RadioGroup) view.findViewById(R.id.sortLayout);

        if (mIsGlobal == true)
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

    private void initFilterLayout(View view, StayCurationOption stayCurationOption)
    {
        // 인원
        mMinusPersonView = view.findViewById(R.id.minusPersonView);
        mPlusPersonView = view.findViewById(R.id.plusPersonView);

        View minusDimView = view.findViewById(R.id.minusDimView);
        View plusDimView = view.findViewById(R.id.plusDimView);

        mMinusPersonView.setTag(minusDimView);
        mPlusPersonView.setTag(plusDimView);

        mPersonCountView = (TextView) view.findViewById(R.id.personCountView);

        mMinusPersonView.setOnClickListener(this);
        mPlusPersonView.setOnClickListener(this);

        updatePersonFilter(stayCurationOption.person);

        // 베드타입
        mBedTypeLayout = (ViewGroup) view.findViewById(R.id.bedTypeLayout);
        View doubleCheckView = view.findViewById(R.id.doubleCheckView);
        View twinCheckView = view.findViewById(R.id.twinCheckView);
        View heatedFloorsCheckView = view.findViewById(R.id.heatedFloorsCheckView);

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

    private void initAmenitiesLayout(View view, StayCurationOption stayCurationOption)
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

                if (v.isSelected() == true)
                {
                    v.setSelected(false);
                    mStayCurationOption.flagAmenitiesFilters ^= flag;

                    AnalyticsManager.getInstance(StayCurationActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                        , AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_UNCLICKED, (String) v.getTag(v.getId()), null);
                } else
                {
                    v.setSelected(true);
                    mStayCurationOption.flagAmenitiesFilters |= flag;

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
            , getString(R.string.label_fitness)};

        final String[] analytics = new String[]{AnalyticsManager.Label.SORTFILTER_WIFI//
            , AnalyticsManager.Label.SORTFILTER_FREEBREAKFAST//
            , AnalyticsManager.Label.SORTFILTER_KITCHEN//
            , AnalyticsManager.Label.SORTFILTER_BATHTUB//
            , AnalyticsManager.Label.SORTFILTER_PARKINGAVAILABEL//
            , AnalyticsManager.Label.SORTFILTER_POOL//
            , AnalyticsManager.Label.SORTFILTER_FITNESS};

        final int[] amenitiesResId = new int[]{R.drawable.selector_filter_amenities_wifi_button//
            , R.drawable.selector_filter_amenities_breakfast_button//
            , R.drawable.selector_filter_amenities_cooking_button//
            , R.drawable.selector_filter_amenities_bath_button//
            , R.drawable.selector_filter_amenities_parking_button//
            , R.drawable.selector_filter_amenities_pool_button//
            , R.drawable.selector_filter_amenities_fitness_button};

        final int[] amenitiesflag = new int[]{StayFilter.FLAG_HOTEL_FILTER_AMENITIES_WIFI//
            , StayFilter.FLAG_HOTEL_FILTER_AMENITIES_BREAKFAST//
            , StayFilter.FLAG_HOTEL_FILTER_AMENITIES_COOKING//
            , StayFilter.FLAG_HOTEL_FILTER_AMENITIES_BATH//
            , StayFilter.FLAG_HOTEL_FILTER_AMENITIES_PARKING//
            , StayFilter.FLAG_HOTEL_FILTER_AMENITIES_POOL//
            , StayFilter.FLAG_HOTEL_FILTER_AMENITIES_FITNESS};

        int length = amenities.length;

        for (int i = 0; i < length; i++)
        {
            DailyTextView amenitiesView = getGridLayoutItemView(amenities[i], amenitiesResId[i], false);
            amenitiesView.setOnClickListener(onClickListener);
            amenitiesView.setTag(amenitiesflag[i]);
            amenitiesView.setTag(amenitiesView.getId(), analytics[i]);

            if ((stayCurationOption.flagAmenitiesFilters & amenitiesflag[i]) == amenitiesflag[i])
            {
                amenitiesView.setSelected(true);
            }

            mGridLayout.addView(amenitiesView);
        }

        mGridLayout.setPadding(Util.dpToPx(this, 10), 0, Util.dpToPx(this, 10), Util.dpToPx(this, 10));
    }

    private void updatePersonFilter(int person)
    {
        if (person < StayFilter.MIN_PERSON)
        {
            person = StayFilter.MIN_PERSON;
        } else if (person > StayFilter.MAX_PERSON)
        {
            person = StayFilter.MAX_PERSON;
        }

        mStayCurationOption.person = person;

        mPersonCountView.setText(getString(R.string.label_more_person, person));

        View minusDimview = (View) mMinusPersonView.getTag();
        View plusDimview = (View) mPlusPersonView.getTag();

        if (person == StayFilter.MIN_PERSON)
        {
            mMinusPersonView.setEnabled(false);
            mPlusPersonView.setEnabled(true);
            minusDimview.setVisibility(View.VISIBLE);
            plusDimview.setVisibility(View.GONE);
        } else if (person == StayFilter.MAX_PERSON)
        {
            mMinusPersonView.setEnabled(true);
            mPlusPersonView.setEnabled(false);
            minusDimview.setVisibility(View.GONE);
            plusDimview.setVisibility(View.VISIBLE);
        } else
        {
            mMinusPersonView.setEnabled(true);
            mPlusPersonView.setEnabled(true);
            minusDimview.setVisibility(View.GONE);
            plusDimview.setVisibility(View.GONE);
        }

        requestUpdateResultDelayed();
    }

    private void updateBedTypeFilter(View view, int flag)
    {
        if (view.isSelected() == true)
        {
            view.setSelected(false);
            mStayCurationOption.flagBedTypeFilters ^= flag;
        } else
        {
            view.setSelected(true);
            mStayCurationOption.flagBedTypeFilters |= flag;
        }

        requestUpdateResultDelayed();
    }

    private void resetCuration()
    {
        mStayCurationOption.clear();

        if (mViewType == ViewType.LIST)
        {
            mSortRadioGroup.setOnCheckedChangeListener(null);
            mSortRadioGroup.check(R.id.regionCheckView);
            mSortRadioGroup.setOnCheckedChangeListener(this);
        }

        if (mIsGlobal == false)
        {
            updatePersonFilter(StayFilter.MIN_PERSON);

            resetLayout(mBedTypeLayout);
            resetLayout(mGridLayout);
        }

        requestUpdateResult();
    }


    private StayParams getStayParams()
    {
        StayParams params = new StayParams();

        params.dateCheckIn = StayCurationManager.getInstance().getCheckInSaleTime().getDayOfDaysDateFormat("yyyy-MM-dd");
        params.stays = StayCurationManager.getInstance().getNights();
        params.provinceIdx = mProvince.getProvinceIndex();

        if (mProvince instanceof Area)
        {
            Area area = (Area) mProvince;
            if (area != null)
            {
                params.areaIdx = area.index;
            }
        }

        params.persons = mStayCurationOption.person;
        params.category = mCategory;
        params.bedType = mStayCurationOption.getParamStringByBedTypes(); // curationOption에서 가져온 스트링
        params.luxury = mStayCurationOption.getParamStingByAmenities(); // curationOption에서 가져온 스트링

        Constants.SortType sortType = mStayCurationOption.getSortType();
        if (Constants.SortType.DISTANCE == sortType)
        {
            Location location = StayCurationManager.getInstance().getLocation();
            if (location != null)
            {
                params.latitude = location.getLatitude();
                params.longitude = location.getLongitude();
            }
        }

        params.page = 0;
        params.limit = 0;
        params.setSortType(sortType);
        params.details = false;

        return params;
    }

    @Override
    protected void updateResultMessage()
    {
        setConfirmOnClickListener(null);

        mNetworkController.requestStayList(getStayParams());
        //        new AsyncTask<Void, Void, Integer>()
        //        {
        //            @Override
        //            protected Integer doInBackground(Void... params)
        //            {
        //                int count = 0;
        //                ArrayList<StayFilters> hotelFiltersList = mStayCurationOption.getFiltersList();
        //
        //                if (Category.ALL.code.equalsIgnoreCase(mCategory.code) == true)
        //                {
        //                    for (StayFilters hotelFilters : hotelFiltersList)
        //                    {
        //                        if (hotelFilters.isFiltered(mStayCurationOption) == true)
        //                        {
        //                            count++;
        //                        }
        //                    }
        //                } else
        //                {
        //                    for (StayFilters hotelFilters : hotelFiltersList)
        //                    {
        //                        if (mCategory.code.equalsIgnoreCase(hotelFilters.categoryCode) == true//
        //                            && hotelFilters.isFiltered(mStayCurationOption) == true)
        //                        {
        //                            count++;
        //                        }
        //                    }
        //                }
        //
        //                return count;
        //            }
        //
        //            @Override
        //            protected void onPostExecute(Integer count)
        //            {
        //                setResultMessage(getString(R.string.label_hotel_filter_result_count, count));
        //
        //                setConfirmOnClickListener(StayCurationActivity.this);
        //                setConfirmEnable(count == 0 ? false : true);
        //            }
        //        }.execute();
    }

    private void setDisabledSortLayout(View view, RadioGroup sortLayout)
    {
        if (sortLayout == null)
        {
            return;
        }

        sortLayout.setEnabled(false);

        int childCount = sortLayout.getChildCount();

        for (int i = 0; i < childCount; i++)
        {
            sortLayout.getChildAt(i).setEnabled(false);
        }

        View dimView = view.findViewById(R.id.dimView);
        dimView.setVisibility(View.VISIBLE);
    }

    private void resetLayout(ViewGroup viewGroup)
    {
        if (viewGroup == null)
        {
            return;
        }

        int childCount = viewGroup.getChildCount();

        for (int i = 0; i < childCount; i++)
        {
            viewGroup.getChildAt(i).setSelected(false);
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
                    checkedChangedDistance();
                } else
                {
                    switch (mStayCurationOption.getSortType())
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
                    }
                }
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

        switch (checkedId)
        {
            case R.id.regionCheckView:
                mStayCurationOption.setSortType(SortType.DEFAULT);
                label = AnalyticsManager.Label.SORTFILTER_DISTRICT;
                break;

            case R.id.distanceCheckView:
            {
                Intent intent = PermissionManagerActivity.newInstance(this, PermissionManagerActivity.PermissionType.ACCESS_FINE_LOCATION);
                startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_PERMISSION_MANAGER);
                return;
            }

            case R.id.lowPriceCheckView:
                mStayCurationOption.setSortType(SortType.LOW_PRICE);
                label = AnalyticsManager.Label.SORTFILTER_LOWTOHIGHPRICE;
                break;

            case R.id.highPriceCheckView:
                mStayCurationOption.setSortType(SortType.HIGH_PRICE);
                label = AnalyticsManager.Label.SORTFILTER_HIGHTOLOWPRICE;
                break;

            case R.id.satisfactionCheckView:
                mStayCurationOption.setSortType(SortType.SATISFACTION);
                label = AnalyticsManager.Label.SORTFILTER_RATING;
                break;

            default:
                return;
        }

        Map<String, String> eventParams = new HashMap<>();

        if (mProvince instanceof Area)
        {
            Area area = (Area) mProvince;
            eventParams.put(AnalyticsManager.KeyType.COUNTRY, area.getProvince().isOverseas ? AnalyticsManager.KeyType.OVERSEAS : AnalyticsManager.KeyType.DOMESTIC);
            eventParams.put(AnalyticsManager.KeyType.PROVINCE, area.getProvince().name);
            eventParams.put(AnalyticsManager.KeyType.DISTRICT, area.name);
        } else
        {
            eventParams.put(AnalyticsManager.KeyType.COUNTRY, mProvince.isOverseas ? AnalyticsManager.KeyType.OVERSEAS : AnalyticsManager.KeyType.DOMESTIC);
            eventParams.put(AnalyticsManager.KeyType.PROVINCE, mProvince.name);
            eventParams.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.EMPTY);
        }

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
            , AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_CLICKED, label, eventParams);
    }

    @Override
    public void onClick(View v)
    {
        super.onClick(v);

        switch (v.getId())
        {
            case R.id.minusPersonView:
                updatePersonFilter(mStayCurationOption.person - 1);

                AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                    , AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_CLICKED, Integer.toString(mStayCurationOption.person), null);
                break;

            case R.id.plusPersonView:
                updatePersonFilter(mStayCurationOption.person + 1);

                AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                    , AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_CLICKED, Integer.toString(mStayCurationOption.person), null);
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
        Map<String, String> eventParams = new HashMap<>();

        eventParams.put(AnalyticsManager.KeyType.SORTING, mStayCurationOption.getSortType().name());

        if (mProvince instanceof Area)
        {
            Area area = (Area) mProvince;
            eventParams.put(AnalyticsManager.KeyType.COUNTRY, area.getProvince().isOverseas ? AnalyticsManager.KeyType.OVERSEAS : AnalyticsManager.KeyType.DOMESTIC);
            eventParams.put(AnalyticsManager.KeyType.PROVINCE, area.getProvince().name);
            eventParams.put(AnalyticsManager.KeyType.DISTRICT, area.name);
        } else
        {
            eventParams.put(AnalyticsManager.KeyType.COUNTRY, mProvince.isOverseas ? AnalyticsManager.KeyType.OVERSEAS : AnalyticsManager.KeyType.DOMESTIC);
            eventParams.put(AnalyticsManager.KeyType.PROVINCE, mProvince.name);
            eventParams.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.EMPTY);
        }

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
            , AnalyticsManager.Action.HOTEL_SORT_FILTER_APPLY_BUTTON_CLICKED, mStayCurationOption.toString(), eventParams);

        if (DEBUG == true)
        {
            ExLog.d(mStayCurationOption.toString());
        }

        Intent intent = new Intent();
        intent.putExtra(INTENT_EXTRA_DATA_CURATION_OPTIONS, mStayCurationOption);
        intent.putExtra(INTENT_EXTRA_DATA_CATEGORY, mCategory);
        intent.putExtra(INTENT_EXTRA_DATA_PROVINCE, mProvince);

        setResult(RESULT_OK, intent);
        hideAnimation();
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

    private void checkedChangedDistance()
    {
        mStayCurationOption.setSortType(SortType.DISTANCE);
        String label = AnalyticsManager.Label.SORTFILTER_DISTANCE;

        Map<String, String> eventParams = new HashMap<>();

        if (mProvince instanceof Area)
        {
            Area area = (Area) mProvince;
            eventParams.put(AnalyticsManager.KeyType.COUNTRY, area.getProvince().isOverseas ? AnalyticsManager.KeyType.OVERSEAS : AnalyticsManager.KeyType.DOMESTIC);
            eventParams.put(AnalyticsManager.KeyType.PROVINCE, area.getProvince().name);
            eventParams.put(AnalyticsManager.KeyType.DISTRICT, area.name);
        } else
        {
            eventParams.put(AnalyticsManager.KeyType.COUNTRY, mProvince.isOverseas ? AnalyticsManager.KeyType.OVERSEAS : AnalyticsManager.KeyType.DOMESTIC);
            eventParams.put(AnalyticsManager.KeyType.PROVINCE, mProvince.name);
            eventParams.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.EMPTY);
        }

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
            , AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_CLICKED, label, eventParams);
    }


    private StayCurationNetworkController.OnNetworkControllerListener mNetworkControllerListener = new StayCurationNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onStayCount(int hotelSaleCount)
        {
            setResultMessage(getString(R.string.label_hotel_filter_result_count, hotelSaleCount));

            setConfirmOnClickListener(StayCurationActivity.this);
            setConfirmEnable(hotelSaleCount == 0 ? false : true);
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            StayCurationActivity.this.onErrorResponse(volleyError);
        }

        @Override
        public void onError(Exception e)
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
    };
}