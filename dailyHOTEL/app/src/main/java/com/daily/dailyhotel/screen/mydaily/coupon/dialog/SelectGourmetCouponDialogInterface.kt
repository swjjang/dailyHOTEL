package com.daily.dailyhotel.screen.mydaily.coupon.dialog

import android.app.Activity
import com.daily.base.BaseAnalyticsInterface
import com.daily.base.BaseDialogViewInterface
import com.daily.base.OnBaseEventListener
import com.daily.dailyhotel.entity.Coupon

interface SelectGourmetCouponDialogInterface {
    interface ViewInterface : BaseDialogViewInterface {
        fun setVisibility(visible: Boolean)

        fun setTitle(resId: Int)

        fun setOneButtonLayout(visible: Boolean, resId: Int)

        fun setTwoButtonLayout(visible: Boolean, positiveResId: Int, negativeResId: Int)

        fun setData(list: MutableList<Coupon>?, selected: Boolean)

        fun getCouponCount(): Int
    }

    interface OnEventListener : OnBaseEventListener {
        fun setResult(coupon: Coupon)

        fun onCouponDownloadClick(coupon: Coupon)
    }

    interface AnalyticsInterface : BaseAnalyticsInterface {
        fun onScreen(activity: Activity, emptyList: Boolean)

        fun onCancelByPayment(activity: Activity, couponCount: Int)

        fun onSelectedCouponResult(activity: Activity, title: String)

        fun onDownloadCoupon(activity: Activity, callByScreen: String, coupon: Coupon)
    }
}
