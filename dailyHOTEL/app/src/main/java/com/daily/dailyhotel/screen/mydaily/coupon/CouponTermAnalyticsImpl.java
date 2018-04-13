package com.daily.dailyhotel.screen.mydaily.coupon;

import android.app.Activity;

import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.screen.common.web.DailyWebAnalyticsImpl;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class CouponTermAnalyticsImpl extends DailyWebAnalyticsImpl implements CouponTermInterface.AnalyticsInterface
{
    @Override
    public void onScreen(Activity activity, String couponIndex)
    {
        if (activity == null)
        {
            return;
        }

        String screen;

        if (DailyTextUtils.isTextEmpty(couponIndex))
        {
            screen = AnalyticsManager.Screen.MENU_COUPON_GENERAL_TERMS_OF_USE;
        } else
        {
            screen = AnalyticsManager.Screen.MENU_COUPON_INDIVIDUAL_TERMS_OF_USE;
        }

        AnalyticsManager.getInstance(activity).recordScreen(activity, screen, null);
    }
}
