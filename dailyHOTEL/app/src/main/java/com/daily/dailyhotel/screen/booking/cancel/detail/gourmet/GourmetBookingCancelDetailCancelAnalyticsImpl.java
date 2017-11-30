package com.daily.dailyhotel.screen.booking.cancel.detail.gourmet;

import android.app.Activity;

import com.daily.dailyhotel.entity.GourmetMultiBookingDetail;
import com.daily.dailyhotel.parcel.analytics.GourmetDetailAnalyticsParam;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class GourmetBookingCancelDetailCancelAnalyticsImpl implements GourmetBookingCancelDetailPresenter.GourmetBookingCancelAnalyticsInterface
{
    @Override
    public void onScreen(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.CANCEL_DETAIL, null);
    }

    @Override
    public void onEventShareClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.RESERVATION_CANCEL, AnalyticsManager.Action.SHARE, null, null);
    }

    @Override
    public void onEventConciergeClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.RESERVATION_CANCEL, AnalyticsManager.Action.INQUIRY, null, null);
    }

    @Override
    public void onEventViewDetailClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.RESERVATION_CANCEL, AnalyticsManager.Action.SHOW_DETAIL, null, null);
    }

    @Override
    public void onEventNavigatorClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.RESERVATION_CANCEL, AnalyticsManager.Action.FIND_PATH, null, null);
    }

    @Override
    public void onEventHideBookingCancelClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.RESERVATION_CANCEL, AnalyticsManager.Action.DELETE_HISTORY, null, null);
    }

    @Override
    public GourmetDetailAnalyticsParam getDetailAnalyticsParam(GourmetMultiBookingDetail gourmetBookingDetail)
    {
        GourmetDetailAnalyticsParam analyticsParam = new GourmetDetailAnalyticsParam();

        return analyticsParam;
    }
}
