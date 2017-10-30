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

    @Override
    public void onEventAgainClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.RESERVATION_CANCEL, AnalyticsManager.Action.RE_RESERVATION, null, null);
    }

    @Override
    public void onEventBackClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.RESERVATION_CANCEL, AnalyticsManager.Action.BACK, null, null);
    }

    @Override
    public void onEventEmptyView(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.RESERVATION_CANCEL, AnalyticsManager.Action.NO_RESULT, null, null);
    }

    @Override
    public void onEventViewStayClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.RESERVATION_CANCEL, AnalyticsManager.Action.STAY_CLICK, null, null);
    }

    @Override
    public void onEventViewGourmetClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.RESERVATION_CANCEL, AnalyticsManager.Action.GOURMET_CLICK, null, null);
    }
}
