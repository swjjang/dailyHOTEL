package com.daily.dailyhotel.screen.mydaily.coupon.list

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.daily.dailyhotel.base.BaseExceptionPresenter
import com.daily.dailyhotel.entity.Coupon
import com.daily.dailyhotel.repository.remote.CouponRemoteImpl
import com.daily.dailyhotel.screen.common.web.DailyWebActivity
import com.daily.dailyhotel.screen.mydaily.coupon.CouponTermActivity
import com.daily.dailyhotel.screen.mydaily.coupon.history.CouponHistoryActivity
import com.daily.dailyhotel.screen.mydaily.coupon.register.RegisterCouponActivity
import com.daily.dailyhotel.storage.preference.DailyPreference
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference
import com.twoheart.dailyhotel.DailyHotel
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.screen.mydaily.member.LoginActivity
import com.twoheart.dailyhotel.util.Constants
import com.twoheart.dailyhotel.util.DailyDeepLink
import com.twoheart.dailyhotel.util.DailyExternalDeepLink
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager
import io.reactivex.android.schedulers.AndroidSchedulers

class CouponListPresenter(activity: CouponListActivity)//
    : BaseExceptionPresenter<CouponListActivity, CouponListInterface.ViewInterface>(activity), CouponListInterface.OnEventListener {

    private var sortType: CouponListActivity.SortType = CouponListActivity.SortType.ALL
    private var dailyDeepLink: DailyDeepLink? = null
    private val couponList = mutableListOf<Coupon>()

    private val couponRemoteImpl: CouponRemoteImpl by lazy {
        CouponRemoteImpl()
    }

    private val analytics: CouponListInterface.AnalyticsInterface by lazy {
        CouponListAnalyticsImpl()
    }

    override fun createInstanceViewInterface(): CouponListInterface.ViewInterface {
        return CouponListView(activity, this)
    }

    override fun constructorInitialize(activity: CouponListActivity) {
        setContentView(R.layout.activity_coupon_list_data)

        DailyPreference.getInstance(activity).setNewCoupon(false)
        DailyPreference.getInstance(activity).viewedCouponTime = DailyPreference.getInstance(activity).latestCouponTime

        isRefresh = true
    }

    override fun onPostCreate() {
        viewInterface.setSelectionSpinner(sortType)
    }

    override fun onIntent(intent: Intent?): Boolean {
        intent?.let {
            sortType = try {
                CouponListActivity.SortType.valueOf(it.getStringExtra(CouponListActivity.INTENT_EXTRA_DATA_SORT_TYPE))
            } catch (e: Exception) {
                CouponListActivity.SortType.ALL
            }

            initDeepLink(it)
            return true
        } ?: return true
    }

    override fun onNewIntent(intent: Intent?) {
        initDeepLink(intent)
    }

    override fun onStart() {
        super.onStart()

        analytics.onScreen(activity)

        if (dailyDeepLink != null) {
            val externalDeepLink: DailyExternalDeepLink? = dailyDeepLink as? DailyExternalDeepLink
            externalDeepLink?.isRegisterCouponView.let {
                startRegisterCoupon()
            }

            dailyDeepLink!!.clear()
            dailyDeepLink = null
        } else {
            if (!DailyHotel.isLogin()) {
                showLoginDialog()
            } else {
                if (isRefresh) {
                    onRefresh(true)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        DailyHotel.isLogin().let {
            if (isRefresh) {
                onRefresh(true)
            }
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
                    finish()
                }
            }

            Constants.CODE_REQUEST_ACTIVITY_REGISTER_COUPON -> {
                if (!DailyHotel.isLogin()) {
                    finish()
                }
            }

            Constants.CODE_REQUEST_ACTIVITY_COUPON_TERMS, Constants.CODE_REQUEST_ACTIVITY_COUPON_HISTORY -> {
                if (resultCode == Constants.CODE_RESULT_ACTIVITY_GO_HOME) {
                    setResult(resultCode)
                    finish()
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
        screenLock(showProgress)

        addCompositeDisposable(couponRemoteImpl.couponList.observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    setCouponList(it)
                    notifyCouponDataSetChanged()
                    unLockAll()
                }, {
                    onHandleError(it)
                }))
    }

    override fun onBackClick() {
        activity.onBackPressed()
    }

    private fun initDeepLink(intent: Intent?) {
        intent?.let {
            dailyDeepLink = try {
                DailyDeepLink.getNewInstance(Uri.parse(it.getStringExtra(Constants.NAME_INTENT_EXTRA_DATA_DEEPLINK)))
            } catch (exception: Exception) {
                null
            }
        }
    }

    private fun showLoginDialog() {
        val positiveListener = View.OnClickListener {
            lock()
            startLogin()
        }

        val negativeListener = View.OnClickListener {
            finish()
        }

        val cancelListener = DialogInterface.OnCancelListener {
            finish()
        }

        activity.resources.run {
            val title = getString(R.string.dialog_notice2)
            val message = getString(R.string.dialog_message_coupon_list_login)
            val positive = getString(R.string.dialog_btn_text_yes)
            val negative = getString(R.string.dialog_btn_text_no)

            this@CouponListPresenter.viewInterface.showSimpleDialog(title, message, positive, negative
                    , positiveListener, negativeListener, cancelListener, null, true)
        }
    }

    private fun startLogin() {
        val intent = LoginActivity.newInstance(activity)
        startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_LOGIN)
    }

    private fun setCouponList(list: MutableList<Coupon>) {
        couponList.clear()
        couponList += list
    }

    private fun notifyCouponDataSetChanged() {
        viewInterface.setData(makeSortCouponList(couponList, sortType), sortType, false)
    }

    private fun makeSortCouponList(originList: MutableList<Coupon>, sortType: CouponListActivity.SortType): MutableList<Coupon> {
        return if (originList.size > 0) {
            val sortList = mutableListOf<Coupon>()

            when (sortType) {
                CouponListActivity.SortType.ALL -> {
                    sortList += originList
                }

                CouponListActivity.SortType.STAY -> {
                    for (coupon in originList) {
                        if (coupon.availableInStay || coupon.availableInOutboundHotel) {
                            sortList += coupon
                        }
                    }
                }

                CouponListActivity.SortType.GOURMET -> {
                    for (coupon in originList) {
                        if (coupon.availableInGourmet) {
                            sortList += coupon
                        }
                    }
                }
            }

            sortList
        } else {
            mutableListOf()
        }
    }

    override fun startCouponHistory() {
        // 쿠폰 사용내역 이동
        if (lock()) {
            return
        }

        val intent = CouponHistoryActivity.newInstance(activity)
        startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_COUPON_HISTORY)
    }

    override fun startNotice() {
        // 쿠폰 사용시 유의사항 안내
        val intent = CouponTermActivity.newInstance(activity)
        startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_COUPON_TERMS)
    }

    override fun startRegisterCoupon() {
        val intent = RegisterCouponActivity.newInstance(activity, AnalyticsManager.Screen.MENU_COUPON_BOX)
        startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_REGISTER_COUPON)
    }

    override fun showListItemNotice(coupon: Coupon) {
        if (Coupon.Type.REWARD == coupon.type) {
            startActivityForResult(DailyWebActivity.newInstance(activity, getString(R.string.coupon_notice_text)
                    , DailyRemoteConfigPreference.getInstance(activity).keyRemoteConfigStaticUrlDailyRewardCouponTerms)
                    , Constants.CODE_REQUEST_ACTIVITY_COUPON_TERMS)
        } else {
            // 리스트 아이템 쿠폰 유의사항 팝업
            // 쿠폰 사용시 유의사항 안내
            val intent = CouponTermActivity.newInstance(activity, coupon.couponCode)
            startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_COUPON_TERMS)
        }
    }

    override fun onListItemDownLoadClick(coupon: Coupon) {
        if (lock()) {
            return
        }

        addCompositeDisposable(couponRemoteImpl.getDownloadCoupon(coupon.couponCode)
                .observeOn(AndroidSchedulers.mainThread()).subscribe({
                    analytics.onDownloadCoupon(activity, coupon)

                    isRefresh = true
                    onRefresh(true)
                }, {
                    onHandleError(it)
                }))
    }

    override fun onItemSelectedSpinner(position: Int) {
        sortType = when (position) {
            2 -> {
                CouponListActivity.SortType.GOURMET
            }

            1 -> {
                CouponListActivity.SortType.STAY
            }

            else -> {
                CouponListActivity.SortType.ALL
            }
        }

        viewInterface.setData(makeSortCouponList(couponList, sortType), sortType, true)
    }
}