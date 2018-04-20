package com.daily.dailyhotel.screen.home.stay.inbound.detail.rooms

import com.daily.base.BaseDialogView
import com.twoheart.dailyhotel.databinding.ActivityCopyDataBinding

class StayRoomsView(activity: StayRoomsActivity, listener: StayRoomsInterface.OnEventListener)//
    : BaseDialogView<StayRoomsInterface.OnEventListener, ActivityCopyDataBinding>(activity, listener), StayRoomsInterface.ViewInterface {

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