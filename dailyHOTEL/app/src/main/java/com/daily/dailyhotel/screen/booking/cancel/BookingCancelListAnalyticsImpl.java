package com.daily.dailyhotel.screen.booking.cancel;

import android.app.Activity;

import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class BookingCancelListAnalyticsImpl implements BookingCancelListPresenter.BookingCancelListAnalyticsInterface
{
    @Override
    public void onScreen(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.CANCEL_LIST, null);
    }
}
