package com.daily.dailyhotel.screen.mydaily.coupon.list

import com.daily.base.BaseAnalyticsInterface
import com.daily.base.BaseDialogViewInterface
import com.daily.base.OnBaseEventListener
import com.twoheart.dailyhotel.model.Coupon

interface CouponListInterface {
    interface ViewInterface : BaseDialogViewInterface {
    }

    interface OnEventListener : OnBaseEventListener {
        fun startCouponHistory()

        fun startNotice()

        fun startRegisterCoupon()

        fun showListItemNotice(coupon: Coupon)

        fun onListItemDownLoadClick(coupon: Coupon)

        fun onItemSelectedSpinner(position: Int)
    }

    interface AnalyticsInterface : BaseAnalyticsInterface {
    }
}
