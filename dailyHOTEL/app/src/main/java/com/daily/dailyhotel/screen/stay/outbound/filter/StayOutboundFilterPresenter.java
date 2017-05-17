package com.daily.dailyhotel.screen.stay.outbound.filter;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.StayOutboundFilters;
import com.twoheart.dailyhotel.R;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayOutboundFilterPresenter extends BaseExceptionPresenter<StayOutboundFilterActivity, StayOutboundFilterViewInterface> implements StayOutboundFilterView.OnEventListener
{
    private StayOutboundFilterAnalyticsInterface mAnalytics;
    private StayOutboundFilters mStayOutboundFilters;

    public interface StayOutboundFilterAnalyticsInterface extends BaseAnalyticsInterface
    {
    }

    public StayOutboundFilterPresenter(@NonNull StayOutboundFilterActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected StayOutboundFilterViewInterface createInstanceViewInterface()
    {
        return new StayOutboundFilterView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(StayOutboundFilterActivity activity)
    {
        setContentView(R.layout.activity_stay_outbound_filter_data);

        setAnalytics(new StayStayOutboundFilterAnalyticsImpl());

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (StayOutboundFilterAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        if (mStayOutboundFilters == null)
        {
            mStayOutboundFilters = new StayOutboundFilters();
        }

        try
        {
            mStayOutboundFilters.sortType = StayOutboundFilters.SortType.valueOf(intent.getStringExtra(StayOutboundFilterActivity.INTENT_EXTRA_DATA_SORT));
        } catch (Exception e)
        {
            mStayOutboundFilters.sortType = StayOutboundFilters.SortType.RECOMMENDATION;
        }

        mStayOutboundFilters.rating = intent.getIntExtra(StayOutboundFilterActivity.INTENT_EXTRA_DATA_RATING, -1);

        return true;
    }

    @Override
    public void onPostCreate()
    {
        getViewInterface().setSort(mStayOutboundFilters.sortType);
        getViewInterface().setRating(mStayOutboundFilters.rating);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (isRefresh() == true)
        {
            onRefresh(true);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        // 꼭 호출해 주세요.
        super.onDestroy();
    }

    @Override
    public boolean onBackPressed()
    {
        return super.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        unLockAll();
    }

    @Override
    protected void onRefresh(boolean showProgress)
    {
        if (getActivity().isFinishing() == true)
        {
            return;
        }

    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    @Override
    public void onSortClick(StayOutboundFilters.SortType sortType)
    {
        if (sortType == null)
        {
            return;
        }

        mStayOutboundFilters.sortType = sortType;
    }

    @Override
    public void onRatingClick(int rating)
    {
        mStayOutboundFilters.rating = rating;
    }

    @Override
    public void onResetClick()
    {
        if (mStayOutboundFilters == null)
        {
            mStayOutboundFilters = new StayOutboundFilters();
        }

        mStayOutboundFilters.sortType = StayOutboundFilters.SortType.RECOMMENDATION;
        mStayOutboundFilters.rating = -1;
    }

    @Override
    public void onResultClick()
    {
        Intent intent = new Intent();
        intent.putExtra(StayOutboundFilterActivity.INTENT_EXTRA_DATA_SORT, mStayOutboundFilters.sortType.name());
        intent.putExtra(StayOutboundFilterActivity.INTENT_EXTRA_DATA_RATING, mStayOutboundFilters.rating);

        setResult(Activity.RESULT_OK, intent);
        onBackClick();
    }
}
