package com.daily.dailyhotel.screen.stay.outbound.filter;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.twoheart.dailyhotel.R;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayOutboundFilterPresenter extends BaseExceptionPresenter<StayOutboundFilterActivity, StayOutboundFilterViewInterface> implements StayOutboundFilterView.OnEventListener
{
    private StayOutboundFilterAnalyticsInterface mAnalytics;

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
        setContentView(R.layout.activity_stay_outbound_search_data);

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

        if (intent.hasExtra(BaseActivity.INTENT_EXTRA_DATA_DEEPLINK) == true)
        {
        }

        return true;
    }

    @Override
    public void onPostCreate()
    {

    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (isRefresh() == true)
        {
            onRefresh();
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
    protected void onRefresh()
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
}
