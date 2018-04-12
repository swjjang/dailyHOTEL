package com.daily.dailyhotel.screen.mydaily.coupon.list

import android.app.Activity
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager

class CouponListAnalyticsImpl : CouponListInterface.AnalyticsInterface {
    override fun onScreen(activity: Activity) {
        AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.MENU_COUPON_BOX, null)
    }
}