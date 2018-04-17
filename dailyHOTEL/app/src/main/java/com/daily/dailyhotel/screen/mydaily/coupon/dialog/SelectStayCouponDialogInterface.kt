package com.daily.dailyhotel.screen.mydaily.coupon.dialog

import android.app.Activity
import com.daily.base.BaseAnalyticsInterface
import com.daily.base.BaseDialogViewInterface
import com.daily.base.OnBaseEventListener

interface SelectStayCouponDialogInterface {
    interface ViewInterface : BaseDialogViewInterface {
    }

    interface OnEventListener : OnBaseEventListener {
    }

    interface AnalyticsInterface : BaseAnalyticsInterface {
        fun onCancelByPayment(activity: Activity, couponCount: Int, categoryCode: String, stayName: String, roomPrice: Int)
    }
}
