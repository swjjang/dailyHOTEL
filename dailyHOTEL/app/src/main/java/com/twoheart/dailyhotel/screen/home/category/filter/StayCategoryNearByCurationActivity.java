package com.twoheart.dailyhotel.screen.home.category.filter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.StayCategoryCuration;
import com.twoheart.dailyhotel.model.StayCategoryNearByParams;
import com.twoheart.dailyhotel.model.StayCurationOption;
import com.twoheart.dailyhotel.model.StayFilter;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;

import retrofit2.Call;
import retrofit2.Response;

@Deprecated
public class StayCategoryNearByCurationActivity extends StayCategoryCurationActivity
{
    private static final String INTENT_EXTRA_DATA_SEARCHTYPE = "searchType";
    private static final String INTENT_EXTRA_DATA_IS_FIXED_LOCATION = "isFixedLocation";

    private SearchType mSearchType;

    //    public static Intent newInstance(Context context, ViewType viewType, SearchType searchType, StayCategoryNearByCuration stayCategoryNearByCuration, boolean isFixedLocation)
    //    {
    //        Intent intent = new Intent(context, StayCategoryNearByCurationActivity.class);
    //        intent.putExtra(INTENT_EXTRA_DATA_VIEWTYPE, viewType.name());
    //        intent.putExtra(INTENT_EXTRA_DATA_SEARCHTYPE, searchType.name());
    //        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACECURATION, stayCategoryNearByCuration);
    //        intent.putExtra(INTENT_EXTRA_DATA_IS_FIXED_LOCATION, isFixedLocation);
    //
    //        return intent;
    //    }

    @Override
    protected void initIntent(Intent intent)
    {
        super.initIntent(intent);

        mSearchType = SearchType.valueOf(intent.getStringExtra(INTENT_EXTRA_DATA_SEARCHTYPE));
        mIsFixedLocation = intent.getBooleanExtra(INTENT_EXTRA_DATA_IS_FIXED_LOCATION, false);
    }

    @Override
    protected void initContentLayout(ViewGroup contentLayout)
    {
        StayCurationOption stayCurationOption = (StayCurationOption) mStayCategoryCuration.getCurationOption();

        View sortLayout = LayoutInflater.from(this).inflate(R.layout.layout_hotel_sort, null);
        initSortLayout(sortLayout, mViewType, stayCurationOption);

        contentLayout.addView(sortLayout);

        View filterLayout = LayoutInflater.from(this).inflate(R.layout.layout_hotel_filter, null);
        initFilterLayout(filterLayout, stayCurationOption);

        contentLayout.addView(filterLayout);

        initAmenitiesLayout(filterLayout, stayCurationOption);

        initInRoomAmenitiesLayout(filterLayout, stayCurationOption);
    }


    @Override
    protected void initSortLayout(View view, ViewType viewType, StayCurationOption stayCurationOption)
    {
        mSortRadioGroup = view.findViewById(R.id.sortLayout);

        RadioButton radioButton = mSortRadioGroup.findViewById(R.id.regionCheckView);
        RadioButton emptyCheckView = mSortRadioGroup.findViewById(R.id.emptyCheckView);

        //        if (mSearchType == SearchType.LOCATION)
        //        {
        //            radioButton.setVisibility(View.GONE);
        //            emptyCheckView.setVisibility(View.INVISIBLE);
        //        } else
        //        {
        radioButton.setText(R.string.label_sort_by_rank);
        radioButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.f_ic_sort_06, 0, 0);

        emptyCheckView.setVisibility(View.GONE);
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

    @Override
    protected void resetCuration()
    {
        mStayCategoryCuration.getCurationOption().clear();

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

        updatePersonFilter(StayFilter.DEFAULT_PERSON);

        resetLayout(mBedTypeLayout);
        resetLayout(mAmenitiesGridLayout);
        resetLayout(mInRoomAmenitiesGridLayout);
    }

    @Override
    protected BaseNetworkController getNetworkController(Context context)
    {
        return new StayCategoryNearByCurationNetworkController(context, mNetworkTag, mNetworkControllerListener);
    }

    @Override
    protected void updateResultMessage()
    {
        setConfirmOnClickListener(null);

        if (mLastParams != null && SortType.DISTANCE == mLastParams.getSortType() && mLastParams.hasLocation() == false)
        {
            onSearchLocationResult(null);
            return;
        }

        String abTestType = DailyRemoteConfigPreference.getInstance(this).getKeyRemoteConfigStayRankTestType();

        ((StayCategoryNearByCurationNetworkController) mNetworkController).requestStayCategoryNearByList(mLastParams, abTestType);
    }

    @Override
    protected void setLastStayCategoryParams(StayCategoryCuration stayCategoryCuration)
    {
        if (stayCategoryCuration == null)
        {
            return;
        }

        if (mLastParams == null)
        {
            mLastParams = new StayCategoryNearByParams(stayCategoryCuration);
        } else
        {
            mLastParams.setPlaceParams(stayCategoryCuration);
        }
    }

    private StayCategoryNearByCurationNetworkController.OnNetworkControllerListener mNetworkControllerListener = new StayCategoryNearByCurationNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onStayCount(String url, int totalCount, int maxCount)
        {
            if (DailyTextUtils.isTextEmpty(url) == true && totalCount == -1)
            {
                // OnNetworkControllerListener onErrorResponse
                setResultMessage(getString(R.string.label_hotel_filter_result_empty));

                setConfirmOnClickListener(StayCategoryNearByCurationActivity.this);
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

            if (totalCount <= 0)
            {
                setResultMessage(getString(R.string.label_hotel_filter_result_empty));
            } else
            {
                if (totalCount >= maxCount)
                {
                    setResultMessage(getString(R.string.label_hotel_filter_result_over_count, maxCount));
                } else
                {
                    setResultMessage(getString(R.string.label_hotel_filter_result_count, totalCount));
                }
            }

            setConfirmOnClickListener(StayCategoryNearByCurationActivity.this);
            setConfirmEnable(totalCount != 0);
        }

        @Override
        public void onError(Call call, Throwable e, boolean onlyReport)
        {
            StayCategoryNearByCurationActivity.this.onError(call, e, onlyReport);
        }

        @Override
        public void onError(Throwable e)
        {
            StayCategoryNearByCurationActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            StayCategoryNearByCurationActivity.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            StayCategoryNearByCurationActivity.this.onErrorToastMessage(message);
        }

        @Override
        public void onErrorResponse(Call call, Response response)
        {
            StayCategoryNearByCurationActivity.this.onErrorResponse(call, response);
        }
    };
}