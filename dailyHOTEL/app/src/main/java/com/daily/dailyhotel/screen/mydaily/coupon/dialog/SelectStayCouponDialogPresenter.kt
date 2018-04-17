package com.daily.dailyhotel.screen.mydaily.coupon.dialog

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.daily.base.util.ExLog
import com.daily.dailyhotel.base.BaseExceptionPresenter
import com.daily.dailyhotel.repository.remote.CouponRemoteImpl
import com.daily.dailyhotel.util.isTextEmpty
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.model.time.StayBookingDay
import com.twoheart.dailyhotel.util.Constants
import com.twoheart.dailyhotel.util.Util
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager

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
    private var categoryCode: String? = null
    private var stayName: String? = null
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
        return intent?.run {
            callByScreen = getStringExtra(Constants.NAME_INTENT_EXTRA_DATA_CALL_BY_SCREEN)
            if (callByScreen.isTextEmpty()) {
                Util.restartApp(activity)
                false
            }

            try {
                stayBookingDay.setCheckInDay(getStringExtra(SelectStayCouponDialogActivity.INTENT_EXTRA_CHECK_IN_DATE))
                stayBookingDay.setCheckOutDay(getStringExtra(SelectStayCouponDialogActivity.INTENT_EXTRA_CHECK_OUT_DATE))
            } catch (e: Exception) {
                ExLog.e(e.toString())
                Util.restartApp(activity)
                false
            }

            stayIndex = getIntExtra(SelectStayCouponDialogActivity.INTENT_EXTRA_STAY_IDX, -1)
            categoryCode = getStringExtra(SelectStayCouponDialogActivity.INTENT_EXTRA_CATEGORY_CODE)
            stayName = getStringExtra(SelectStayCouponDialogActivity.INTENT_EXTRA_STAY_NAME)

            if (AnalyticsManager.Screen.DAILYHOTEL_BOOKINGINITIALISE.equals(callByScreen, true)) {
                roomIndex = getIntExtra(SelectStayCouponDialogActivity.INTENT_EXTRA_ROOM_IDX, -1)
                roomPrice = getIntExtra(SelectStayCouponDialogActivity.INTENT_EXTRA_ROOM_PRICE, 0)
            }

            true
        } ?: false
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

    override fun onFinish() {
        super.onFinish()

        // activity finish() 이전에 해야 함 - 임시
        if (!isSetOk) {

            if (AnalyticsManager.Screen.DAILYHOTEL_BOOKINGINITIALISE.equals(callByScreen, true)) {
                analytics.onCancelByPayment(activity, viewInterface.getCouponCount(), categoryCode, stayName, roomPrice)
            }

            val intent = Intent()
            intent.putExtra(SelectStayCouponDialogActivity.INTENT_EXTRA_MAX_COUPON_AMOUNT, maxCouponAmount)
            activity.setResult(Activity.RESULT_CANCELED, intent)
        }
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
            return
        }

        isRefresh = false
        screenLock(showProgress)
    }

    override fun onBackClick() {
        activity.onBackPressed()
    }
}