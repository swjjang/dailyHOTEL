package com.daily.dailyhotel.screen.mydaily.coupon.register

import android.content.Intent
import android.os.Bundle
import com.daily.dailyhotel.base.BaseExceptionPresenter
import com.twoheart.dailyhotel.R

class RegisterCouponPresenter(activity: RegisterCouponActivity)//
    : BaseExceptionPresenter<RegisterCouponActivity, RegisterCouponInterface.ViewInterface>(activity), RegisterCouponInterface.OnEventListener {

    private val analytics: RegisterCouponInterface.AnalyticsInterface by lazy {
        RegisterCouponAnalyticsImpl()
    }

    override fun createInstanceViewInterface(): RegisterCouponInterface.ViewInterface {
        return RegisterCouponView(activity, this)
    }

    override fun constructorInitialize(activity: RegisterCouponActivity) {
        setContentView(R.layout.activity_copy_data)

        isRefresh = true
    }

    override fun onPostCreate() {
    }

    override fun onIntent(intent: Intent?): Boolean {
        return intent?.let {
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