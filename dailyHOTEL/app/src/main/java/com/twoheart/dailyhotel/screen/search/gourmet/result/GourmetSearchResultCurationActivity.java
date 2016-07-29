package com.twoheart.dailyhotel.screen.search.gourmet.result;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.GourmetCuration;
import com.twoheart.dailyhotel.model.GourmetCurationOption;
import com.twoheart.dailyhotel.model.GourmetSearchParams;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.screen.gourmet.filter.GourmetCurationActivity;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONObject;

public class GourmetSearchResultCurationActivity extends GourmetCurationActivity
{
    private static final String INTENT_EXTRA_DATA_SEARCHTYPE = "searchType";

    private SearchType mSearchType;
    protected GourmetSearchParams mLastParams;

    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onStayCount(String url, int hotelSaleCount);
    }

    public static Intent newInstance(Context context, ViewType viewType, SearchType searchType, GourmetCuration gourmetCuration)
    {
        Intent intent = new Intent(context, GourmetCurationActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_VIEWTYPE, viewType.name());
        intent.putExtra(INTENT_EXTRA_DATA_SEARCHTYPE, searchType.name());
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACECURATION, gourmetCuration);

        return intent;
    }

    @Override
    protected void initIntent(Intent intent)
    {
        super.initIntent(intent);

        mSearchType = SearchType.valueOf(intent.getStringExtra(INTENT_EXTRA_DATA_SEARCHTYPE));

        if (mSearchType == SearchType.LOCATION)
        {
            mGourmetCuration.getCurationOption().setDefaultSortType(SortType.DISTANCE);
        } else
        {
            mGourmetCuration.getCurationOption().setDefaultSortType(SortType.DEFAULT);
        }
    }

    @Override
    protected void initSortLayout(View view, ViewType viewType, GourmetCurationOption gourmetCurationOption)
    {
        mSortRadioGroup = (RadioGroup) view.findViewById(R.id.sortLayout);

        if (viewType == ViewType.MAP)
        {
            setDisabledSortLayout(view, mSortRadioGroup);
            return;
        }

        RadioButton radioButton = (RadioButton) mSortRadioGroup.findViewById(R.id.regionCheckView);

        if (mSearchType == SearchType.LOCATION)
        {
            radioButton.setVisibility(View.GONE);

            gourmetCurationOption.setSortType(SortType.DISTANCE);
        } else
        {
            radioButton.setText(R.string.label_sort_by_rank);
            radioButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.selector_sort_rank_button, 0, 0);
        }

        switch (gourmetCurationOption.getSortType())
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

            if (mSearchType == SearchType.LOCATION)
            {
                mSortRadioGroup.check(R.id.distanceCheckView);
            } else
            {
                mSortRadioGroup.check(R.id.regionCheckView);
            }

            mSortRadioGroup.setOnCheckedChangeListener(this);
        }

        if (mGourmetCuration.getProvince().isOverseas == false)
        {
            resetLayout(mGridLayout);
            resetLayout(mAmenitiesLayout);
            resetLayout(mTimeRangeLayout);
        }

        requestUpdateResult();
    }

    @Override
    protected void requestUpdateResult()
    {
        setResultMessage(getResources().getString(R.string.label_searching));

        if (mGourmetCuration == null || mGourmetCuration.getSaleTime() == null)
        {
            Util.restartApp(this);
            return;
        }

        setLastGourmetParams(mGourmetCuration);

        super.requestUpdateResult();
    }

    @Override
    protected void requestUpdateResultDelayed()
    {
        setResultMessage(getResources().getString(R.string.label_searching));

        if (mGourmetCuration == null || mGourmetCuration.getSaleTime() == null)
        {
            Util.restartApp(this);
            return;
        }

        setLastGourmetParams(mGourmetCuration);

        super.requestUpdateResultDelayed();
    }

    @Override
    protected void updateResultMessage()
    {
        setConfirmOnClickListener(null);

        DailyNetworkAPI.getInstance(this).requestGourmetList(mNetworkTag, mLastParams.toParamsString(), mGourmetListJsonResponseListener);
    }

    private void setLastGourmetParams(GourmetCuration gourmetCuration)
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

    private DailyHotelJsonResponseListener mGourmetListJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            onGourmetCount(null, -1);
        }

        @Override
        public void onResponse(String url, JSONObject response)
        {
            int gourmetSaleCount;

            try
            {
                int msgCode = response.getInt("msgCode");
                if (msgCode == 100)
                {
                    JSONObject dataJSONObject = response.getJSONObject("data");
                    gourmetSaleCount = dataJSONObject.getInt("gourmetSalesCount");

                } else
                {
                    gourmetSaleCount = 0;
                }
            } catch (Exception e)
            {
                gourmetSaleCount = 0;
            }

            onGourmetCount(url, gourmetSaleCount);
        }

        private void onGourmetCount(String url, int gourmetSaleCount)
        {
            if (Util.isTextEmpty(url) == true && gourmetSaleCount == -1)
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

            if (gourmetSaleCount <= 0)
            {
                setResultMessage(getString(R.string.label_gourmet_filter_result_empty));
            } else
            {
                setResultMessage(getString(R.string.label_gourmet_filter_result_count, gourmetSaleCount));
            }

            setConfirmOnClickListener(GourmetSearchResultCurationActivity.this);
            setConfirmEnable(gourmetSaleCount == 0 ? false : true);
        }
    };
}