package com.daily.dailyhotel.screen.mydaily.coupon.dialog

import com.daily.base.BaseDialogView
import com.twoheart.dailyhotel.databinding.ActivitySelectCouponDialogDataBinding

class SelectStayCouponDialogView(activity: SelectStayCouponDialogActivity, listener: SelectStayCouponDialogInterface.OnEventListener)//
    : BaseDialogView<SelectStayCouponDialogInterface.OnEventListener, ActivitySelectCouponDialogDataBinding>(activity, listener), SelectStayCouponDialogInterface.ViewInterface {

    override fun setContentView(viewDataBinding: ActivitySelectCouponDialogDataBinding) {
//        initToolbar(viewDataBinding)
    }

    override fun setToolbarTitle(title: String?) {
//        viewDataBinding.toolbarView.setTitleText(title)
    }

    private fun initToolbar(viewDataBinding: ActivitySelectCouponDialogDataBinding) {
//        viewDataBinding.toolbarView.setOnBackClickListener { eventListener.onBackClick() }
    }
}