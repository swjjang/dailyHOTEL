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
