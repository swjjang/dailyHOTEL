package com.daily.dailyhotel.screen.mydaily.coupon

import android.app.Activity

import com.daily.dailyhotel.screen.common.web.DailyWebInterface

interface CouponTermInterface {
    interface ViewInterface : DailyWebInterface.ViewInterface

    interface OnEventListener : DailyWebInterface.OnEventListener

    interface AnalyticsInterface : DailyWebInterface.AnalyticsInterface {
        fun onScreen(activity: Activity?, couponIndex: String?)
    }
}
