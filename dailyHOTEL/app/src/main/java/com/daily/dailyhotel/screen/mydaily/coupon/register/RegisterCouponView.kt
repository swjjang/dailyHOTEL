package com.daily.dailyhotel.screen.mydaily.coupon.register

import com.daily.base.BaseDialogView
import com.twoheart.dailyhotel.databinding.ActivityCopyDataBinding

class RegisterCouponView(activity: RegisterCouponActivity, listener: RegisterCouponInterface.OnEventListener)//
    : BaseDialogView<RegisterCouponInterface.OnEventListener, ActivityCopyDataBinding>(activity, listener), RegisterCouponInterface.ViewInterface {

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