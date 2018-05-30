package com.daily.dailyhotel.screen.mydaily.coupon.dialog

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import com.daily.base.util.ExLog
import com.daily.dailyhotel.base.BaseExceptionPresenter
import com.daily.dailyhotel.entity.Coupon
import com.daily.dailyhotel.entity.Coupons
import com.daily.dailyhotel.parcel.CouponParcel
import com.daily.dailyhotel.repository.remote.CouponRemoteImpl
import com.daily.dailyhotel.util.isTextEmpty
import com.daily.dailyhotel.util.runTrue
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.model.time.StayBookingDay
import com.twoheart.dailyhotel.util.Constants
import com.twoheart.dailyhotel.util.DailyCalendar
import com.twoheart.dailyhotel.util.Util
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

class SelectStayCouponDialogPresenter(activity: SelectStayCouponDialogActivity)//
    : BaseExceptionPresenter<SelectStayCouponDialogActivity, SelectStayCouponDialogInterface.ViewInterface>(activity), SelectStayCouponDialogInterface.OnEventListener {

    private val analytics: SelectStayCouponDialogInterface.AnalyticsInterface by lazy {
        SelectStayCouponDialogAnalyticsImpl()
    }

    private var isSetOk: Boolean = false
    private var stayIndex: Int = -1
    private var roomIndex: Int = -1
    private var roomPrice: Int = 0
    private var maxCouponAmount: Int = 0
    private var categoryCode: String = ""
    private var stayName: String = ""
    private var hasDownloadable = false

    private lateinit var callByScreen: String
    private val stayBookingDay: StayBookingDay by lazy {
        StayBookingDay()
    }

    private val couponRemoteImpl: CouponRemoteImpl by lazy {
        CouponRemoteImpl()
    }

    override fun createInstanceViewInterface(): SelectStayCouponDialogInterface.ViewInterface {
        return SelectStayCouponDialogView(activity, this)
    }

    override fun constructorInitialize(activity: SelectStayCouponDialogActivity) {
        setContentView(R.layout.activity_select_coupon_dialog_data)

        isRefresh = true
    }

    override fun onPostCreate() {
    }

    override fun onIntent(intent: Intent?): Boolean {
        var result = false

        intent?.run {
            callByScreen = getStringExtra(Constants.NAME_INTENT_EXTRA_DATA_CALL_BY_SCREEN)
            if (callByScreen.isTextEmpty()) {
                Util.restartApp(activity)
                result = false
            }

            try {
                stayBookingDay.setCheckInDay(getStringExtra(SelectStayCouponDialogActivity.INTENT_EXTRA_CHECK_IN_DATE))
                stayBookingDay.setCheckOutDay(getStringExtra(SelectStayCouponDialogActivity.INTENT_EXTRA_CHECK_OUT_DATE))
            } catch (e: Exception) {
                ExLog.e(e.toString())

                Util.restartApp(activity)
                result = false
            }

            stayIndex = getIntExtra(SelectStayCouponDialogActivity.INTENT_EXTRA_STAY_IDX, -1)
            categoryCode = getStringExtra(SelectStayCouponDialogActivity.INTENT_EXTRA_CATEGORY_CODE)
            stayName = getStringExtra(SelectStayCouponDialogActivity.INTENT_EXTRA_STAY_NAME)

            if (AnalyticsManager.Screen.DAILYHOTEL_BOOKINGINITIALISE.equals(callByScreen, true)) {
                roomIndex = getIntExtra(SelectStayCouponDialogActivity.INTENT_EXTRA_ROOM_IDX, -1)
                roomPrice = getIntExtra(SelectStayCouponDialogActivity.INTENT_EXTRA_ROOM_PRICE, 0)
            }

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

    override fun onFinish() {
        super.onFinish()

        if (!isSetOk) {
            if (AnalyticsManager.Screen.DAILYHOTEL_BOOKINGINITIALISE.equals(callByScreen, true)) {
                analytics.onCancelByPayment(activity, viewInterface.getCouponCount(), categoryCode, stayName, roomPrice)
            }

            setFinishResult()
        }
    }

    private fun setFinishResult() {
        val intent = Intent().putExtra(SelectStayCouponDialogActivity.INTENT_EXTRA_MAX_COUPON_AMOUNT, maxCouponAmount)

        if (AnalyticsManager.Screen.DAILYHOTEL_DETAIL.equals(callByScreen, true)) {
            intent.putExtra(SelectStayCouponDialogActivity.INTENT_EXTRA_HAS_DOWNLOADABLE_COUPON, hasDownloadable)
        }

        activity.setResult(Activity.RESULT_CANCELED, intent)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBackPressed(): Boolean {
        return super.onBackPressed()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        unLockAll()
    }

    @Synchronized
    override fun onRefresh(showProgress: Boolean) {
        if (isFinish || !isRefresh) {
            unLockAll()
            return
        }

        isRefresh = false
        screenLock(showProgress)

        val observable: Observable<Coupons>? = when (callByScreen) {
            AnalyticsManager.Screen.DAILYHOTEL_BOOKINGINITIALISE -> {
                couponRemoteImpl.getStayCouponListByPayment(stayIndex, roomIndex, stayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT), stayBookingDay.getCheckOutDay(DailyCalendar.ISO_8601_FORMAT))
            }

            AnalyticsManager.Screen.DAILYHOTEL_DETAIL -> {
                couponRemoteImpl.getStayCouponListByDetail(stayIndex, stayBookingDay.getCheckInDay("yyyy-MM-dd"), stayBookingDay.nights)
            }

            else -> {
                null
            }
        }

        if (observable == null) {
            ExLog.d("sam : observable is null. check callByScreen = $callByScreen")
            unLockAll()
            showEmptyCouponListDialog()
            return
        }

        addCompositeDisposable(observable.observeOn(AndroidSchedulers.mainThread()).subscribe({
            val list: MutableList<Coupon> = it.coupons?.let { it } ?: mutableListOf()
            maxCouponAmount = it.maxCouponAmount

            when (callByScreen) {
                AnalyticsManager.Screen.DAILYHOTEL_BOOKINGINITIALISE -> {
                    if (list.isEmpty()) {
                        showEmptyCouponListDialog()
                    } else {
                        viewInterface.setVisibility(true)
                        viewInterface.setTitle(R.string.label_select_coupon)
                        viewInterface.setTwoButtonLayout(true, R.string.dialog_btn_text_select, R.string.dialog_btn_text_cancel)

                        viewInterface.setData(list, true)
                    }

                    analytics.onScreen(activity, list.isEmpty())
                }

                AnalyticsManager.Screen.DAILYHOTEL_DETAIL -> {
                    viewInterface.setVisibility(true)

                    hasDownloadable = list.any { !it.isDownloaded }

                    viewInterface.setTitle(if (hasDownloadable) R.string.coupon_download_coupon else R.string.coupon_dont_download_coupon)
                    viewInterface.setOneButtonLayout(true, R.string.dialog_btn_text_close)
                    viewInterface.setData(list, false)
                }

                else -> {
                    showEmptyCouponListDialog()
                }
            }

            unLockAll()

        }, {
            onHandleErrorAndFinish(it)
        }))
    }

    override fun onBackClick() {
        activity.onBackPressed()
    }

    override fun setResult(coupon: Coupon) {
        if (lock()) {
            return
        }

        isSetOk = true

        val intent = Intent()
                .putExtra(SelectStayCouponDialogActivity.INTENT_EXTRA_SELECT_COUPON, CouponParcel(coupon))
                .putExtra(SelectStayCouponDialogActivity.INTENT_EXTRA_MAX_COUPON_AMOUNT, maxCouponAmount)

        if (AnalyticsManager.Screen.DAILYHOTEL_DETAIL.equals(callByScreen, true)) {
            intent.putExtra(SelectStayCouponDialogActivity.INTENT_EXTRA_HAS_DOWNLOADABLE_COUPON, hasDownloadable)
        }

        setResult(Activity.RESULT_OK, intent)

        finish()

        analytics.onSelectedCouponResult(activity, coupon.title)
    }

    override fun onCouponDownloadClick(coupon: Coupon) {
        if (lock()) {
            return
        }

        screenLock(true)

        addCompositeDisposable(couponRemoteImpl.getDownloadCoupon(coupon.couponCode)
                .observeOn(AndroidSchedulers.mainThread()).subscribe({
                    analytics.onDownloadCoupon(activity, callByScreen, coupon)

                    isRefresh = true
                    onRefresh(true)
                }, {
                    onHandleError(it)
                }))
    }

    private fun showEmptyCouponListDialog() {
        viewInterface.setVisibility(false)
        viewInterface.showSimpleDialog(getString(R.string.label_booking_select_coupon), getString(R.string.message_select_coupon_empty),
                getString(R.string.dialog_btn_text_confirm), null, DialogInterface.OnDismissListener {
            onBackClick()
        })
    }
}