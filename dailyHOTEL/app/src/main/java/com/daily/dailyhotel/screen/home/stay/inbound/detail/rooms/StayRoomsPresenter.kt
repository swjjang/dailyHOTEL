package com.daily.dailyhotel.screen.home.stay.inbound.detail.rooms

import android.content.Intent
import com.daily.dailyhotel.base.BaseExceptionPresenter
import com.daily.dailyhotel.entity.Room
import com.daily.dailyhotel.parcel.RoomParcel
import com.twoheart.dailyhotel.R

class StayRoomsPresenter(activity: StayRoomsActivity)//
    : BaseExceptionPresenter<StayRoomsActivity, StayRoomsInterface.ViewInterface>(activity), StayRoomsInterface.OnEventListener {

    private val roomList = mutableListOf<Room>()
    private var activeReward:Boolean = false

    private val analytics: StayRoomsInterface.AnalyticsInterface by lazy {
        StayRoomsAnalyticsImpl()
    }

    override fun createInstanceViewInterface(): StayRoomsInterface.ViewInterface {
        return StayRoomsView(activity, this)
    }

    override fun constructorInitialize(activity: StayRoomsActivity) {
        setContentView(R.layout.activity_stay_rooms_data)

        isRefresh = true
    }

    override fun onPostCreate() {
    }

    override fun onIntent(intent: Intent?): Boolean {
        return intent?.let {
            val parcelList: ArrayList<RoomParcel> = it.getParcelableArrayListExtra(StayRoomsActivity.INTENT_EXTRA_ROOM_LIST)
            for (parcel in parcelList) {
                roomList += parcel.room
            }

            activeReward = it.getBooleanExtra(StayRoomsActivity.INTENT_EXTRA_ACTIVE_REWARD, false)
            true
        } ?: true
    }

    override fun onNewIntent(intent: Intent?) {
    }

    override fun onStart() {
        super.onStart()

        if (isRefresh) {
            onRefresh(true)
        }
    }

    override fun onResume() {
        super.onResume()

        if (isRefresh) {
            onRefresh(true)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        unLockAll()
    }

    @Synchronized
    override fun onRefresh(showProgress: Boolean) {
        if (isFinish || !isRefresh) {
            return
        }

        isRefresh = false
        screenLock(showProgress)
    }

    override fun onBackClick() {
        activity.onBackPressed()
    }
}