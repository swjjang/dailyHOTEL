package com.daily.dailyhotel.screen.home.stay.inbound.detailk;

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.crashlytics.android.Crashlytics
import com.daily.base.BaseActivity
import com.daily.base.util.DailyTextUtils
import com.daily.base.util.ExLog
import com.daily.base.widget.DailyToast
import com.daily.dailyhotel.base.BaseExceptionPresenter
import com.daily.dailyhotel.entity.*
import com.daily.dailyhotel.parcel.analytics.ImageListAnalyticsParam
import com.daily.dailyhotel.parcel.analytics.NavigatorAnalyticsParam
import com.daily.dailyhotel.parcel.analytics.StayDetailAnalyticsParam
import com.daily.dailyhotel.repository.local.RecentlyLocalImpl
import com.daily.dailyhotel.repository.remote.CalendarImpl
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl
import com.daily.dailyhotel.repository.remote.ProfileRemoteImpl
import com.daily.dailyhotel.repository.remote.StayRemoteImpl
import com.daily.dailyhotel.screen.common.calendar.stay.StayCalendarActivity
import com.daily.dailyhotel.screen.common.dialog.call.CallDialogActivity
import com.daily.dailyhotel.screen.common.dialog.navigator.NavigatorDialogActivity
import com.daily.dailyhotel.screen.common.event.EventWebActivity
import com.daily.dailyhotel.screen.common.images.ImageListActivity
import com.daily.dailyhotel.screen.common.web.DailyWebActivity
import com.daily.dailyhotel.screen.mydaily.coupon.dialog.SelectStayCouponDialogActivity
import com.daily.dailyhotel.screen.mydaily.reward.RewardActivity
import com.daily.dailyhotel.storage.preference.DailyPreference
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference
import com.daily.dailyhotel.storage.preference.DailyUserPreference
import com.daily.dailyhotel.util.*
import com.twoheart.dailyhotel.DailyHotel
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.screen.common.HappyTalkCategoryDialog
import com.twoheart.dailyhotel.screen.common.ZoomMapActivity
import com.twoheart.dailyhotel.screen.information.FAQActivity
import com.twoheart.dailyhotel.screen.mydaily.member.LoginActivity
import com.twoheart.dailyhotel.util.*
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager
import io.reactivex.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.functions.Function4
import io.reactivex.schedulers.Schedulers
import java.util.*
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
    private var soldOutDays: IntArray? = null
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
                this@StayDetailPresenter.soldOutDays = soldOutDayList.map { it.replaceAfter('-', "").toInt() }.toIntArray()
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

        val regionName = stayDetail.province?.name
        val singleObservable: Single<String> = if (regionName.isTextEmpty()) {
            Single.create(SingleOnSubscribe<String> { emitter ->
                try {
                    Geocoder(activity, Locale.KOREA).getFromLocation(stayDetail.latitude, stayDetail.longitude, 10)?.forEach {
                        it.locality?.takeNotEmpty {
                            emitter.onSuccess(it)
                        } ?: emitter.onSuccess("")
                    }
                } catch (e: Exception) {
                    ExLog.e(e.toString())
                }
            }).subscribeOn(Schedulers.io())
        } else {
            Single.just(regionName)
        }

        addCompositeDisposable(singleObservable.flatMapObservable(Function<String, ObservableSource<Boolean>> {
            recentlyLocalImpl.addRecentlyItem(activity, Constants.ServiceType.HOTEL,
                    stayDetail.index, stayDetail.name, null,
                    if (defaultImageUrl.isTextEmpty()) stayDetail.defaultImageUrl else defaultImageUrl,
                    it, false)
        }).subscribe())
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
        } else {
            stayDetail?.let {
                val wish = !it.myWish
                val wishCount = it.wishCount + if (wish) 1 else -1

                notifyWishChanged(wishCount, wish)

                processWish(it.index, wish)
            }
        }
    }

    private fun processWish(stayIndex: Int, wish: Boolean) {
        addCompositeDisposable(getWishObservable(stayIndex, wish).observeOn(AndroidSchedulers.mainThread()).subscribe({ wishResult ->

            setResult(BaseActivity.RESULT_CODE_REFRESH, Intent().putExtra(StayDetailActivity.INTENT_EXTRA_DATA_WISH, wish))

            stayDetail?.let {
                if (wishResult.success) {
                    it.myWish = wish
                    it.wishCount += if (wish) 1 else -1

                    notifyWishChanged()

                    addCompositeDisposable(viewInterface.showWishView(it.myWish).subscribeOn(AndroidSchedulers.mainThread()).subscribe { unLockAll() })

                    analytics.onEventWishClick(activity, bookDateTime, it, viewPrice, wish)
                } else {
                    notifyWishChanged(wishCount, wish)

                    viewInterface.showSimpleDialog(getString(R.string.dialog_notice2), wishResult.message, getString(R.string.dialog_btn_text_confirm), null);

                    unLockAll()
                }
            }
        }, {
            onHandleError(it)

            stayDetail?.let { notifyWishChanged(it.wishCount, it.myWish) }
        }))
    }

    private fun getWishObservable(stayIndex: Int, wish: Boolean): Observable<WishResult> {
        return if (wish) stayRemoteImpl.removeWish(stayIndex) else stayRemoteImpl.addWish(stayIndex)
    }

    override fun onShareKakaoClick() {
        if (lock()) return

        stayDetail?.let { stayDetail ->
            try {
                activity.packageManager.getPackageInfo("com.kakao.talk", PackageManager.GET_META_DATA)

                val name: String? = DailyUserPreference.getInstance(activity).name
                val urlFormat = "https://mobile.dailyhotel.co.kr/stay/%d?dateCheckIn=%s&stays=%d&utm_source=share&utm_medium=stay_detail_kakaotalk"
                val longUrl = String.format(Locale.KOREA, urlFormat, stayDetail.index, bookDateTime.getCheckInDateTime("yyyy-MM-dd"), bookDateTime.nights)

                addCompositeDisposable(commonRemoteImpl.getShortUrl(longUrl).observeOn(AndroidSchedulers.mainThread()).subscribe({ shortUrl ->
                    unLockAll()

                    KakaoLinkManager.newInstance(activity).shareStay(name, stayDetail.name, stayDetail.address,
                            stayDetail.index, stayDetail.defaultImageUrl, shortUrl, bookDateTime)
                }, {
                    unLockAll()

                    KakaoLinkManager.newInstance(activity).shareStay(name, stayDetail.name, stayDetail.address, stayDetail.index,
                            stayDetail.defaultImageUrl, "https://mobile.dailyhotel.co.kr/stay/" + stayDetail.index, bookDateTime)
                }))

                analytics.onEventShareKakaoClick(activity, DailyHotel.isLogin(), DailyUserPreference.getInstance(activity).type,
                        DailyUserPreference.getInstance(activity).isBenefitAlarm, stayDetail.index, stayDetail.name, stayDetail.overseas)
            } catch (e: Exception) {
                unLockAll()

                viewInterface.showSimpleDialog(null, getString(R.string.dialog_msg_not_installed_kakaotalk),
                        getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no),
                        View.OnClickListener { Util.installPackage(activity, "com.kakao.talk") }, null)
            }
        } ?: Util.restartApp(activity)
    }

    override fun onCopyLinkClick() {
        if (lock()) return

        stayDetail?.let { stayDetail ->
            try {
                val longUrl = String.format(Locale.KOREA, "https://mobile.dailyhotel.co.kr/stay/%d?dateCheckIn=%s&stays=%d"//
                        , stayDetail.index, bookDateTime.getCheckInDateTime("yyyy-MM-dd"), bookDateTime.nights)

                addCompositeDisposable(commonRemoteImpl.getShortUrl(longUrl).subscribe({
                    DailyTextUtils.clipText(activity, it)
                    DailyToast.showToast(activity, R.string.toast_msg_copy_link, DailyToast.LENGTH_LONG)
                    unLockAll()
                }, {
                    DailyTextUtils.clipText(activity, "https://mobile.dailyhotel.co.kr/stay/" + stayDetail.index)
                    DailyToast.showToast(activity, R.string.toast_msg_copy_link, DailyToast.LENGTH_LONG)
                    unLockAll()
                }))

                analytics.onEventLinkCopyClick(activity)
            } catch (e: Exception) {
                ExLog.e(e.toString())

                unLockAll()
            }
        } ?: Util.restartApp(activity)
    }

    override fun onMoreShareClick() {
        if (lock()) return

        stayDetail?.let { stayDetail ->
            try {
                val longUrl = String.format(Locale.KOREA, "https://mobile.dailyhotel.co.kr/stay/%d?dateCheckIn=%s&stays=%d&utm_source=share&utm_medium=stay_detail_moretab",
                        stayDetail.index, bookDateTime.getCheckInDateTime("yyyy-MM-dd"), bookDateTime.nights)
                val name = DailyUserPreference.getInstance(activity).name.takeNotEmptyThisAddStringButDefaultString(getString(R.string.label_friend) + "가", "님이")
                val message = getString(R.string.message_detail_stay_share_sms, name, stayDetail.name,
                        bookDateTime.getCheckInDateTime("yyyy.MM.dd(EEE)"),
                        bookDateTime.getCheckOutDateTime("yyyy.MM.dd(EEE)"),
                        bookDateTime.nights, bookDateTime.nights + 1, stayDetail.address)

                addCompositeDisposable(commonRemoteImpl.getShortUrl(longUrl).subscribe({
                    startActivity(Intent.createChooser(Intent(android.content.Intent.ACTION_SEND)
                            .apply { type = "text/plain" }
                            .putExtra(Intent.EXTRA_SUBJECT, "")
                            .putExtra(Intent.EXTRA_TEXT, message + it),
                            getString(R.string.label_doshare)))

                    unLockAll()
                }, {
                    startActivity(Intent.createChooser(Intent(android.content.Intent.ACTION_SEND)
                            .apply { type = "text/plain" }
                            .putExtra(Intent.EXTRA_SUBJECT, "")
                            .putExtra(Intent.EXTRA_TEXT, message + "https://mobile.dailyhotel.co.kr/stay/" + stayDetail.index),
                            getString(R.string.label_doshare)))
                    unLockAll()
                }))

                analytics.onEventMoreShareClick(activity)
            } catch (e: Exception) {
                ExLog.e(e.toString())

                unLockAll()
            }
        } ?: Util.restartApp(activity)
    }

    override fun onImageClick(position: Int) {
        if (stayDetail.filterIf({ it.hasImageInformation() }) && lock()) return

        stayDetail?.let {
            startActivityForResult(ImageListActivity.newInstance(activity, it.name,
                    it.imageInformationList, position,
                    ImageListAnalyticsParam().apply { serviceType = Constants.ServiceType.HOTEL }),
                    StayDetailActivity.REQUEST_CODE_IMAGE_LIST)

            analytics.onEventImageClick(activity, it.name)
        } ?: Util.restartApp(activity)
    }

    override fun onCalendarClick() {
        if (lock()) return

        stayDetail?.let {
            val calendar = DailyCalendar.getInstance(commonDateTime.dailyDateTime, DailyCalendar.ISO_8601_FORMAT)
            val startDateTime = DailyCalendar.format(calendar.time, DailyCalendar.ISO_8601_FORMAT)
            calendar.add(Calendar.DAY_OF_MONTH, DAYS_OF_MAX_COUNT - 1)
            val endDateTime = DailyCalendar.format(calendar.time, DailyCalendar.ISO_8601_FORMAT)
            val callByScreen = if (equalsCallingActivity(EventWebActivity::class.java)) AnalyticsManager.Label.EVENT else AnalyticsManager.ValueType.DETAIL

            startActivityForResult(StayCalendarActivity.newInstance(activity,
                    startDateTime, endDateTime,
                    if (it.singleStay) 1 else DAYS_OF_MAX_COUNT - 1,
                    bookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT),
                    bookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT),
                    it.index, soldOutDays, callByScreen, !isSoldOut(),
                    0, true), StayDetailActivity.REQUEST_CODE_CALENDAR)

            analytics.onEventCalendarClick(activity)
        } ?: Util.restartApp(activity)
    }

    private fun isSoldOut(): Boolean {
        return stayDetail.filterIf({ it.hasRooms() }, true)
    }

    override fun onMapClick() {
        if (lock()) return

        stayDetail?.let {
            if (Util.isInstallGooglePlayService(activity)) {
                startActivityForResult(ZoomMapActivity.newInstance(activity, ZoomMapActivity.SourceType.HOTEL,
                        it.name, it.address, it.latitude, it.longitude, false), StayDetailActivity.REQUEST_CODE_MAP)
            } else {
                viewInterface.showSimpleDialog(getString(R.string.dialog_title_googleplayservice),
                        getString(R.string.dialog_msg_install_update_googleplayservice),
                        getString(R.string.dialog_btn_text_install), getString(R.string.dialog_btn_text_cancel),
                        View.OnClickListener {
                            try {
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.google.android.gms")).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
                                    `package` = "com.android.vending"
                                })
                            } catch (e: ActivityNotFoundException) {
                                try {
                                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.gms")).apply {
                                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
                                        `package` = "com.android.vending"
                                    })
                                } catch (f: ActivityNotFoundException) {
                                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.google.android.gms")).apply {
                                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
                                    })
                                }
                            }
                        }, null, true)

                unLockAll()
            }

            analytics.onEventMapClick(activity, it.name)
        } ?: Util.restartApp(activity)
    }

    override fun onClipAddressClick(address: String) {
        if (lock()) return

        DailyTextUtils.clipText(activity, address)
        DailyToast.showToast(activity, R.string.message_detail_copy_address, DailyToast.LENGTH_SHORT)

        stayDetail?.let {
            analytics.onEventClipAddressClick(activity, it.name)
        }
    }

    override fun onNavigatorClick() {
        if (lock()) return

        stayDetail?.let {
            val analyticsParam = NavigatorAnalyticsParam().apply {
                category = AnalyticsManager.Category.HOTEL_BOOKINGS
                action = AnalyticsManager.Action.HOTEL_DETAIL_NAVIGATION_APP_CLICKED
            }

            startActivityForResult(NavigatorDialogActivity.newInstance(activity, it.name,
                    it.latitude, it.longitude, false, analyticsParam), StayDetailActivity.REQUEST_CODE_NAVIGATOR)
        }
    }

    override fun onConciergeClick() {
        if (lock()) return

        viewInterface.showConciergeDialog(DialogInterface.OnDismissListener { unLockAll() })

        analytics.onEventConciergeClick(activity)
    }

    override fun onMoreRoomListClick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPriceTypeClick(priceType: PriceType) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConciergeFaqClick() {
        startActivity(FAQActivity.newInstance(activity))

        analytics.onEventFaqClick(activity)
    }

    override fun onConciergeHappyTalkClick() {
        stayDetail?.let {
            try {
                // 카카오톡 패키지 설치 여부
                activity.packageManager.getPackageInfo("com.kakao.talk", PackageManager.GET_META_DATA)

                startActivityForResult(HappyTalkCategoryDialog.newInstance(activity, HappyTalkCategoryDialog.CallScreen.SCREEN_STAY_DETAIL
                        , it.index, 0, it.name), StayDetailActivity.REQUEST_CODE_HAPPYTALK)
            } catch (e: Exception) {
                viewInterface.showSimpleDialog(null, getString(R.string.dialog_msg_not_installed_kakaotalk),
                        getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no),
                        View.OnClickListener { Util.installPackage(activity, "com.kakao.talk") }, null)
            }

            analytics.onEventHappyTalkClick(activity)
        } ?: Util.restartApp(activity)
    }

    override fun onConciergeCallClick() {
        startActivityForResult(CallDialogActivity.newInstance(activity), StayDetailActivity.REQUEST_CODE_CALL)

        analytics.onEventCallClick(activity)
    }

    override fun onRoomClick(stayRoom: StayRoom) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onTrueReviewClick() {
//        if(lock()) return
//
//        stayDetail?.let {
//            val analyticsParam = TrueReviewAnalyticsParam().apply {
//                category = it.category
//            }
//
//            startActivityForResult(StayTrueReviewActivity.newInstance(activity, it.index,
//                    mReviewScores, analyticsParam), StayDetailActivity.REQUEST_CODE_TRUE_VIEW)
//
//            analytics.onEventTrueReviewClick(activity)
//        } ?: Util.restartApp(activity)
    }

    override fun onTrueVRClick() {
//        if (lock()) return
//
//        stayDetail?.let { stayDetail ->
//            if (DailyPreference.getInstance(activity).isTrueVRCheckDataGuide) {
//                startActivityForResult(TrueVRActivity.newInstance(activity, stayDetail.index, mTrueVRList,
//                        Constants.PlaceType.HOTEL, stayDetail.category), StayDetailActivity.REQUEST_CODE_TRUE_VR)
//            } else {
//                viewInterface.showTrueVRDialog(CompoundButton.OnCheckedChangeListener { buttonView, checked ->
//                    DailyPreference.getInstance(activity).isTrueVRCheckDataGuide = checked
//                }, View.OnClickListener {
//                    startActivityForResult(TrueVRActivity.newInstance(activity, stayDetail.index, mTrueVRList,
//                            Constants.PlaceType.HOTEL, stayDetail.category), StayDetailActivity.REQUEST_CODE_TRUE_VR)
//                }, DialogInterface.OnDismissListener { unLockAll() })
//            }
//
//            analytics.onEventTrueVRClick(activity, stayDetail.index)
//        } ?: Util.restartApp(activity)
    }

    override fun onDownloadCouponClick() {
        if (lock()) return

        stayDetail?.let { stayDetail ->
            if (DailyHotel.isLogin()) {
                val intent = SelectStayCouponDialogActivity.newInstance(activity, stayDetail.index,
                        bookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT),
                        bookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT), stayDetail.category, stayDetail.name)
                startActivityForResult(intent, StayDetailActivity.REQUEST_CODE_DOWNLOAD_COUPON)

            } else {
                viewInterface.showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_detail_please_login),
                        getString(R.string.dialog_btn_login_for_benefit), getString(R.string.dialog_btn_text_close), {
                    startActivityForResult(LoginActivity.newInstance(activity, AnalyticsManager.Screen.DAILYHOTEL_DETAIL),
                            StayDetailActivity.REQUEST_CODE_LOGIN_IN_BY_COUPON)
                    analytics.onEventDownloadCouponByLogin(activity, true)
                }, {
                    analytics.onEventDownloadCouponByLogin(activity, false)
                }, {
                    analytics.onEventDownloadCouponByLogin(activity, false)
                }, { unLockAll() }, true)
            }

            analytics.onEventDownloadCoupon(activity, stayDetail.name)
        } ?: Util.restartApp(activity)
    }

    override fun onHideWishTooltipClick() {
        DailyPreference.getInstance(activity).isWishTooltip = false
        viewInterface.hideWishTooltip()
    }

    override fun onLoginClick() {
        if (lock()) return

        startActivityForResult(LoginActivity.newInstance(activity, AnalyticsManager.Screen.DAILYHOTEL_DETAIL), StayDetailActivity.REQUEST_CODE_LOGIN)
    }

    override fun onRewardClick() {
        if (lock()) return

        startActivityForResult(RewardActivity.newInstance(activity), StayDetailActivity.REQUEST_CODE_REWARD)
    }

    override fun onRewardGuideClick() {
        if (lock()) return

        startActivityForResult(DailyWebActivity.newInstance(activity, getString(R.string.label_daily_reward),
                DailyRemoteConfigPreference.getInstance(activity).keyRemoteConfigStaticUrlDailyReward), StayDetailActivity.REQUEST_CODE_WEB)
    }

    override fun onTrueAwardsClick() {
        if (lock()) return

        stayDetail?.let {
            viewInterface.showTrueAwardsDialog(it.awards, DialogInterface.OnDismissListener { unLockAll() })

            analytics.onEventTrueAwardsClick(activity, it.index)
        } ?: Util.restartApp(activity)
    }

    private fun notifyDetailDataSetChanged() {
        stayDetail?.let {

            if (defaultImageUrl.isTextEmpty() && it.hasImageInformation()) {
                defaultImageUrl = it.defaultImageUrl
            }

            status = if (isSoldOut()) Status.SOLD_OUT else Status.BOOKING

            if (DailyPreference.getInstance(activity).trueVRSupport > 0 && it.hasTrueVR()) {
            } else {
                viewInterface.setTrueVRVisible(false)
            }

            when {
                showCalendar -> {
                    showCalendar = false

                    if (it.hasRooms()) onCalendarClick()
                }

                showTrueVR -> {
                    showTrueVR = false

                    if (DailyPreference.getInstance(activity).trueVRSupport > 0) {
                        onTrueVRClick()
                    } else {
                        viewInterface.showSimpleDialog(null, getString(R.string.message_truevr_not_support_hardware), getString(R.string.dialog_btn_text_confirm), null)
                    }
                }
            }



            hasDeepLink = false
        } ?: Util.restartApp(activity)
    }

    private fun notifyRewardDataSetChanged() {
        stayDetail?.let {
            if (it.activeReward && it.provideRewardSticker) {
                viewInterface.setRewardVisible(true)

                if (DailyHotel.isLogin()) {
                    viewInterface.setRewardMember(DailyRemoteConfigPreference.getInstance(activity).keyRemoteConfigRewardStickerCardTitleMessage,
                            getString(R.string.label_reward_go_reward), it.rewardStickerCount,
                            DailyRemoteConfigPreference.getInstance(activity).getKeyRemoteConfigRewardStickerMemberMessage(it.rewardStickerCount))

                    viewInterface.stopCampaignStickerAnimation()
                } else {
                    val campaignEnabled = DailyRemoteConfigPreference.getInstance(activity).isKeyRemoteConfigRewardStickerCampaignEnabled
                    val campaignFreeNights: Int;
                    val descriptionText: String;

                    if (campaignEnabled) {
                        campaignFreeNights = DailyRemoteConfigPreference.getInstance(activity).keyRemoteConfigRewardStickerNonMemberCampaignFreeNights
                        descriptionText = DailyRemoteConfigPreference.getInstance(activity).keyRemoteConfigRewardStickerNonMemberCampaignMessage
                    } else {
                        campaignFreeNights = 0
                        descriptionText = DailyRemoteConfigPreference.getInstance(activity).keyRemoteConfigRewardStickerNonMemberDefaultMessage)
                    }

                    viewInterface.setRewardNonMember(DailyRemoteConfigPreference.getInstance(activity).keyRemoteConfigRewardStickerCardTitleMessage,
                            getString(R.string.label_reward_login), campaignFreeNights, descriptionText)
                            .also { campaignEnabled.runTrue { viewInterface.startCampaignStickerAnimation() } }
                }
            } else {
                viewInterface.setRewardVisible(false)
            }
        } ?: Util.restartApp(activity)
    }

    private fun notifyWishDataSetChanged() {
        stayDetail?.let {
            notifyWishDataSetChanged(it.wishCount, it.myWish)
        } ?: Util.restartApp(activity)
    }

    private fun notifyWishDataSetChanged(wishCount: Int, myWish: Boolean) {
        stayDetail?.let {
            viewInterface.setWishCount(wishCount)
            viewInterface.setWishSelected(myWish)
        } ?: Util.restartApp(activity)
    }
}