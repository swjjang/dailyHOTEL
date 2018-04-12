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
import com.daily.dailyhotel.storage.preference.DailyPreference
import com.twoheart.dailyhotel.DailyHotel
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.screen.mydaily.coupon.RegisterCouponActivity
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

    private val couponRemotImpl: CouponRemoteImpl by lazy {
        CouponRemoteImpl()
    }

    private val analytics: CouponListInterface.AnalyticsInterface by lazy {
        CouponListAnalyticsImpl()
    }

    override fun createInstanceViewInterface(): CouponListInterface.ViewInterface {
        return CouponListView(activity, this)
    }

    override fun constructorInitialize(activity: CouponListActivity) {
        DailyPreference.getInstance(activity).setNewCoupon(false)
        DailyPreference.getInstance(activity).viewedCouponTime = DailyPreference.getInstance(activity).latestCouponTime

        setContentView(R.layout.activity_coupon_list_data)

        isRefresh = true
    }

    override fun onPostCreate() {
        viewInterface.setSelectionSpinner(sortType)
    }

    override fun onIntent(intent: Intent?): Boolean {
        intent?.let {
            sortType = try {
                CouponListActivity.SortType.valueOf(it.getStringExtra(Constants.NAME_INTENT_EXTRA_DATA_PLACETYPE))
            } catch (exception: Exception) {
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

        addCompositeDisposable(couponRemotImpl.couponList.observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    setCouponList(it)

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

        val cancleListener = DialogInterface.OnCancelListener {
            finish()
        }

        activity.resources.run {
            val title = getString(R.string.dialog_notice2)
            val message = getString(R.string.dialog_message_coupon_list_login)
            val positive = getString(R.string.dialog_btn_text_yes)
            val negative = getString(R.string.dialog_btn_text_no)

            this@CouponListPresenter.viewInterface.showSimpleDialog(title, message, positive, negative
                    , positiveListener, negativeListener, cancleListener, null, true)
        }
    }

    private fun startLogin() {
        val intent = LoginActivity.newInstance(activity)
        startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_LOGIN)
    }

    private fun setCouponList(list : MutableList<Coupon>) {
        couponList += list
    }

    private fun notifyDataSetChange() {


        viewInterface.setData()
    }

    private fun makeSortCouponList(orginList: MutableList<Coupon>, sortType:CouponListActivity.SortType) : MutableList<Coupon> {
        return (orginList.size > 0).let {
            val sortList = mutableListOf<Coupon>()

            when(sortType) {
                CouponListActivity.SortType.ALL -> {
                    sortList += orginList
                }

                CouponListActivity.SortType.STAY -> {
                    for (coupon in orginList) {
                        if (coupon.availableInStay || coupon.availableInOutboundHotel) {
                            sortList.add(coupon)
                        }
                    }
                }

                CouponListActivity.SortType.GOURMET -> {
                    for (coupon in orginList) {
                        if (coupon.availableInGourmet) {
                            sortList.add(coupon)
                        }
                    }
                }
            }

            sortList
        } ?: mutableListOf<Coupon>()
    }

    override fun startCouponHistory() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun startNotice() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun startRegisterCoupon() {
        val intent = RegisterCouponActivity.newInstance(activity, AnalyticsManager.Screen.MENU_COUPON_BOX)
        startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_REGISTER_COUPON)
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