package com.daily.dailyhotel.screen.home.stay.inbound.detail.rooms

import com.daily.base.BaseAnalyticsInterface
import com.daily.base.BaseDialogViewInterface
import com.daily.base.OnBaseEventListener
import com.daily.dailyhotel.entity.Room

interface StayRoomsInterface {
    interface ViewInterface : BaseDialogViewInterface {
        fun setIndicatorText(position: Int)

        fun setNights(nights: Int)

        fun setRoomList(roomList: MutableList<Room>, position: Int)

        fun notifyDataSetChanged()

        fun setGuideVisible(visible: Boolean)
    }

    interface OnEventListener : OnBaseEventListener {

        fun onScrolled(position: Int, real: Boolean)
    }

    interface AnalyticsInterface : BaseAnalyticsInterface {
    }
}
