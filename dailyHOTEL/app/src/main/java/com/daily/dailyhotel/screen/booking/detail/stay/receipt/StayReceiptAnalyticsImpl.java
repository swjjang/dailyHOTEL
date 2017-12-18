package com.daily.dailyhotel.screen.booking.detail.stay.receipt;

import android.app.Activity;

import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class StayReceiptAnalyticsImpl implements StayReceiptPresenter.StayReceiptAnalyticsInterface
{
    @Override
    public void onScreen(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.BOOKING_DETAIL_RECEIPT, null);
    }
}
