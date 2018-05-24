package com.daily.dailyhotel.screen.home.stay.inbound.detail.rooms

import android.content.DialogInterface
import android.view.View
import android.widget.CompoundButton
import com.daily.base.BaseAnalyticsInterface
import com.daily.base.BaseDialogViewInterface
import com.daily.base.OnBaseEventListener
import com.daily.dailyhotel.entity.Room
import io.reactivex.Observable

interface StayRoomsInterface {
    interface ViewInterface : BaseDialogViewInterface {
        fun setIndicatorText(position: Int)

        fun setBookingButtonText(position: Int)

        fun setNights(nights: Int)

        fun setRoomList(roomList: MutableList<Room>, position: Int)

        fun notifyDataSetChanged()

        fun setInvisibleData(position: Int)

        fun setGuideVisible(visible: Boolean)

        fun hideGuideAnimation(): Observable<Boolean>

        fun showVrDialog(checkedChangeListener: CompoundButton.OnCheckedChangeListener
                         , positiveListener: View.OnClickListener
                         , onDismissListener: DialogInterface.OnDismissListener)
    }

    interface OnEventListener : OnBaseEventListener {
        fun onCloseClick()

        fun onGuideClick()

        fun onBookingClick()

        fun onScrolled(position: Int, real: Boolean)

        fun onMoreImageClick(position: Int)

        fun onVrImageClick(position: Int)
    }

    interface AnalyticsInterface : BaseAnalyticsInterface {
    }
}
