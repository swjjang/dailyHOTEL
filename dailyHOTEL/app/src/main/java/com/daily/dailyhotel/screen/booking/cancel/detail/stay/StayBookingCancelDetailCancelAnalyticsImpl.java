package com.daily.dailyhotel.screen.booking.cancel.detail.stay;

import android.app.Activity;

import com.daily.dailyhotel.entity.StayBookingDetail;
import com.daily.dailyhotel.parcel.analytics.StayDetailAnalyticsParam;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class StayBookingCancelDetailCancelAnalyticsImpl implements StayBookingCancelDetailPresenter.StayBookingCancelAnalyticsInterface
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
    public void onEventShareKakaoClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SHARE//
            , AnalyticsManager.Action.STAY_CANCEL_SHARE, AnalyticsManager.ValueType.KAKAO, null);
    }

    @Override
    public void onEventMoreShareClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SHARE//
            , AnalyticsManager.Action.STAY_CANCEL_SHARE, AnalyticsManager.ValueType.ETC, null);
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
    public StayDetailAnalyticsParam getDetailAnalyticsParam(StayBookingDetail stayBookingDetail)
    {
        StayDetailAnalyticsParam analyticsParam = new StayDetailAnalyticsParam();

        return analyticsParam;
    }
}
