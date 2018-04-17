package com.daily.dailyhotel.screen.mydaily.coupon.dialog

import com.daily.base.BaseDialogView
import com.twoheart.dailyhotel.databinding.ActivityCopyDataBinding

class SelectStayCouponDialogView(activity: SelectStayCouponDialogActivity, listener: SelectStayCouponDialogInterface.OnEventListener)//
    : BaseDialogView<SelectStayCouponDialogInterface.OnEventListener, ActivityCopyDataBinding>(activity, listener), SelectStayCouponDialogInterface.ViewInterface {

    override fun setContentView(viewDataBinding: ActivityCopyDataBinding) {
        initToolbar(viewDataBinding)
    }

    override fun setToolbarTitle(title: String?) {
        viewDataBinding.toolbarView.setTitleText(title)
    }

    private fun initToolbar(viewDataBinding: ActivityCopyDataBinding) {
        viewDataBinding.toolbarView.setOnBackClickListener { eventListener.onBackClick() }
    }
}