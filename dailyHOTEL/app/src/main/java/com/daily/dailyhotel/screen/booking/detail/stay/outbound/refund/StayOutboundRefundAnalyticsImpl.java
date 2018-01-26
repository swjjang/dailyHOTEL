package com.daily.dailyhotel.screen.booking.detail.stay.outbound.refund;

import android.app.Activity;

import com.daily.dailyhotel.entity.StayOutboundBookingDetail;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.HashMap;
import java.util.Map;

public class StayOutboundRefundAnalyticsImpl implements StayOutboundRefundPresenter.StayOutboundRefundAnalyticsInterface
{
    @Override
    public void onCompletedRefund(Activity activity, String stayName, StayOutboundBookingDetail.RefundType refundType, String cancelMessage)
    {
        if (activity == null || refundType == null)
        {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(AnalyticsManager.KeyType.STAY_NAME, stayName);

        switch (refundType)
        {
            case FULL:
                params.put(AnalyticsManager.KeyType.CANCEL_TYPE, AnalyticsManager.ValueType.FREE_CANCEL);
                break;

            case PARTIAL:
                params.put(AnalyticsManager.KeyType.CANCEL_TYPE, AnalyticsManager.ValueType.CANCEL_FEE);
                break;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
            , AnalyticsManager.Action.STAY_OUTBOUND_CANCELED, cancelMessage, params);
    }
}
