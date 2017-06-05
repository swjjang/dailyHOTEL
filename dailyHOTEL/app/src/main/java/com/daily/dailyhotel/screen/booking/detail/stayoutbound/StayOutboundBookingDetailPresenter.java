package com.daily.dailyhotel.screen.booking.detail.stayoutbound;


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
public class StayOutboundBookingDetailPresenter extends BaseExceptionPresenter<StayOutboundBookingDetailActivity, StayOutboundBookingDetailInterface> implements StayOutboundBookingDetailView.OnEventListener
{
    private StayOutboundBookingAnalyticsInterface mAnalytics;

    private int mReservationIndex;
    private String mImageUrl;

    public interface StayOutboundBookingAnalyticsInterface extends BaseAnalyticsInterface
    {
    }

    public StayOutboundBookingDetailPresenter(@NonNull StayOutboundBookingDetailActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected StayOutboundBookingDetailInterface createInstanceViewInterface()
    {
        return new StayOutboundBookingDetailView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(StayOutboundBookingDetailActivity activity)
    {
        setContentView(R.layout.activity_stay_outbound_booking_detail_data);

        setAnalytics(new StayOutboundBookingDetailAnalyticsImpl());

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (StayOutboundBookingAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        mReservationIndex = intent.getIntExtra(StayOutboundBookingDetailActivity.INTENT_EXTRA_DATA_RESERVATION_INDEX, -1);
        mImageUrl = intent.getStringExtra(StayOutboundBookingDetailActivity.INTENT_EXTRA_DATA_IMAGE_URL);

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
