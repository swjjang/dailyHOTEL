package com.twoheart.dailyhotel.screen.search.stay.result;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.StayCuration;
import com.twoheart.dailyhotel.model.StayCurationOption;
import com.twoheart.dailyhotel.model.StayFilter;
import com.twoheart.dailyhotel.model.StaySearchParams;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.screen.hotel.filter.StayCurationActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.HashMap;
import java.util.Map;

public class StaySearchResultCurationActivity extends StayCurationActivity
{
    private static final String INTENT_EXTRA_DATA_SEARCHTYPE = "searchType";

    private SearchType mSearchType;

    public static Intent newInstance(Context context, ViewType viewType, SearchType searchType, StayCuration stayCuration)
    {
        Intent intent = new Intent(context, StaySearchResultCurationActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_VIEWTYPE, viewType.name());
        intent.putExtra(INTENT_EXTRA_DATA_SEARCHTYPE, searchType.name());
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACECURATION, stayCuration);

        return intent;
    }

    @Override
    protected void initIntent(Intent intent)
    {
        super.initIntent(intent);

        mSearchType = SearchType.valueOf(intent.getStringExtra(INTENT_EXTRA_DATA_SEARCHTYPE));
    }

    @Override
    protected void initContentLayout(ViewGroup contentLayout)
    {
        View sortLayout = LayoutInflater.from(this).inflate(R.layout.layout_hotel_sort, null);

        StayCurationOption stayCurationOption = (StayCurationOption) mStayCuration.getCurationOption();

        initSortLayout(sortLayout, mViewType, stayCurationOption);

        contentLayout.addView(sortLayout);

        View filterLayout = LayoutInflater.from(this).inflate(R.layout.layout_hotel_filter, null);
        initFilterLayout(filterLayout, stayCurationOption);

        contentLayout.addView(filterLayout);

        initAmenitiesLayout(filterLayout, stayCurationOption);
    }


    @Override
    protected void initSortLayout(View view, ViewType viewType, StayCurationOption stayCurationOption)
    {
        mSortRadioGroup = (RadioGroup) view.findViewById(R.id.sortLayout);

        RadioButton radioButton = (RadioButton) mSortRadioGroup.findViewById(R.id.regionCheckView);
        RadioButton emptyCheckView = (RadioButton) mSortRadioGroup.findViewById(R.id.emptyCheckView);

        if (mSearchType == SearchType.LOCATION)
        {
            radioButton.setVisibility(View.GONE);
            emptyCheckView.setVisibility(View.INVISIBLE);
        } else
        {
            radioButton.setText(R.string.label_sort_by_rank);
            radioButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.selector_sort_rank_button, 0, 0);

            emptyCheckView.setVisibility(View.GONE);
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

    @Override
    protected void resetCuration()
    {
        mStayCuration.getCurationOption().clear();

        if (mViewType == ViewType.LIST)
        {
            mSortRadioGroup.setOnCheckedChangeListener(null);

            if (mSearchType == SearchType.LOCATION)
            {
                mSortRadioGroup.check(R.id.distanceCheckView);
            } else
            {
                mSortRadioGroup.check(R.id.regionCheckView);
            }

            mSortRadioGroup.setOnCheckedChangeListener(this);
        }

        updatePersonFilter(StayFilter.MIN_PERSON);

        resetLayout(mBedTypeLayout);
        resetLayout(mGridLayout);
    }

    @Override
    protected void onComplete()
    {
        StayCurationOption stayCurationOption = (StayCurationOption) mStayCuration.getCurationOption();

        Map<String, String> eventParams = new HashMap<>();

        eventParams.put(AnalyticsManager.KeyType.SORTING, stayCurationOption.getSortType().name());

        //        if (mProvince instanceof Area)
        //        {
        //            Area area = (Area) mProvince;
        //            eventParams.put(AnalyticsManager.KeyType.COUNTRY, area.getProvince().isOverseas ? AnalyticsManager.KeyType.OVERSEAS : AnalyticsManager.KeyType.DOMESTIC);
        //            eventParams.put(AnalyticsManager.KeyType.PROVINCE, area.getProvince().name);
        //            eventParams.put(AnalyticsManager.KeyType.DISTRICT, area.name);
        //        } else
        //        {
        //            eventParams.put(AnalyticsManager.KeyType.COUNTRY, mProvince.isOverseas ? AnalyticsManager.KeyType.OVERSEAS : AnalyticsManager.KeyType.DOMESTIC);
        //            eventParams.put(AnalyticsManager.KeyType.PROVINCE, mProvince.name);
        //            eventParams.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.EMPTY);
        //        }

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
            , AnalyticsManager.Action.HOTEL_SORT_FILTER_APPLY_BUTTON_CLICKED, stayCurationOption.toString(), eventParams);

        if (Constants.DEBUG == true)
        {
            ExLog.d(stayCurationOption.toString());
        }

        Intent intent = new Intent();
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACECURATION, mStayCuration);

        setResult(RESULT_OK, intent);
        hideAnimation();
    }

    @Override
    protected BaseNetworkController getNetworkController(Context context)
    {
        return new StaySearchResultCurationNetworkController(context, mNetworkTag, mNetworkControllerListener);
    }

    @Override
    protected void updateResultMessage()
    {
        setConfirmOnClickListener(null);

        ((StaySearchResultCurationNetworkController)mNetworkController).requestStaySearchList(mLastParams);
    }

    @Override
    protected void setLastStayParams(StayCuration stayCuration)
    {
        if (stayCuration == null)
        {
            return;
        }

        if (mLastParams == null)
        {
            mLastParams = new StaySearchParams(stayCuration);
        } else
        {
            mLastParams.setPlaceParams(stayCuration);
        }
    }

    private StaySearchResultCurationNetworkController.OnNetworkControllerListener mNetworkControllerListener = new StaySearchResultCurationNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onStayCount(String url, int totalCount, int maxCount)
        {
            if (Util.isTextEmpty(url) == true && totalCount == -1)
            {
                // OnNetworkControllerListener onErrorResponse
                setResultMessage(getString(R.string.label_hotel_filter_result_empty));

                setConfirmOnClickListener(StaySearchResultCurationActivity.this);
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

            if (totalCount <= 0)
            {
                setResultMessage(getString(R.string.label_hotel_filter_result_empty));
            } else
            {
                if(totalCount >= maxCount)
                {
                    setResultMessage(getString(R.string.label_hotel_filter_result_over_count, maxCount));
                } else
                {
                    setResultMessage(getString(R.string.label_hotel_filter_result_count, totalCount));
                }
            }

            setConfirmOnClickListener(StaySearchResultCurationActivity.this);
            setConfirmEnable(totalCount == 0 ? false : true);
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            StaySearchResultCurationActivity.this.onErrorResponse(volleyError);
        }

        @Override
        public void onError(Exception e)
        {
            StaySearchResultCurationActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            StaySearchResultCurationActivity.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            StaySearchResultCurationActivity.this.onErrorToastMessage(message);
        }
    };
}