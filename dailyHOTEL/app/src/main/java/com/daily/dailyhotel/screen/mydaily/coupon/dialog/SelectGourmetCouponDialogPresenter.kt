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
import com.twoheart.dailyhotel.util.Constants
import com.twoheart.dailyhotel.util.Util
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

class SelectGourmetCouponDialogPresenter(activity: SelectGourmetCouponDialogActivity)//
    : BaseExceptionPresenter<SelectGourmetCouponDialogActivity, SelectGourmetCouponDialogInterface.ViewInterface>(activity), SelectGourmetCouponDialogInterface.OnEventListener {

    private val analytics: SelectGourmetCouponDialogInterface.AnalyticsInterface by lazy {
        SelectGourmetCouponDialogAnalyticsImpl()
    }

    private var isSetOk: Boolean = false
    private var gourmetIndex: Int = -1
    private var ticketIndexes: IntArray = IntArray(0)
    private var ticketCounts: IntArray = IntArray(0)
    private var visitDay: String = ""
    private var gourmetName: String = ""
    private lateinit var callByScreen: String
    private var maxCouponAmount: Int = 0

    private val couponRemoteImpl: CouponRemoteImpl by lazy {
        CouponRemoteImpl()
    }

    override fun createInstanceViewInterface(): SelectGourmetCouponDialogInterface.ViewInterface {
        return SelectGourmetCouponDialogView(activity, this)
    }

    override fun constructorInitialize(activity: SelectGourmetCouponDialogActivity) {
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

            visitDay = getStringExtra(SelectGourmetCouponDialogActivity.INTENT_EXTRA_VISIT_DAY)
            gourmetIndex = getIntExtra(SelectGourmetCouponDialogActivity.INTENT_EXTRA_GOURMET_INDEX, -1)
            gourmetName = getStringExtra(SelectGourmetCouponDialogActivity.INTENT_EXTRA_GOURMET_NAME)

            if (AnalyticsManager.Screen.DAILYGOURMET_BOOKINGINITIALISE.equals(callByScreen, true)) {
                ticketIndexes = getIntArrayExtra(SelectGourmetCouponDialogActivity.INTENT_EXTRA_TICKET_INDEXES)
                ticketCounts = getIntArrayExtra(SelectGourmetCouponDialogActivity.INTENT_EXTRA_TICKET_COUNTS)
            }

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

    override fun onFinish() {
        super.onFinish()

        if (isSetOk) {
            if (AnalyticsManager.Screen.DAILYGOURMET_BOOKINGINITIALISE.equals(callByScreen, true)) {
                analytics.onCancelByPayment(activity, viewInterface.getCouponCount())
            }

            val intent: Intent = Intent().apply {
                putExtra(SelectGourmetCouponDialogActivity.INTENT_EXTRA_MAX_COUPON_AMOUNT, maxCouponAmount)
            }

            activity.setResult(Activity.RESULT_OK, intent)
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
            unLockAll()
            return
        }

        if (visitDay.isTextEmpty()) {
            Util.restartApp(activity)
            return
        }

        isRefresh = false
        screenLock(showProgress)

        var observable: Observable<Coupons>? = when (callByScreen) {
            AnalyticsManager.Screen.DAILYGOURMET_BOOKINGINITIALISE -> {
                couponRemoteImpl.getGourmetCouponListByPayment(ticketIndexes, ticketCounts)
            }

            AnalyticsManager.Screen.DAILYGOURMET_DETAIL -> {
                couponRemoteImpl.getGourmetCouponListByDetail(gourmetIndex, visitDay)
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
            val list: MutableList<Coupon> = it.coupons?.let { it } ?: mutableListOf<Coupon>()
            maxCouponAmount = it.maxCouponAmount

            when (callByScreen) {
                AnalyticsManager.Screen.DAILYGOURMET_BOOKINGINITIALISE -> {
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

                AnalyticsManager.Screen.DAILYGOURMET_DETAIL -> {
                    viewInterface.setVisibility(true)

                    var hasDownloadable = list.any {
                        !it.isDownloaded
                    }

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

        val intent = Intent().apply {
            putExtra(SelectGourmetCouponDialogActivity.INTENT_EXTRA_SELECT_COUPON, CouponParcel(coupon))
            putExtra(SelectGourmetCouponDialogActivity.INTENT_EXTRA_MAX_COUPON_AMOUNT, maxCouponAmount)
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