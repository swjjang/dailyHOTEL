package com.twoheart.dailyhotel.screen.search.gourmet.result;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.RadioButton;

import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.GourmetCuration;
import com.twoheart.dailyhotel.model.GourmetCurationOption;
import com.twoheart.dailyhotel.model.GourmetSearchCuration;
import com.twoheart.dailyhotel.model.GourmetSearchParams;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.screen.gourmet.filter.GourmetCurationActivity;
import com.twoheart.dailyhotel.util.Constants;

import retrofit2.Call;
import retrofit2.Response;

public class GourmetSearchResultCurationActivity extends GourmetCurationActivity
{
    private static final String INTENT_EXTRA_DATA_IS_FIXED_LOCATION = "isFixedLocation";

    public static Intent newInstance(Context context, ViewType viewType, GourmetSearchCuration gourmetCuration, boolean isFixedLocation)
    {
        Intent intent = new Intent(context, GourmetSearchResultCurationActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_VIEWTYPE, viewType.name());
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACECURATION, gourmetCuration);
        intent.putExtra(INTENT_EXTRA_DATA_IS_FIXED_LOCATION, isFixedLocation);

        return intent;
    }

    @Override
    protected void initIntent(Intent intent)
    {
        super.initIntent(intent);

        mIsFixedLocation = intent.getBooleanExtra(INTENT_EXTRA_DATA_IS_FIXED_LOCATION, false);
    }

    @Override
    protected void initSortLayout(View view, ViewType viewType, GourmetCurationOption gourmetCurationOption)
    {
        mSortRadioGroup = view.findViewById(R.id.sortLayout);

        RadioButton radioButton = mSortRadioGroup.findViewById(R.id.regionCheckView);
        RadioButton emptyCheckView = mSortRadioGroup.findViewById(R.id.emptyCheckView);

        radioButton.setText(R.string.label_sort_by_rank);
        radioButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.f_ic_sort_06, 0, 0);

        emptyCheckView.setVisibility(View.GONE);

        if (viewType == ViewType.MAP)
        {
            setDisabledSortLayout(view, mSortRadioGroup);
            return;
        }

        switch (gourmetCurationOption.getSortType())
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
        mGourmetCuration.getCurationOption().clear();

        if (mViewType == ViewType.LIST)
        {
            mSortRadioGroup.setOnCheckedChangeListener(null);

            if (((GourmetSearchCuration) mGourmetCuration).getSuggest().isLocationSuggestItem() == true)
            {
                mSortRadioGroup.check(R.id.distanceCheckView);
            } else
            {
                mSortRadioGroup.check(R.id.regionCheckView);
            }

            mSortRadioGroup.setOnCheckedChangeListener(this);
        }

        requestUpdateResultDelayed();

        resetLayout(mGridLayout);
        resetLayout(mAmenitiesGridLayout);
        resetLayout(mTimeRangeLayout);
    }

    @Override
    protected BaseNetworkController getNetworkController(Context context)
    {
        return new GourmetSearchResultCurationNetworkController(context, mNetworkTag, mNetworkControllerListener);
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

        ((GourmetSearchResultCurationNetworkController) mNetworkController).requestGourmetSearchList(mLastParams);
    }

    @Override
    protected void setLastGourmetParams(GourmetCuration gourmetCuration)
    {
        if (gourmetCuration == null)
        {
            return;
        }

        if (mLastParams == null)
        {
            mLastParams = new GourmetSearchParams(gourmetCuration);
        } else
        {
            mLastParams.setPlaceParams(gourmetCuration);
        }
    }

    private GourmetSearchResultCurationNetworkController.OnNetworkControllerListener mNetworkControllerListener = new GourmetSearchResultCurationNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onGourmetCount(String url, int totalCount, int maxCount)
        {
            if (DailyTextUtils.isTextEmpty(url) == true && totalCount == -1)
            {
                // OnNetworkControllerListener onErrorResponse
                setResultMessage(getString(R.string.label_gourmet_filter_result_empty));

                setConfirmOnClickListener(GourmetSearchResultCurationActivity.this);
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
                setResultMessage(getString(R.string.label_gourmet_filter_result_empty));
            } else
            {
                if (totalCount >= maxCount)
                {
                    setResultMessage(getString(R.string.label_gourmet_filter_result_over_count, maxCount));
                } else
                {
                    setResultMessage(getString(R.string.label_gourmet_filter_result_count, totalCount));
                }
            }

            setConfirmOnClickListener(GourmetSearchResultCurationActivity.this);
            setConfirmEnable(totalCount != 0);
        }

        @Override
        public void onError(Call call, Throwable e, boolean onlyReport)
        {
            GourmetSearchResultCurationActivity.this.onError(call, e, onlyReport);
        }

        @Override
        public void onError(Throwable e)
        {
            GourmetSearchResultCurationActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            GourmetSearchResultCurationActivity.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            GourmetSearchResultCurationActivity.this.onErrorToastMessage(message);
        }

        @Override
        public void onErrorResponse(Call call, Response response)
        {
            GourmetSearchResultCurationActivity.this.onErrorResponse(call, response);
        }
    };
}