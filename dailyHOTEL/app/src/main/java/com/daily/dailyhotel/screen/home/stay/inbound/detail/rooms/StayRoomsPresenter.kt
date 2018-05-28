package com.daily.dailyhotel.screen.home.stay.inbound.detail.rooms

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.view.View
import android.widget.CompoundButton
import com.daily.dailyhotel.base.BaseExceptionPresenter
import com.daily.dailyhotel.entity.*
import com.daily.dailyhotel.parcel.RoomParcel
import com.daily.dailyhotel.repository.remote.StayRemoteImpl
import com.daily.dailyhotel.screen.common.images.ImageListActivity
import com.daily.dailyhotel.storage.preference.DailyPreference
import com.daily.dailyhotel.util.isTextEmpty
import com.daily.dailyhotel.util.runFalse
import com.daily.dailyhotel.util.runTrue
import com.daily.dailyhotel.util.takeNotEmpty
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.screen.common.TrueVRActivity
import com.twoheart.dailyhotel.util.Constants
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class StayRoomsPresenter(activity: StayRoomsActivity)//
    : BaseExceptionPresenter<StayRoomsActivity, StayRoomsInterface.ViewInterface>(activity), StayRoomsInterface.OnEventListener {

    private val roomList = mutableListOf<Room>()
    private var activeReward: Boolean = false
    private val bookDateTime = StayBookDateTime()
    private var stayIndex = 0
    private var category = ""

    private var position = 0
    private var centerPosition = -1
    private val stayRemoteImpl = StayRemoteImpl()

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
            val checkOutDate = it.getStringExtra(StayRoomsActivity.INTENT_EXTRA_CHECK_OUT_DATE)
            if (checkInDate.isTextEmpty() || checkOutDate.isTextEmpty()) {
                false
            }

            bookDateTime.setBookDateTime(checkInDate, checkOutDate)

            stayIndex = it.getIntExtra(StayRoomsActivity.INTENT_EXTRA_STAY_INDEX, 0)
            if (stayIndex == 0) {
                false
            }

            category = it.getStringExtra(StayRoomsActivity.INTENT_EXTRA_STAY_CATEGORY)
            position = it.getIntExtra(StayRoomsActivity.INTENT_EXTRA_POSITION, 0)
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
        screenLock(showProgress)

        val observable: Observable<Boolean> = Observable.defer({
            val checkInDate = bookDateTime.getCheckInDateTime("M.d(EEE)")
            val checkOutDate = bookDateTime.getCheckOutDateTime("M.d(EEE)")
            if (isTextEmpty(checkInDate, checkOutDate)) {
                Observable.just(false)
            }

            viewInterface.setToolbarTitle("$checkInDate - $checkOutDate ${bookDateTime.nights}박")
            viewInterface.setNights(bookDateTime.nights)
            viewInterface.setRoomList(roomList, position)

            Observable.just(true)
        })
//
        addCompositeDisposable(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
            viewInterface.notifyDataSetChanged()
            onScrolled(position, false)
            unLockAll()
        }, {
            onHandleError(it)
        }))
    }

    override fun onBackClick() {
        if (viewInterface.showInvisibleLayout()) {
            viewInterface.startInvisibleLayoutAnimation(false)
            return
        }

        activity.onBackPressed()
    }

    override fun onBackPressed(): Boolean {
        if (viewInterface.showInvisibleLayout()) {
            viewInterface.startInvisibleLayoutAnimation(false)
            return true
        }

        return super.onBackPressed()
    }

    override fun onCloseClick() {
        onBackClick()
    }

    override fun onGuideClick() {
        lock().runTrue { return }

        val observable = viewInterface.hideGuideAnimation()
        addCompositeDisposable(observable.subscribeOn(AndroidSchedulers.mainThread()).subscribe({
            unLockAll()
        }, {
            unLockAll()
        }))

    }

    override fun onBookingClick() {
        lock().runTrue { return }

        // TODO : 결제 넘기는 부분 상세에서 진행 할지 Sheldon 과 이야기 필요!
        val intent = Intent()
        if (centerPosition in 0..roomList.size) {
            val room = roomList[centerPosition]
            intent.putExtra(StayRoomsActivity.INTENT_EXTRA_ROOM, RoomParcel(room))
            setResult(Activity.RESULT_OK, intent)
            finish()
        } else {
            // TODO : error 처리 필요한지 확인 필요
        }
    }

    override fun onScrolled(position: Int, real: Boolean) {
        (centerPosition == position).runTrue { return }

        centerPosition = position

        viewInterface.setIndicatorText(position + 1)
        viewInterface.setBookingButtonText(position)
        viewInterface.setInvisibleData(position)
    }

    override fun onMoreImageClick(position: Int) {
        lock().runTrue { return }

        val room = roomList[position]

        addCompositeDisposable(stayRemoteImpl.getRoomImages(stayIndex, room.index)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()
                ).subscribe({
                    var imageList = mutableListOf<DetailImageInformation>()
                    it.forEach {
                        val info = DetailImageInformation()
                        info.caption = it.description
                        info.imageMap = ImageMap().apply({
                            smallUrl = it.url
                            mediumUrl = it.url
                            bigUrl = it.url
                        })

                        imageList.add(info)
                    }

                    imageList.takeNotEmpty {
                        startActivityForResult(ImageListActivity.newInstance(activity, room.name
                                , it, 0, null), StayRoomsActivity.REQUEST_CODE_IMAGE_LIST)
                    }

                    unLockAll()
                }, {
                    onHandleError(it)
                }))
    }

    override fun onVrImageClick(position: Int) {
        (position in 0 until roomList.size).runFalse { return }
        lock().runTrue { return }

        val room = roomList[position]

        if (room.vrInformationList == null || room.vrInformationList.isEmpty()) {
            return
        }

        val trueVrList = mutableListOf<TrueVR>()
        room.vrInformationList.forEach {
            val trueVr = TrueVR()
            trueVr.name = it.name
            trueVr.type = it.type
            trueVr.typeIndex = it.typeIndex
            trueVr.url = it.url

            trueVrList.add(trueVr)
        }

        if (!DailyPreference.getInstance(activity).isTrueVRCheckDataGuide) {
            viewInterface.showVrDialog(
                    CompoundButton.OnCheckedChangeListener { _, checked -> DailyPreference.getInstance(activity).isTrueVRCheckDataGuide = checked }
                    , View.OnClickListener {
                startActivityForResult(TrueVRActivity.newInstance(activity, stayIndex, trueVrList//
                        , Constants.PlaceType.HOTEL, category), StayRoomsActivity.REQUEST_CODE_TRUE_VR)
            }, DialogInterface.OnDismissListener { unLockAll() })
        } else {
            startActivityForResult(TrueVRActivity.newInstance(activity, stayIndex, trueVrList//
                    , Constants.PlaceType.HOTEL, category), StayRoomsActivity.REQUEST_CODE_TRUE_VR)
        }
    }
}