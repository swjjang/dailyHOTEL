package com.daily.dailyhotel.screen.home.stay.outbound.payment.coupon;

import android.app.Activity;

import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class SelectStayOutboundCouponDialogAnalyticsImpl implements SelectStayOutboundCouponDialogInterface.AnalyticsInterface
{
    @Override
    public void onEventConfirm(Activity activity, String couponName)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS, //
            "OB_HotelCouponSelected", couponName, null);
    }

    @Override
    public void onEventCancel(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS, //
            "OB_HotelUsingCouponCancelClicked", AnalyticsManager.Label.HOTEL_USING_COUPON_CANCEL_CLICKED, null);
    }
}
