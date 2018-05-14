package com.daily.dailyhotel.screen.home.stay.inbound.detail.rooms

import android.content.Intent
import com.daily.dailyhotel.base.BaseExceptionPresenter
import com.daily.dailyhotel.entity.Room
import com.daily.dailyhotel.entity.StayBookDateTime
import com.daily.dailyhotel.parcel.RoomParcel
import com.daily.dailyhotel.storage.preference.DailyPreference
import com.daily.dailyhotel.util.isTextEmpty
import com.daily.dailyhotel.util.runTrue
import com.twoheart.dailyhotel.R

class StayRoomsPresenter(activity: StayRoomsActivity)//
    : BaseExceptionPresenter<StayRoomsActivity, StayRoomsInterface.ViewInterface>(activity), StayRoomsInterface.OnEventListener {

    private val roomList = mutableListOf<Room>()
    private var activeReward: Boolean = false
    private val bookDateTime = StayBookDateTime()
    private var stayIndex = 0
    private var category = ""

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
        if (DailyPreference.getInstance(activity).stayProductDetailGuide) {
            DailyPreference.getInstance(activity).stayProductDetailGuide = false
            viewInterface.setGuideVisible(true)
        }
    }

    override fun onIntent(intent: Intent?): Boolean {
        return intent?.let {
            val parcelList: ArrayList<RoomParcel> = it.getParcelableArrayListExtra(StayRoomsActivity.INTENT_EXTRA_ROOM_LIST)
            parcelList.forEach { roomList += it.room }

            if (roomList.isEmpty()) {
                false
            }

            val checkInDate = it.getStringExtra(StayRoomsActivity.INTENT_EXTRA_CHECK_IN_DATE)
            val checkOutDate = it.getStringExtra(StayRoomsActivity.INTENT_EXTRA_CHECK_IN_DATE)
            if (checkInDate.isTextEmpty() || checkOutDate.isTextEmpty()) {
                false
            }

            bookDateTime.setBookDateTime(checkInDate, checkOutDate)

            stayIndex = it.getIntExtra(StayRoomsActivity.INTENT_EXTRA_STAY_INDEX, 0)
            if (stayIndex == 0) {
                false
            }

            category = it.getStringExtra(StayRoomsActivity.INTENT_EXTRA_STAY_CATEGORY)

            activeReward = it.getBooleanExtra(StayRoomsActivity.INTENT_EXTRA_ACTIVE_REWARD, false)

            true
        } ?: true
    }

    override fun onNewIntent(intent: Intent?) {
    }

    override fun onStart() {
        super.onStart()

        isRefresh.runTrue { onRefresh(true) }
    }

    override fun onResume() {
        super.onResume()

        isRefresh.runTrue { onRefresh(true) }
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
//        screenLock(showProgress)

//        val observable: Observable<Boolean> = Observable.defer(Callable {

            val checkInDate = bookDateTime.getCheckInDateTime("M.d(EEE)")
            val checkOutDate = bookDateTime.getCheckOutDateTime("M.d(EEE)")
//            if (isTextEmpty(checkInDate, checkOutDate)) {
//                Observable.just(false)
//            }

            viewInterface.setToolbarTitle("$checkInDate - $checkOutDate ${bookDateTime.nights}박")

            viewInterface.setNights(bookDateTime.nights)
            viewInterface.setRoomList(roomList, 0)



//            Observable.just(true)
//        })
//
//        addCompositeDisposable(observable.subscribeOn(Schedulers.io()).ob)
    }

    override fun onBackClick() {
        activity.onBackPressed()
    }


    override fun onScrolled(position: Int, real: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}