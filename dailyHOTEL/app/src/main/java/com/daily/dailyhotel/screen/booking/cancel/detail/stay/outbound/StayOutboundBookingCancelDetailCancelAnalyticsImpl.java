package com.daily.dailyhotel.screen.booking.cancel.detail.stay.outbound;

import android.app.Activity;

import com.daily.dailyhotel.entity.StayOutboundBookingDetail;
import com.daily.dailyhotel.parcel.analytics.StayOutboundDetailAnalyticsParam;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class StayOutboundBookingCancelDetailCancelAnalyticsImpl implements StayOutboundBookingCancelDetailPresenter.StayOutboundBookingCancelAnalyticsInterface
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
            , AnalyticsManager.Action.OB_CANCEL_SHARE, AnalyticsManager.ValueType.KAKAO, null);
    }

    @Override
    public void onEventMoreShareClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SHARE//
            , AnalyticsManager.Action.OB_CANCEL_SHARE, AnalyticsManager.ValueType.ETC, null);
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
    public StayOutboundDetailAnalyticsParam getDetailAnalyticsParam(StayOutboundBookingDetail stayOutboundBookingDetail)
    {
        StayOutboundDetailAnalyticsParam analyticsParam = new StayOutboundDetailAnalyticsParam();

        if (stayOutboundBookingDetail != null)
        {
            analyticsParam.index = stayOutboundBookingDetail.stayIndex;
        }

        analyticsParam.benefit = false;
        analyticsParam.grade = null;
        analyticsParam.rankingPosition = -1;
        analyticsParam.rating = null;
        analyticsParam.listSize = -1;

        return analyticsParam;
    }
}
