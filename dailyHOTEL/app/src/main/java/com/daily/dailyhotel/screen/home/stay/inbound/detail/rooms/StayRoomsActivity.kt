package com.daily.dailyhotel.screen.home.stay.inbound.detail.rooms

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.daily.base.BaseActivity
import com.daily.dailyhotel.entity.Room
import com.daily.dailyhotel.parcel.RoomParcel
import com.twoheart.dailyhotel.R

class StayRoomsActivity : BaseActivity<StayRoomsPresenter>() {

    companion object {
        @JvmStatic
        fun newInstance(context: Context, roomList: ArrayList<Room> = ArrayList<Room>(), activeReward: Boolean = false): Intent {
            return Intent(context, StayRoomsActivity::class.java).apply {

                val list = arrayListOf<RoomParcel>()
                for (room in roomList) {
                    list.add(RoomParcel(room))
                }

                putParcelableArrayListExtra(INTENT_EXTRA_ROOM_LIST, list)
                putExtra(INTENT_EXTRA_ACTIVE_REWARD, activeReward)
            }
        }

        const val INTENT_EXTRA_ROOM_LIST = "roomList"
        const val INTENT_EXTRA_ACTIVE_REWARD = "activeReward"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold)

        super.onCreate(savedInstanceState)
    }

    override fun createInstancePresenter(): StayRoomsPresenter {
        return StayRoomsPresenter(this)
    }

    override fun finish() {
        super.finish()

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right)
    }
}