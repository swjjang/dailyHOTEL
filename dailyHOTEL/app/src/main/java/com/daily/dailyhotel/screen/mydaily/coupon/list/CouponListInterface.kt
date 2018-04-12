package com.daily.dailyhotel.screen.mydaily.coupon.list

import android.app.Activity
import com.daily.base.BaseAnalyticsInterface
import com.daily.base.BaseDialogViewInterface
import com.daily.base.OnBaseEventListener
import com.daily.dailyhotel.entity.Coupon

interface CouponListInterface {
    interface ViewInterface : BaseDialogViewInterface {
        fun setSelectionSpinner(sortType: CouponListActivity.SortType)

        fun setData(list: List<Coupon>, sortType: CouponListActivity.SortType, isScrollTop: Boolean)
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
        fun onScreen(activity: Activity)

        fun onDownloadCoupon(activity: Activity, coupon: Coupon)
    }
}
