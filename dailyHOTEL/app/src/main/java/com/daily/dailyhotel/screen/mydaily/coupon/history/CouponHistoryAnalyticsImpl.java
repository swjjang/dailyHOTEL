package com.daily.dailyhotel.screen.mydaily.coupon.history;

import android.app.Activity;

import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

/**
 * Created by android_sam on 2017. 9. 28..
 */

public class CouponHistoryAnalyticsImpl implements CouponHistoryPresenter.CouponHistoryAnalyticsInterface
{
    @Override
    public void onScreen(Activity activity)
    {
        AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.MENU_COUPON_HISTORY, null);
    }
}
