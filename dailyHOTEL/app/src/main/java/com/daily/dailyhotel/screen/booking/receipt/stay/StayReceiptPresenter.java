package com.daily.dailyhotel.screen.booking.receipt.stay;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.Booking;
import com.twoheart.dailyhotel.R;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayReceiptPresenter extends BaseExceptionPresenter<StayReceiptActivity, StayReceiptInterface> implements StayReceiptView.OnEventListener
{
    private StayReceiptAnalyticsInterface mAnalytics;

    private int mBookingIdx;
    private int mBookingState;
    private String mAggregationId;
    private String mReservationIndex;
    boolean mIsFullscreen;

    public interface StayReceiptAnalyticsInterface extends BaseAnalyticsInterface
    {
    }

    public StayReceiptPresenter(@NonNull StayReceiptActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected StayReceiptInterface createInstanceViewInterface()
    {
        return new StayReceiptView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(StayReceiptActivity activity)
    {
        setContentView(R.layout.activity_stay_receipt_data);

        setAnalytics(new StayReceiptAnalyticsImpl());

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (StayReceiptAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        mBookingIdx = intent.getIntExtra(StayReceiptActivity.INTENT_EXTRA_RESERVATION_INDEX, -1);
        mBookingState = intent.getIntExtra(StayReceiptActivity.INTENT_EXTRA_BOOKING_STATE, Booking.BOOKING_STATE_NONE);
        mAggregationId = intent.getStringExtra(StayReceiptActivity.INTENT_EXTRA_AGGREGATION_ID);

        if (mBookingIdx < 0 && DailyTextUtils.isTextEmpty(mAggregationId) == true)
        {
            return false;
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

        if (isRefresh() == true)
        {
            onRefresh(true);
        }
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
    protected synchronized void onRefresh(boolean showProgress)
    {
        if (getActivity().isFinishing() == true || isRefresh() == false)
        {
            return;
        }

        setRefresh(false);
        screenLock(showProgress);
    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

}
