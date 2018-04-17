package com.daily.dailyhotel.screen.mydaily.coupon.register

import android.app.Activity
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager

class RegisterCouponAnalyticsImpl : RegisterCouponInterface.AnalyticsInterface {
    override fun onScreen(activity: Activity) {
        activity.let {
            AnalyticsManager.getInstance(it).recordScreen(it, AnalyticsManager.Screen.MENU_COUPON_REGISTRATION, null)
        }
    }

    override fun onRegistrationClick(activity: Activity, callByScreen: String?) {
        activity.let {
            AnalyticsManager.getInstance(it).recordEvent(AnalyticsManager.Category.COUPON_BOX, AnalyticsManager.Action.REGISTRATION_CLICKED, callByScreen, null)
        }
    }

    override fun onRegistrationResult(activity: Activity, isSuccess: Boolean, couponCode: String, params: Map<String, String>) {
        activity.let {
            val action: String = if (isSuccess) AnalyticsManager.Action.REGISTRATION_COMPLETE else AnalyticsManager.Action.REGISTRATION_REJECTED
            AnalyticsManager.getInstance(it).recordEvent(AnalyticsManager.Category.COUPON_BOX, action, couponCode, params)
        }
    }
}