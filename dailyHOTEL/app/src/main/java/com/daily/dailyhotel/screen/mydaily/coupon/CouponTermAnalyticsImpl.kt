package com.daily.dailyhotel.screen.mydaily.coupon

import android.app.Activity
import com.daily.dailyhotel.screen.common.web.DailyWebAnalyticsImpl
import com.daily.dailyhotel.util.isTextEmpty
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager

class CouponTermAnalyticsImpl : DailyWebAnalyticsImpl(), CouponTermInterface.AnalyticsInterface {
    override fun onScreen(activity: Activity?, couponIndex: String?) {
        if (activity == null) {
            return
        }

        val screen = if (couponIndex.isTextEmpty()) {
            AnalyticsManager.Screen.MENU_COUPON_GENERAL_TERMS_OF_USE
        } else {
            AnalyticsManager.Screen.MENU_COUPON_INDIVIDUAL_TERMS_OF_USE
        }

        AnalyticsManager.getInstance(activity).recordScreen(activity, screen, null)
    }
}
