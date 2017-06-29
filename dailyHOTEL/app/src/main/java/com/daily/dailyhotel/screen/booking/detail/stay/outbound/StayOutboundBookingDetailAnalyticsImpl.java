package com.daily.dailyhotel.screen.booking.detail.stay.outbound;

import com.daily.dailyhotel.entity.StayOutboundBookingDetail;
import com.daily.dailyhotel.parcel.analytics.StayOutboundDetailAnalyticsParam;

public class StayOutboundBookingDetailAnalyticsImpl implements StayOutboundBookingDetailPresenter.StayOutboundBookingAnalyticsInterface
{
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
