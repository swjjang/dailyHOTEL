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
        fun newInstance(context: Context, roomList: List<Room>? = ArrayList<Room>()
                        , position: Int
                        , checkInDate: String?, checkOutDate: String?
                        , stayIndex: Int?, category: String?
                        , activeReward: Boolean = false): Intent {
            return Intent(context, StayRoomsActivity::class.java).apply {

                val list = arrayListOf<RoomParcel>()
                roomList?.forEach { list.add(RoomParcel(it)) }

                putParcelableArrayListExtra(INTENT_EXTRA_ROOM_LIST, list)
                putExtra(INTENT_EXTRA_POSITION, position)
                putExtra(INTENT_EXTRA_CHECK_IN_DATE, checkInDate?.let { it } ?: "")
                putExtra(INTENT_EXTRA_CHECK_OUT_DATE, checkOutDate?.let { it } ?: "")
                putExtra(INTENT_EXTRA_STAY_INDEX, stayIndex?.let { it } ?: 0)
                putExtra(INTENT_EXTRA_STAY_CATEGORY, category?.let { it } ?: "")
                putExtra(INTENT_EXTRA_ACTIVE_REWARD, activeReward)
            }
        }

        const val INTENT_EXTRA_ROOM_LIST = "roomList"
        const val INTENT_EXTRA_ACTIVE_REWARD = "activeReward"
        const val INTENT_EXTRA_CHECK_IN_DATE = "checkInDate"
        const val INTENT_EXTRA_CHECK_OUT_DATE = "checkOutDate"
        const val INTENT_EXTRA_STAY_INDEX = "stayIndex"
        const val INTENT_EXTRA_STAY_CATEGORY = "category"
        const val INTENT_EXTRA_POSITION = "position"
        const val INTENT_EXTRA_ROOM = "room"

        const val REQUEST_CODE_TRUE_VR = 10000
        const val REQUEST_CODE_IMAGE_LIST = 10001
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