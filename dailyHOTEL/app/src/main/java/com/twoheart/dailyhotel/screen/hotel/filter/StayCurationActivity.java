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

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyTextView;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.entity.AreaElement;
import com.daily.dailyhotel.entity.StayArea;
import com.daily.dailyhotel.entity.StayRegion;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceCuration;
import com.twoheart.dailyhotel.model.StayAmenities;
import com.twoheart.dailyhotel.model.StayCuration;
import com.twoheart.dailyhotel.model.StayCurationOption;
import com.twoheart.dailyhotel.model.StayFilter;
import com.twoheart.dailyhotel.model.StayParams;
import com.twoheart.dailyhotel.model.StayRoomAmenities;
import com.twoheart.dailyhotel.place.activity.PlaceCurationActivity;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

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
    protected android.support.v7.widget.GridLayout mAmenitiesGridLayout;
    protected android.support.v7.widget.GridLayout mInRoomAmenitiesGridLayout;

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
    }

    protected void initIntent(Intent intent)
    {
        try
        {
            mViewType = ViewType.valueOf(intent.getStringExtra(INTENT_EXTRA_DATA_VIEWTYPE));
            mStayCuration = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PLACECURATION);
        } catch (Exception e)
        {
            ExLog.d(e.toString());

            Util.restartApp(this);
        }
    }

    @Override
    protected void initContentLayout(ViewGroup contentLayout)
    {
        StayCurationOption stayCurationOption = (StayCurationOption) mStayCuration.getCurationOption();

        View sortLayout = LayoutInflater.from(this).inflate(R.layout.layout_hotel_sort, null);
        initSortLayout(sortLayout, mViewType, stayCurationOption);

        contentLayout.addView(sortLayout);

        // 해외 지역 삭제
        //        if (mStayCuration.getProvince().isOverseas == false)
        //        {
        View filterLayout = LayoutInflater.from(this).inflate(R.layout.layout_hotel_filter, null);
        initFilterLayout(filterLayout, stayCurationOption);

        initAmenitiesLayout(filterLayout, stayCurationOption);

        initInRoomAmenitiesLayout(filterLayout, stayCurationOption);

        contentLayout.addView(filterLayout);
        //        } else
        //        {
        //            requestUpdateResult();
        //        }
    }

    protected void initSortLayout(View view, ViewType viewType, StayCurationOption stayCurationOption)
    {
        mSortRadioGroup = view.findViewById(R.id.sortLayout);

        // 해외 지역 삭제
        //        if (mStayCuration.getProvince().isOverseas == true)
        //        {
        //            View satisfactionCheckView = view.findViewById(R.id.satisfactionCheckView);
        //            satisfactionCheckView.setVisibility(View.INVISIBLE);
        //        }

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

        mPersonCountView = view.findViewById(R.id.personCountView);

        mMinusPersonView.setOnClickListener(this);
        mPlusPersonView.setOnClickListener(this);

        updatePersonFilter(stayCurationOption.person);

        // 베드타입
        mBedTypeLayout = view.findViewById(R.id.bedTypeLayout);
        DailyTextView doubleCheckView = view.findViewById(R.id.doubleCheckView);
        DailyTextView twinCheckView = view.findViewById(R.id.twinCheckView);
        DailyTextView heatedFloorsCheckView = view.findViewById(R.id.heatedFloorsCheckView);

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
        mAmenitiesGridLayout = view.findViewById(R.id.amenitiesGridLayout);

        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                StayAmenities stayAmenities = (StayAmenities) v.getTag();

                if (stayAmenities == null)
                {
                    v.setSelected(false);
                    return;
                }

                StayCurationOption stayCurationOption = (StayCurationOption) mStayCuration.getCurationOption();

                if (v.isSelected() == true)
                {
                    v.setSelected(false);
                    stayCurationOption.flagAmenitiesFilters ^= stayAmenities.getFlag();
                } else
                {
                    v.setSelected(true);
                    stayCurationOption.flagAmenitiesFilters |= stayAmenities.getFlag();
                }

                requestUpdateResultDelayed();
            }
        };

        StayAmenities[] values = StayAmenities.values();
        if (values != null && values.length > 0)
        {
            for (StayAmenities stayAmenities : values)
            {
                DailyTextView amenitiesView = getGridLayoutItemView(stayAmenities.getName(this), stayAmenities.getResId());
                amenitiesView.setOnClickListener(onClickListener);
                amenitiesView.setTag(stayAmenities);
                amenitiesView.setDrawableVectorTintList(R.color.selector_svg_color_dababab_sb70038_eeaeaea);

                if ((stayCurationOption.flagAmenitiesFilters & stayAmenities.getFlag()) == stayAmenities.getFlag())
                {
                    amenitiesView.setSelected(true);
                }

                mAmenitiesGridLayout.addView(amenitiesView);
            }
        }
    }

    protected void initInRoomAmenitiesLayout(View view, StayCurationOption stayCurationOption)
    {
        mInRoomAmenitiesGridLayout = view.findViewById(R.id.inRoomAmenitiesGridLayout);

        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                StayRoomAmenities stayRoomAmenities = (StayRoomAmenities) v.getTag();

                if (stayRoomAmenities == null)
                {
                    v.setSelected(false);
                    return;
                }

                StayCurationOption stayCurationOption = (StayCurationOption) mStayCuration.getCurationOption();

                if (v.isSelected() == true)
                {
                    v.setSelected(false);
                    stayCurationOption.flagRoomAmenitiesFilters ^= stayRoomAmenities.getFlag();
                } else
                {
                    v.setSelected(true);
                    stayCurationOption.flagRoomAmenitiesFilters |= stayRoomAmenities.getFlag();
                }

                requestUpdateResultDelayed();
            }
        };

        StayRoomAmenities[] values = StayRoomAmenities.values();
        if (values != null && values.length > 0)
        {
            for (StayRoomAmenities stayRoomAmenities : values)
            {
                DailyTextView amenitiesView = getGridLayoutItemView(stayRoomAmenities.getName(this), stayRoomAmenities.getResId());
                amenitiesView.setOnClickListener(onClickListener);
                amenitiesView.setTag(stayRoomAmenities);
                amenitiesView.setDrawableVectorTintList(R.color.selector_svg_color_dababab_sb70038_eeaeaea);

                if ((stayCurationOption.flagRoomAmenitiesFilters & stayRoomAmenities.getFlag()) == stayRoomAmenities.getFlag())
                {
                    amenitiesView.setSelected(true);
                }

                mInRoomAmenitiesGridLayout.addView(amenitiesView);
            }
        }

        mInRoomAmenitiesGridLayout.setPadding(ScreenUtils.dpToPx(this, 10), 0, ScreenUtils.dpToPx(this, 10), ScreenUtils.dpToPx(this, 5));
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

        // 해외 지역 삭제
        //        if (mStayCuration.getProvince().isOverseas == false)
        //        {
        updatePersonFilter(StayFilter.DEFAULT_PERSON);

        resetLayout(mBedTypeLayout);
        resetLayout(mAmenitiesGridLayout);
        //            resetLayout(mInRoomAmenitiesGridLayout);
        //        }

        requestUpdateResultDelayed();
    }

    @Override
    protected void requestUpdateResult()
    {
        setResultMessage(getResources().getString(R.string.label_searching));

        if (mStayCuration == null)
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

        if (mStayCuration == null)
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

        String abTestType = DailyRemoteConfigPreference.getInstance(this).getKeyRemoteConfigStayRankTestType();

        ((StayCurationNetworkController) mNetworkController).requestStayList(mLastParams, abTestType);
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

        AnalyticsManager.getInstance(this).recordScreen(this, AnalyticsManager.Screen.DAILYHOTEL_CURATION, null);
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
                switch (resultCode)
                {
                    case RESULT_OK:
                        searchMyLocation();
                        break;

                    case CODE_RESULT_ACTIVITY_GO_HOME:
                        setResult(resultCode);
                        finish();
                        break;

                    default:
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
                        break;
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
        RadioButton radioButton = group.findViewById(checkedId);

        if (radioButton == null)
        {
            return;
        }

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
                break;

            case R.id.distanceCheckView:
                searchMyLocation();
                return;

            case R.id.lowPriceCheckView:
                stayCurationOption.setSortType(SortType.LOW_PRICE);
                break;

            case R.id.highPriceCheckView:
                stayCurationOption.setSortType(SortType.HIGH_PRICE);
                break;

            case R.id.satisfactionCheckView:
                stayCurationOption.setSortType(SortType.SATISFACTION);
                break;

            default:
                return;
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
                break;

            case R.id.twinCheckView:
                updateBedTypeFilter(v, StayFilter.FLAG_HOTEL_FILTER_BED_TWIN);
                break;

            case R.id.heatedFloorsCheckView:
                updateBedTypeFilter(v, StayFilter.FLAG_HOTEL_FILTER_BED_HEATEDFLOORS);
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
        finish();

        try
        {
            StayRegion region = mStayCuration.getRegion();
            Map<String, String> eventParams = new HashMap<>();

            eventParams.put(AnalyticsManager.KeyType.SORTING, stayCurationOption.getSortType().name());
            eventParams.put(AnalyticsManager.KeyType.SEARCH_COUNT, String.valueOf(getConfirmCount()));

            if (region != null)
            {
                eventParams.put(AnalyticsManager.KeyType.COUNTRY, AnalyticsManager.ValueType.DOMESTIC);
                eventParams.put(AnalyticsManager.KeyType.PROVINCE, region.getAreaGroupName());

                AreaElement areaElement = region.getAreaElement();
                eventParams.put(AnalyticsManager.KeyType.DISTRICT, areaElement == null || areaElement.index == StayArea.ALL ? AnalyticsManager.ValueType.ALL_LOCALE_KR : areaElement.name);
            }

            AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                , AnalyticsManager.Action.HOTEL_SORT_FILTER_APPLY_BUTTON_CLICKED, stayCurationOption.toString(), eventParams);

            // 추가 항목
            AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.SORT_FLITER//
                , AnalyticsManager.Action.STAY_SORT, stayCurationOption.toSortString(), null);

            AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.SORT_FLITER//
                , AnalyticsManager.Action.STAY_PERSON, String.valueOf(stayCurationOption.person), null);

            AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.SORT_FLITER//
                , AnalyticsManager.Action.STAY_BEDTYPE, stayCurationOption.toBedTypeString(StayCurationOption.GA_DELIMITER), null);

            AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.SORT_FLITER//
                , AnalyticsManager.Action.STAY_AMENITIES, stayCurationOption.toAmenitiesString(StayCurationOption.GA_DELIMITER), null);

            AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.SORT_FLITER//
                , AnalyticsManager.Action.STAY_ROOM_AMENITIES, stayCurationOption.toRoomAmenitiesString(StayCurationOption.GA_DELIMITER), null);

            if (Constants.DEBUG == true)
            {
                ExLog.d(stayCurationOption.toString());
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    @Override
    protected void onCancel()
    {
        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
            , AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_CLICKED, AnalyticsManager.Label.CLOSE_BUTTON_CLICKED, null);

        finish();
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
    }

    private StayCurationNetworkController.OnNetworkControllerListener mNetworkControllerListener = new StayCurationNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onStayCount(String url, int hotelSaleCount)
        {
            if (DailyTextUtils.isTextEmpty(url) == true && hotelSaleCount == -1)
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
            if (requestParams != null && requestParams.contains(lastParams) == false) // ab 테스트로 인해서 데이터가 다름.
            //            if (lastParams.equalsIgnoreCase(requestParams) == false)
            {
                // already running another request!
                return;
            }

            if (hotelSaleCount <= 0)
            {
                setResultMessage(getString(R.string.label_hotel_filter_result_empty));

                StayCurationOption stayCurationOption = (StayCurationOption) mStayCuration.getCurationOption();

                AnalyticsManager.getInstance(StayCurationActivity.this).recordEvent(AnalyticsManager.Category.SORT_FLITER //
                    , AnalyticsManager.Action.STAY_NO_RESULT, stayCurationOption.toString(), null);
            } else
            {
                setResultMessage(getString(R.string.label_hotel_filter_result_count, hotelSaleCount));
            }

            setConfirmOnClickListener(StayCurationActivity.this);
            setConfirmEnable(hotelSaleCount != 0);
        }

        @Override
        public void onError(Call call, Throwable e, boolean onlyReport)
        {
            StayCurationActivity.this.onError(call, e, onlyReport);
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