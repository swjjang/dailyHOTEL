package com.daily.dailyhotel.screen.booking.detail.stay.outbound.refund;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.twoheart.dailyhotel.R;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayOutboundRefundPresenter extends BaseExceptionPresenter<StayOutboundRefundActivity, StayOutboundRefundInterface> implements StayOutboundRefundView.OnEventListener
{
    private StayOutboundRefundAnalyticsInterface mAnalytics;

    public interface StayOutboundRefundAnalyticsInterface extends BaseAnalyticsInterface
    {
    }

    public StayOutboundRefundPresenter(@NonNull StayOutboundRefundActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected StayOutboundRefundInterface createInstanceViewInterface()
    {
        return new StayOutboundRefundView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(StayOutboundRefundActivity activity)
    {
        setContentView(R.layout.activity_stay_outbound_refund_data);

        setAnalytics(new StayOutboundRefundAnalyticsImpl());

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (StayOutboundRefundAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
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

}
