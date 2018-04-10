package com.daily.dailyhotel.screen.mydaily.coupon.list

import com.daily.base.BaseDialogView
import com.twoheart.dailyhotel.databinding.ActivityCouponListDataBinding

class CouponListView(activity: CouponListActivity, listener: CouponListInterface.OnEventListener)//
    : BaseDialogView<CouponListInterface.OnEventListener, ActivityCouponListDataBinding>(activity, listener), CouponListInterface.ViewInterface {

    override fun setContentView(viewDataBinding: ActivityCouponListDataBinding) {
        initToolbar(viewDataBinding)
    }

    override fun setToolbarTitle(title: String?) {
        viewDataBinding.toolbarView.setTitleText(title)
    }

    private fun initToolbar(viewDataBinding: ActivityCouponListDataBinding) {
        viewDataBinding.toolbarView.setOnBackClickListener { eventListener.onBackClick() }
    }
}