package com.daily.dailyhotel.screen.mydaily.coupon.register

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.daily.dailyhotel.base.BaseExceptionPresenter
import com.daily.dailyhotel.repository.remote.CouponRemoteImpl
import com.twoheart.dailyhotel.DailyHotel
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.screen.mydaily.member.LoginActivity
import com.twoheart.dailyhotel.util.Constants
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager
import io.reactivex.android.schedulers.AndroidSchedulers

class RegisterCouponPresenter(activity: RegisterCouponActivity)//
    : BaseExceptionPresenter<RegisterCouponActivity, RegisterCouponInterface.ViewInterface>(activity), RegisterCouponInterface.OnEventListener {

    private val analytics: RegisterCouponInterface.AnalyticsInterface by lazy {
        RegisterCouponAnalyticsImpl()
    }

    private val couponRemoteImpl: CouponRemoteImpl by lazy {
        CouponRemoteImpl()
    }

    private var callByScreen: String? = null

    override fun createInstanceViewInterface(): RegisterCouponInterface.ViewInterface {
        return RegisterCouponView(activity, this)
    }

    override fun constructorInitialize(activity: RegisterCouponActivity) {
        setContentView(R.layout.activity_register_coupon_data)

        isRefresh = true
    }

    override fun onPostCreate() {
        analytics.onRegistrationClick(activity, callByScreen)
    }

    override fun onIntent(intent: Intent?): Boolean {
        return intent?.let {
            callByScreen = it.getStringExtra(RegisterCouponActivity.EXTRA_DATA_CALL_BY_SCREEN)

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

        if (!DailyHotel.isLogin()) {
            showLoginDialog()
        }
    }

    override fun onResume() {
        super.onResume()

        if (isRefresh) {
            onRefresh(true)
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

        when (requestCode) {
            Constants.CODE_REQUEST_ACTIVITY_LOGIN -> {
                if (resultCode != Activity.RESULT_OK) {
                    onBackClick()
                }
            }
        }
    }

    @Synchronized
    override fun onRefresh(showProgress: Boolean) {
        if (isFinish || !isRefresh) {
            return
        }

        isRefresh = false
    }

    override fun onBackClick() {
        setResult(Activity.RESULT_OK)
        activity.onBackPressed()
    }

    private fun showLoginDialog() {
        val positiveListener = View.OnClickListener {
            if (isLock) {
                return@OnClickListener
            }

            startLogin()
        }

        val negativeListener = View.OnClickListener {
            onBackClick()
        }

        val title = getString(R.string.dialog_notice2)
        val message = getString(R.string.dialog_message_register_coupon_login)
        val positive = getString(R.string.dialog_btn_text_yes)
        val negative = getString(R.string.dialog_btn_text_no)

        viewInterface.showSimpleDialog(title, message, positive, negative, positiveListener, negativeListener, DialogInterface.OnCancelListener {
            onBackClick()
        }, null, true)
    }

    private fun startLogin() {
        val intent: Intent = LoginActivity.newInstance(activity)
        startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_LOGIN)
    }

    override fun onRegisterCouponClick(couponCode: String) {
        if (isLock) {
            return
        }

        screenLock(true)

        addCompositeDisposable(couponRemoteImpl.setRegisterCoupon(couponCode)
                .observeOn(AndroidSchedulers.mainThread()).subscribe({
                    unLockAll()

                    val isSuccess = it.msgCode == 100

                    viewInterface.showSimpleDialog(getString(R.string.dialog_notice2), it.msg, getString(R.string.dialog_btn_text_confirm)
                            , null, DialogInterface.OnDismissListener {
                        if (isSuccess) {
                            onBackClick()
                        }
                    })

                    val params = HashMap<String, String>()
                    params[AnalyticsManager.KeyType.COUPON_CODE] = couponCode
                    params[AnalyticsManager.KeyType.STATUS_CODE] = it.msgCode.toString()

                    analytics.onRegistrationResult(activity, isSuccess, couponCode, params)

                }, {
                    onHandleError(it)
                }))
    }
}