package com.daily.dailyhotel.screen.copy.kotlin

import com.daily.base.BaseDialogView
import com.twoheart.dailyhotel.databinding.ActivityCopyDataBinding

class CopyView(activity: CopyActivity, listener: CopyInterface.OnEventListener)//
    : BaseDialogView<CopyInterface.OnEventListener, ActivityCopyDataBinding>(activity, listener), CopyInterface.ViewInterface {

    override fun setContentView(viewDataBinding: ActivityCopyDataBinding) {
        initToolbar(viewDataBinding)
    }

    override fun setToolbarTitle(title: String?) {
        viewDataBinding.toolbarView.setTitleText(title)
    }

    private fun initToolbar(viewDataBinding: ActivityCopyDataBinding) {
        viewDataBinding.toolbarView.setOnBackClickListener { eventListener.onBackClick() };
    }
}