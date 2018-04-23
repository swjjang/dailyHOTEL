package com.daily.dailyhotel.screen.home.stay.inbound.detail.rooms

import com.daily.base.BaseAnalyticsInterface
import com.daily.base.BaseDialogViewInterface
import com.daily.base.OnBaseEventListener
import com.daily.dailyhotel.entity.Room

interface StayRoomsInterface {
    interface ViewInterface : BaseDialogViewInterface {
        fun setRoomList(roomList : MutableList<Room>, position: Int)

        fun setGuideVisible(visible: Boolean)
    }

    interface OnEventListener : OnBaseEventListener {

        fun onScrolled(position: Int, real: Boolean)
    }

    interface AnalyticsInterface : BaseAnalyticsInterface {
    }
}
