package com.daily.dailyhotel.screen.home.stay.inbound.detailk;

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.crashlytics.android.Crashlytics
import com.daily.base.BaseActivity
import com.daily.dailyhotel.base.BaseExceptionPresenter
import com.daily.dailyhotel.entity.CommonDateTime
import com.daily.dailyhotel.entity.StayBookDateTime
import com.daily.dailyhotel.entity.StayDetail
import com.daily.dailyhotel.parcel.analytics.StayDetailAnalyticsParam
import com.daily.dailyhotel.repository.local.RecentlyLocalImpl
import com.daily.dailyhotel.repository.remote.CalendarImpl
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl
import com.daily.dailyhotel.repository.remote.ProfileRemoteImpl
import com.daily.dailyhotel.repository.remote.StayRemoteImpl
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.util.AppResearch
import com.twoheart.dailyhotel.util.Constants
import com.twoheart.dailyhotel.util.DailyDeepLink
import com.twoheart.dailyhotel.util.DailyExternalDeepLink
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

        writeRecentlyViewedPlace()

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

    private fun writeRecentlyViewedPlace() {
        addCompositeDisposable(recentlyLocalImpl.addRecentlyItem(activity, Constants.ServiceType.HOTEL,
                stayIndex, stayName, null, defaultImageUrl, null, true).subscribe())
    }

    private fun screenLockDelay(delay: Int) {
        addCompositeDisposable(Completable.timer(2, TimeUnit.SECONDS).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe { screenLock(true) })
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

    private fun onRefresh(observable: Observable<Boolean>) {

        addCompositeDisposable(Observable.zip(observable,
                stayRemoteImpl.getDetail(stayIndex, bookDateTime),
                calendarImpl.getStayUnavailableCheckInDates(stayIndex, DAYS_OF_MAX_COUNT, false),
                commonRemoteImpl.commonDateTime, object : Function4<Boolean, StayDetail, List<String>, CommonDateTime, StayDetail> {
            override fun apply(sharedElementTransition: Boolean, stayDetail: StayDetail, soldOutDayList: List<String>, commonDateTime: CommonDateTime): StayDetail {

                this@StayDetailPresenter.commonDateTime.setDateTime(commonDateTime)
                this@StayDetailPresenter.soldOutDays = soldOutDayList.map { it.replaceAfter('-', "").toInt() }.toTypedArray()
                this@StayDetailPresenter.stayDetail = stayDetail

                return stayDetail
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe({




        }, {

        }))
    }

    override fun onBackClick() {
        activity.onBackPressed()
    }
}