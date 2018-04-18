package com.daily.dailyhotel.screen.home.stay.inbound.detailk;

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.crashlytics.android.Crashlytics
import com.daily.base.BaseActivity
import com.daily.base.util.DailyTextUtils
import com.daily.base.util.ExLog
import com.daily.base.widget.DailyToast
import com.daily.dailyhotel.base.BaseExceptionPresenter
import com.daily.dailyhotel.entity.CommonDateTime
import com.daily.dailyhotel.entity.StayBookDateTime
import com.daily.dailyhotel.entity.StayDetail
import com.daily.dailyhotel.entity.StayRoom
import com.daily.dailyhotel.parcel.analytics.StayDetailAnalyticsParam
import com.daily.dailyhotel.repository.local.RecentlyLocalImpl
import com.daily.dailyhotel.repository.remote.CalendarImpl
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl
import com.daily.dailyhotel.repository.remote.ProfileRemoteImpl
import com.daily.dailyhotel.repository.remote.StayRemoteImpl
import com.daily.dailyhotel.screen.common.calendar.stay.StayCalendarActivity
import com.daily.dailyhotel.storage.preference.DailyPreference
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference
import com.daily.dailyhotel.util.isTextEmpty
import com.twoheart.dailyhotel.DailyHotel
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.screen.mydaily.member.LoginActivity
import com.twoheart.dailyhotel.util.*
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function4
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class StayDetailPresenter(activity: StayDetailActivity)//
    : BaseExceptionPresenter<StayDetailActivity, StayDetailInterface.ViewInterface>(activity), StayDetailInterface.OnEventListener {

    val DAYS_OF_MAX_COUNT = 60

    enum class Status {
        NONE, BOOKING, SOLD_OUT, FINISH
    }

    enum class PriceType {
        AVERAGE, TOTAL
    }

    private val analytics: StayDetailInterface.AnalyticsInterface by lazy {
        StayDetailAnalyticsImpl()
    }

    private val stayRemoteImpl: StayRemoteImpl by lazy {
        StayRemoteImpl()
    }

    private val commonRemoteImpl: CommonRemoteImpl by lazy {
        CommonRemoteImpl()
    }

    private val profileRemoteImpl: ProfileRemoteImpl by lazy {
        ProfileRemoteImpl()
    }

    private val calendarImpl: CalendarImpl by lazy {
        CalendarImpl()
    }

    private val recentlyLocalImpl: RecentlyLocalImpl by lazy {
        RecentlyLocalImpl()
    }

    private val appResearch: AppResearch by lazy {
        AppResearch()
    }

    private var stayIndex: Int = 0
    private var viewPrice: Int = 0
    private var stayName: String? = null
    private var defaultImageUrl: String? = null
    private var stayDetail: StayDetail? = null
    private var status = Status.NONE
    private var isUsedMultiTransition = false
    private var hasDeepLink = false
    private var gradientType = StayDetailActivity.TransGradientType.NONE
    private var soldOutDays: Array<Int>? = null
    private var showCalendar = false
    private var showTrueVR = false
    private var deepLink: DailyDeepLink? = null

    private val bookDateTime = StayBookDateTime()
    private val commonDateTime = CommonDateTime()

    override fun createInstanceViewInterface(): StayDetailInterface.ViewInterface {
        return StayDetailView(activity, this)
    }

    override fun constructorInitialize(activity: StayDetailActivity) {
        setContentView(R.layout.activity_stay_detail_data)

        isRefresh = true
    }

    override fun onIntent(intent: Intent?): Boolean {
        return intent?.let {

            if (it.hasExtra(BaseActivity.INTENT_EXTRA_DATA_DEEPLINK)) {
                processDeepLink(it.getStringExtra(BaseActivity.INTENT_EXTRA_DATA_DEEPLINK))
            } else {
                processIntent(it)
            }
        } ?: true
    }

    private fun processDeepLink(deepLink: String): Boolean {
        analytics.setAnalyticsParam(StayDetailAnalyticsParam())

        try {
            this.deepLink = DailyDeepLink.getNewInstance(Uri.parse(deepLink))
        } catch (e: Exception) {
            this.deepLink = null
            return false
        }

        isUsedMultiTransition = false
        hasDeepLink = true

        addCompositeDisposable(commonRemoteImpl.commonDateTime.subscribe({
            this@StayDetailPresenter.commonDateTime.setDateTime(it)

            if (this@StayDetailPresenter.deepLink is DailyExternalDeepLink) {
                val externalDeepLink = this@StayDetailPresenter.deepLink as DailyExternalDeepLink

                try {
                    stayIndex = externalDeepLink.index.toInt()
                    bookDateTime.setBookDateTime(externalDeepLink.getStayBookDateTime(it))
                    showCalendar = externalDeepLink.isShowCalendar
                    showTrueVR = externalDeepLink.isShowVR

                    this@StayDetailPresenter.deepLink?.clear()
                    this@StayDetailPresenter.deepLink = null

                    isRefresh = true
                    onRefresh(true)
                } catch (e: Exception) {
                    Crashlytics.log(externalDeepLink.deepLink)
                    Crashlytics.logException(e)
                    finish()
                }
            }
        }, {
            Crashlytics.log(this@StayDetailPresenter.deepLink?.deepLink)
            Crashlytics.logException(it)

            onHandleErrorAndFinish(it)
        }))

        return true
    }

    private fun processIntent(intent: Intent): Boolean {
        try {
            isUsedMultiTransition = intent.getBooleanExtra(StayDetailActivity.INTENT_EXTRA_DATA_MULTITRANSITION, false)

            try {
                gradientType = StayDetailActivity.TransGradientType.valueOf(intent.getStringExtra(StayDetailActivity.INTENT_EXTRA_DATA_CALL_GRADIENT_TYPE))
            } catch (e: Exception) {
                gradientType = StayDetailActivity.TransGradientType.NONE
            }

            stayIndex = intent.getIntExtra(StayDetailActivity.INTENT_EXTRA_DATA_STAY_INDEX, 0)
            stayName = intent.getStringExtra(StayDetailActivity.INTENT_EXTRA_DATA_STAY_NAME)
            defaultImageUrl = intent.getStringExtra(StayDetailActivity.INTENT_EXTRA_DATA_IMAGE_URL)
            viewPrice = intent.getIntExtra(StayDetailActivity.INTENT_EXTRA_DATA_LIST_PRICE, StayDetailActivity.NONE_PRICE)

            val checkInDateTime = intent.getStringExtra(StayDetailActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME)
            val checkOutDateTime = intent.getStringExtra(StayDetailActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME)
            bookDateTime.setBookDateTime(checkInDateTime, checkOutDateTime)

            analytics.setAnalyticsParam(intent.getParcelableExtra(BaseActivity.INTENT_EXTRA_DATA_ANALYTICS))
        } catch (e: Exception) {
            return false
        }

        return true
    }

    override fun onNewIntent(intent: Intent?) {
    }

    override fun onPostCreate() {
        viewInterface.setSharedElementTransitionEnabled(isTransitionEnabled(), gradientType)
        viewInterface.setInitializedLayout(stayName, defaultImageUrl)

        if (isUsedMultiTransition) {
            isRefresh = true
            screenLock(false)

            screenLockDelay(2)

            onRefresh(viewInterface.getSharedElementTransition(gradientType))
        } else {

        }
    }

    private fun isTransitionEnabled(): Boolean {
        return !hasDeepLink && isUsedMultiTransition
    }

    private fun screenLockDelay(delay: Int) {
        addCompositeDisposable(Completable.timer(2, TimeUnit.SECONDS).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe { screenLock(true) })
    }

    override fun onStart() {
        super.onStart()

        if (isRefresh) onRefresh(true)
    }

    override fun onResume() {
        super.onResume()

        if (isRefresh) onRefresh(true)

        if (!DailyHotel.isLogin() && DailyRemoteConfigPreference.getInstance(activity).isKeyRemoteConfigRewardStickerCampaignEnabled) {
            viewInterface.startCampaignStickerAnimation()
        }

        appResearch.onResume(activity, getString(R.string.label_stay), stayIndex)
    }

    override fun onPause() {
        super.onPause()

        viewInterface.stopCampaignStickerAnimation()

        appResearch.onPause(activity, getString(R.string.label_stay), stayIndex)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onFinish() {
        super.onFinish()

        if (!isUsedMultiTransition) activity.overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }

    override fun onBackPressed(): Boolean {
        when (status) {
            Status.BOOKING -> return true

            Status.FINISH -> return super.onBackPressed()

            else -> {
                status = Status.FINISH

                if (resultCode == BaseActivity.RESULT_CODE_REFRESH) {
                    finish()
                    return true
                }

                if (isUsedMultiTransition) {
                    if (lock()) return true

                    viewInterface.setTransitionVisible(true)
                    viewInterface.scrollTop()

                    Completable.timer(300, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).subscribe { onBackPressed() }

                    return true
                }
            }
        }

        analytics.onScreen(activity, bookDateTime, stayDetail, viewPrice)

        return super.onBackPressed()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        Util.restartApp(activity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        unLockAll()

        when (requestCode) {
            StayDetailActivity.REQUEST_CODE_CALENDAR -> onCalendarActivityResult(resultCode, intent)

            StayDetailActivity.REQUEST_CODE_PAYMENT -> isRefresh = true

            StayDetailActivity.REQUEST_CODE_LOGIN -> onLoginActivityResult(resultCode, intent)

            StayDetailActivity.REQUEST_CODE_LOGIN_IN_BY_WISH -> onLoginByWishActivityResult(resultCode, intent)

            StayDetailActivity.REQUEST_CODE_LOGIN_IN_BY_COUPON -> onLoginByCouponActivityResult(resultCode, intent)
        }
    }

    private fun onCalendarActivityResult(resultCode: Int, intent: Intent?) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                intent?.let {
                    try {
                        val checkInDateTime = it.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECK_IN_DATETIME)
                        val checkOutDateTime = it.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATETIME)

                        if (isTextEmpty(checkInDateTime, checkOutDateTime)) return

                        bookDateTime.setBookDateTime(checkInDateTime, checkOutDateTime)
                        isRefresh = true
                    } catch (e: Exception) {
                        ExLog.e(e.toString())
                    }
                }
            }
        }
    }

    private fun onLoginActivityResult(resultCode: Int, intent: Intent?) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                setResult(BaseActivity.RESULT_CODE_REFRESH)
                isRefresh = true
            }
        }
    }

    private fun onLoginByWishActivityResult(resultCode: Int, intent: Intent?) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                onWishClick()

                setResult(BaseActivity.RESULT_CODE_REFRESH)
                isRefresh = true
            }
        }
    }

    private fun onLoginByCouponActivityResult(resultCode: Int, intent: Intent?) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                onDownloadCouponClick()

                setResult(BaseActivity.RESULT_CODE_REFRESH)
                isRefresh = true
            }
        }
    }

    @Synchronized
    override fun onRefresh(showProgress: Boolean) {
        if (isFinish || !isRefresh) return

        if (bookDateTime == null || !bookDateTime.validate()) {
            Util.restartApp(activity)
            return
        }

        isRefresh = false
        screenLock(showProgress)

        onRefresh(Observable.just(true))
    }

    private fun onRefresh(observable: Observable<Boolean>) {

        addCompositeDisposable(Observable.zip(observable,
                stayRemoteImpl.getDetail(stayIndex, bookDateTime),
                calendarImpl.getStayUnavailableCheckInDates(stayIndex, DAYS_OF_MAX_COUNT, false),
                commonRemoteImpl.commonDateTime, object : Function4<Boolean, StayDetail, List<String>, CommonDateTime, StayDetail> {

            override fun apply(sharedElementTransition: Boolean, stayDetail: StayDetail, soldOutDayList: List<String>, commonDateTime: CommonDateTime): StayDetail {
                this@StayDetailPresenter.commonDateTime.setDateTime(commonDateTime)
                this@StayDetailPresenter.soldOutDays = soldOutDayList.map { it.replaceAfter('-', "").toInt() }.toTypedArray()
                this@StayDetailPresenter.stayDetail = stayDetail

                writeRecentlyViewedPlace(stayDetail)

                return stayDetail
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe({
            //

            if (DailyPreference.getInstance(activity).isWishTooltip) showWishTooltip()

//            if(reviewdafsdf) analytics.onEventShowTrueReview(activity, it.index)

            if (it.couponPrice > 0) analytics.onEventShowCoupon(activity, it.index)

            if (!DailyTextUtils.isTextEmpty(it.awards?.title)) analytics.onEventShowCoupon(activity, it.index)

            unLockAll()

        }, {
            onHandleErrorAndFinish(it)
        }))
    }

    private fun writeRecentlyViewedPlace(stayDetail: StayDetail) {
        addCompositeDisposable(recentlyLocalImpl.addRecentlyItem(activity, Constants.ServiceType.HOTEL,
                stayDetail.index, stayDetail.name, null,
                if (defaultImageUrl.isTextEmpty()) stayDetail.defaultImageUrl else defaultImageUrl,
                null, true).subscribe())
    }

    private fun showWishTooltip() {
        viewInterface.showWishTooltip()

        addCompositeDisposable(Completable.timer(3, TimeUnit.SECONDS).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe {
                    DailyPreference.getInstance(activity).isWishTooltip = false
                    viewInterface.hideWishTooltip()
                })
    }

    override fun onBackClick() {
        activity.onBackPressed()
    }

    override fun onShareClick() {
        if (lock()) return

        viewInterface.showShareDialog(DialogInterface.OnDismissListener { unLockAll() })

        analytics.onEventShare(activity)
    }

    override fun onWishClick() {
        if (lock()) return

        if (!DailyHotel.isLogin()) {
            DailyToast.showToast(activity, R.string.toast_msg_please_login, DailyToast.LENGTH_LONG)

            startActivityForResult(LoginActivity.newInstance(activity, AnalyticsManager.Screen.DAILYHOTEL_DETAIL),
                    StayDetailActivity.REQUEST_CODE_LOGIN_IN_BY_WISH)

            return
        }

        stayDetail?.let {
            val wish = !it.myWish
            val wishCount = it.wishCount + if(wish) 1 else - 1

            notifyWishChanged(wishCount, wish)


        }
    }

    override fun onShareKakaoClick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCopyLinkClick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMoreShareClick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onImageClick(position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCalendarClick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMapClick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onClipAddressClick(address: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNavigatorClick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConciergeClick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMoreRoomListClick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPriceTypeClick(priceType: PriceType) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConciergeFaqClick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConciergeHappyTalkClick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConciergeCallClick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRoomClick(stayRoom: StayRoom) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onTrueReviewClick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onTrueVRClick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDownloadCouponClick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onHideWishTooltipClick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLoginClick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRewardClick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRewardGuideClick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onTrueAwardsClick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}