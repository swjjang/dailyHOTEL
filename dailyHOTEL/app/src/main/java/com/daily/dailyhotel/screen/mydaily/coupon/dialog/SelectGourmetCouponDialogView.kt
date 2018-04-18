package com.daily.dailyhotel.screen.mydaily.coupon.dialog

import com.daily.base.BaseDialogView
import com.twoheart.dailyhotel.databinding.ActivitySelectCouponDialogDataBinding

class SelectGourmetCouponDialogView(activity: SelectGourmetCouponDialogActivity, listener: SelectGourmetCouponDialogInterface.OnEventListener)//
    : BaseDialogView<SelectGourmetCouponDialogInterface.OnEventListener, ActivitySelectCouponDialogDataBinding>(activity, listener), SelectGourmetCouponDialogInterface.ViewInterface {

    override fun setContentView(viewDataBinding: ActivitySelectCouponDialogDataBinding) {
    }

    override fun setToolbarTitle(title: String?) {
    }

}