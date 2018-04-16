package com.daily.dailyhotel.screen.mydaily.coupon.register

import android.app.Activity
import com.daily.base.BaseAnalyticsInterface
import com.daily.base.BaseDialogViewInterface
import com.daily.base.OnBaseEventListener

interface RegisterCouponInterface {
    interface ViewInterface : BaseDialogViewInterface {
    }

    interface OnEventListener : OnBaseEventListener {
        fun onRegisterCouponClick(couponCode: String)
    }

    interface AnalyticsInterface : BaseAnalyticsInterface {
        fun onScreen(activity: Activity)

        fun onRegistrationClick(activity: Activity, callByScreen: String?)

        fun onRegistrationResult(activity: Activity, isSuccess: Boolean, couponCode: String, params: Map<String, String>)
    }
}
