package com.daily.dailyhotel.screen.mydaily.coupon;

import android.app.Activity;

import com.daily.dailyhotel.screen.common.web.DailyWebInterface;

public interface CouponTermInterface
{
    interface ViewInterface extends DailyWebInterface.ViewInterface
    {
    }

    interface OnEventListener extends DailyWebInterface.OnEventListener
    {
    }

    interface AnalyticsInterface extends DailyWebInterface.AnalyticsInterface
    {
        void onScreen(Activity activity, String couponIndex);
    }
}
