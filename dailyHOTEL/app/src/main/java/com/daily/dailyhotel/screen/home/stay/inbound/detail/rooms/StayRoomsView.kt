package com.daily.dailyhotel.screen.home.stay.inbound.detail.rooms

import com.daily.base.BaseDialogView
import com.twoheart.dailyhotel.databinding.ActivityStayRoomsDataBinding

class StayRoomsView(activity: StayRoomsActivity, listener: StayRoomsInterface.OnEventListener)//
    : BaseDialogView<StayRoomsInterface.OnEventListener, ActivityStayRoomsDataBinding>(activity, listener), StayRoomsInterface.ViewInterface {

    override fun setContentView(viewDataBinding: ActivityStayRoomsDataBinding) {
    }

    override fun setToolbarTitle(title: String?) {
    }

}