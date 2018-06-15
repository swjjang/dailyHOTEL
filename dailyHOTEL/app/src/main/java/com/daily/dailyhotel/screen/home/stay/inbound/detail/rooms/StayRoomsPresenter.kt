package com.daily.dailyhotel.screen.home.stay.inbound.detail.rooms

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.view.View
import android.widget.CompoundButton
import com.daily.base.util.ExLog
import com.daily.base.widget.DailyToast
import com.daily.dailyhotel.base.BaseExceptionPresenter
import com.daily.dailyhotel.entity.*
import com.daily.dailyhotel.parcel.RoomParcel
import com.daily.dailyhotel.repository.remote.ProfileRemoteImpl
import com.daily.dailyhotel.repository.remote.StayRemoteImpl
import com.daily.dailyhotel.screen.common.images.ImageListActivity
import com.daily.dailyhotel.storage.preference.DailyPreference
import com.daily.dailyhotel.util.isTextEmpty
import com.daily.dailyhotel.util.runFalse
import com.daily.dailyhotel.util.runTrue
import com.daily.dailyhotel.util.takeNotEmpty
import com.twoheart.dailyhotel.DailyHotel
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.model.Customer
import com.twoheart.dailyhotel.screen.common.TrueVRActivity
import com.twoheart.dailyhotel.screen.mydaily.member.AddProfileSocialActivity
import com.twoheart.dailyhotel.screen.mydaily.member.EditProfilePhoneActivity
import com.twoheart.dailyhotel.screen.mydaily.member.LoginActivity
import com.twoheart.dailyhotel.util.Constants
import com.twoheart.dailyhotel.util.Util
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
    private val profileRemoteImpl = ProfileRemoteImpl()

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
        var result = true
        intent?.run {
            val parcelList: ArrayList<RoomParcel> = getParcelableArrayListExtra(StayRoomsActivity.INTENT_EXTRA_ROOM_LIST)
            parcelList.forEach { roomList += it.room }

            if (roomList.isEmpty()) {
                result = false
            }

            val checkInDate = getStringExtra(StayRoomsActivity.INTENT_EXTRA_CHECK_IN_DATE)
            val checkOutDate = getStringExtra(StayRoomsActivity.INTENT_EXTRA_CHECK_OUT_DATE)
            if (checkInDate.isTextEmpty() || checkOutDate.isTextEmpty()) {
                result = false
            }

            bookDateTime.setBookDateTime(checkInDate, checkOutDate)

            stayIndex = getIntExtra(StayRoomsActivity.INTENT_EXTRA_STAY_INDEX, 0)
            if (stayIndex == 0) {
                result = false
            }

            category = getStringExtra(StayRoomsActivity.INTENT_EXTRA_STAY_CATEGORY)
            position = getIntExtra(StayRoomsActivity.INTENT_EXTRA_POSITION, 0)
            activeReward = getBooleanExtra(StayRoomsActivity.INTENT_EXTRA_ACTIVE_REWARD, false)

            result = true
        }

        return result
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

        when (requestCode) {
            Constants.CODE_REQUEST_ACTIVITY_LOGIN,
            StayRoomsActivity.REQUEST_CODE_PROFILE_UPDATE -> {
                (resultCode == Activity.RESULT_OK).runTrue {
                    onBookingClick()
                }
            }

            else -> {
            }
        }
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

        addCompositeDisposable(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
            viewInterface.notifyDataSetChanged()
            viewInterface.setRecyclerPosition(position)
            viewInterface.initInvisibleLayout(position)
            onScrolled(position, false)
            viewInterface.setInvisibleData(position)
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

        when (DailyHotel.isLogin()) {
            false -> {
                DailyToast.showToast(activity, R.string.toast_msg_please_login, DailyToast.LENGTH_LONG)
                val intent = LoginActivity.newInstance(activity)
                startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_LOGIN)
            }

            true -> {
                (centerPosition in 0..roomList.size).runTrue {
                    val room = roomList[centerPosition]

                    addCompositeDisposable(profileRemoteImpl.profile.subscribe({ user ->
                        when (Util.verifyUserInformation(user)) {
                            Util.VERIFY_USER -> {
                                val consecutive: Boolean = room.roomChargeInformation?.consecutiveInformation?.enable
                                        ?: false

                                (bookDateTime.nights > 1 && consecutive).runTrue {
                                    viewInterface.showSimpleDialog(getString(R.string.dialog_notice2)
                                            , getString(R.string.dialog_message_check_consecutive)
                                            , getString(R.string.label_do_booking)
                                            , getString(R.string.dialog_btn_text_cancel2)
                                            , View.OnClickListener {
                                        analytics.onBookingClick(activity, stayIndex, room.index)

                                        finishRoomDetailList(room.index)
                                    }
                                            , View.OnClickListener { unLockAll() }
                                            , DialogInterface.OnCancelListener { unLockAll() }
                                            , null, true)
                                    return@subscribe
                                }

                                analytics.onBookingClick(activity, stayIndex, room.index)

                                finishRoomDetailList(room.index)
                            }

                            Util.VERIFY_DAILY_USER_NOT_VERIFY_PHONE -> startActivityForResult(EditProfilePhoneActivity.newInstance(activity//
                                    , EditProfilePhoneActivity.Type.NEED_VERIFICATION_PHONENUMBER, user.phone)//
                                    , StayRoomsActivity.REQUEST_CODE_PROFILE_UPDATE)

                            Util.VERIFY_SOCIAL_USER_NOT_VERIFY, Util.VERIFY_SOCIAL_USER_NOT_VERIFY_EMAIL -> startActivityForResult(AddProfileSocialActivity.newInstance(activity//
                                    , Customer(user), user.birthday), StayRoomsActivity.REQUEST_CODE_PROFILE_UPDATE)

                            Util.VERIFY_SOCIAL_USER_NOT_VERIFY_PHONE -> startActivityForResult(EditProfilePhoneActivity.newInstance(activity//
                                    , EditProfilePhoneActivity.Type.WRONG_PHONENUMBER, user.phone)//
                                    , StayRoomsActivity.REQUEST_CODE_PROFILE_UPDATE)

                            else -> {
                            }
                        }
                    }, { throwable -> onHandleError(throwable) }))
                }
            }
        }
    }

    private fun finishRoomDetailList(roomIndex: Int) {
        val intent = Intent()
        intent.putExtra(StayRoomsActivity.INTENT_EXTRA_ROOM_INDEX, roomIndex)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onScrolled(position: Int, real: Boolean) {
        (centerPosition == position).runTrue { return }

        centerPosition = position

        ExLog.d("sam - onScrolled , position : $position")

        viewInterface.setIndicatorText(position + 1)
        viewInterface.setBookingButtonText(position)

        real.runTrue {
            (position in 0 until roomList.size).runFalse { return }
            val room = roomList[position]
            analytics.onScrolled(activity, stayIndex, room.index)
        }
    }

    override fun onMoreImageClick(position: Int) {
        lock().runTrue { return }
        (position in 0 until roomList.size).runFalse {
            unLockAll()
            return
        }

        val room = roomList[position]

        addCompositeDisposable(stayRemoteImpl.getRoomImages(stayIndex, room.index)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()
                ).subscribe({
                    val imageList = mutableListOf<DetailImageInformation>()
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
        lock().runTrue { return }
        (position in 0 until roomList.size).runFalse {
            unLockAll()
            return
        }

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
                startActivityForResult(TrueVRActivity.newInstance(activity, stayIndex, trueVrList
                        , Constants.PlaceType.HOTEL, category), StayRoomsActivity.REQUEST_CODE_TRUE_VR)
            }, DialogInterface.OnDismissListener { unLockAll() })
        } else {
            startActivityForResult(TrueVRActivity.newInstance(activity, stayIndex, trueVrList
                    , Constants.PlaceType.HOTEL, category), StayRoomsActivity.REQUEST_CODE_TRUE_VR)
        }
    }
}