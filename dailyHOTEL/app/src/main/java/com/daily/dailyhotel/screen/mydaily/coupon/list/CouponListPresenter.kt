package com.daily.dailyhotel.screen.mydaily.coupon.list

import android.content.Intent
import android.os.Bundle
import com.daily.dailyhotel.base.BaseExceptionPresenter
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.model.Coupon

class CouponListPresenter(activity: CouponListActivity)//
    : BaseExceptionPresenter<CouponListActivity, CouponListInterface.ViewInterface>(activity), CouponListInterface.OnEventListener {

    private val analytics: CouponListInterface.AnalyticsInterface by lazy {
        CouponListAnalyticsImpl()
    }

    override fun createInstanceViewInterface(): CouponListInterface.ViewInterface {
        return CouponListView(activity, this)
    }

    override fun constructorInitialize(activity: CouponListActivity) {
        setContentView(R.layout.activity_coupon_list_data)

        isRefresh = true
    }

    override fun onPostCreate() {
    }

    override fun onIntent(intent: Intent?): Boolean {
        if (intent == null) {
            return true
        }

        return true
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

    override fun startCouponHistory() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun startNotice() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun startRegisterCoupon() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showListItemNotice(coupon: Coupon) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onListItemDownLoadClick(coupon: Coupon) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemSelectedSpinner(position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}