package com.daily.dailyhotel.screen.home.stay.inbound.detail

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.view.View
import android.widget.CompoundButton
import com.crashlytics.android.Crashlytics
import com.daily.base.BaseActivity
import com.daily.base.util.DailyImageSpan
import com.daily.base.util.DailyTextUtils
import com.daily.base.util.ExLog
import com.daily.base.util.ScreenUtils
import com.daily.base.widget.DailyToast
import com.daily.dailyhotel.base.BaseExceptionPresenter
import com.daily.dailyhotel.entity.*
import com.daily.dailyhotel.parcel.analytics.ImageListAnalyticsParam
import com.daily.dailyhotel.parcel.analytics.NavigatorAnalyticsParam
import com.daily.dailyhotel.parcel.analytics.StayDetailAnalyticsParam
import com.daily.dailyhotel.parcel.analytics.TrueReviewAnalyticsParam
import com.daily.dailyhotel.repository.local.RecentlyLocalImpl
import com.daily.dailyhotel.repository.remote.CalendarImpl
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl
import com.daily.dailyhotel.repository.remote.GoogleAddressRemoteImpl
import com.daily.dailyhotel.repository.remote.StayRemoteImpl
import com.daily.dailyhotel.screen.common.calendar.stay.StayCalendarActivity
import com.daily.dailyhotel.screen.common.dialog.call.CallDialogActivity
import com.daily.dailyhotel.screen.common.dialog.navigator.NavigatorDialogActivity
import com.daily.dailyhotel.screen.common.dialog.wish.WishDialogActivity
import com.daily.dailyhotel.screen.common.event.EventWebActivity
import com.daily.dailyhotel.screen.common.images.ImageListActivity
import com.daily.dailyhotel.screen.common.web.DailyWebActivity
import com.daily.dailyhotel.screen.home.stay.inbound.detail.rooms.StayRoomsActivity
import com.daily.dailyhotel.screen.home.stay.inbound.detail.truereview.StayTrueReviewActivity
import com.daily.dailyhotel.screen.home.stay.inbound.payment.StayPaymentActivity
import com.daily.dailyhotel.screen.mydaily.coupon.dialog.SelectStayCouponDialogActivity
import com.daily.dailyhotel.screen.mydaily.reward.RewardActivity
import com.daily.dailyhotel.storage.preference.DailyPreference
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference
import com.daily.dailyhotel.storage.preference.DailyUserPreference
import com.daily.dailyhotel.util.*
import com.twoheart.dailyhotel.DailyHotel
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.screen.common.HappyTalkCategoryDialog
import com.twoheart.dailyhotel.screen.common.TrueVRActivity
import com.twoheart.dailyhotel.screen.common.ZoomMapActivity
import com.twoheart.dailyhotel.screen.information.FAQActivity
import com.twoheart.dailyhotel.screen.mydaily.member.LoginActivity
import com.twoheart.dailyhotel.util.*
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function4
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

private const val DAYS_OF_MAX_COUNT = 60

class StayDetailPresenter(activity: StayDetailActivity)//
    : BaseExceptionPresenter<StayDetailActivity, StayDetailInterface.ViewInterface>(activity), StayDetailInterface.OnEventListener {

    internal enum class Status {
        NONE, BOOKING, SOLD_OUT, FINISH, ROOM_FILTER
    }

    enum class PriceType {
        AVERAGE, TOTAL
    }

    private val analytics = StayDetailAnalyticsImpl()
    private val stayRemoteImpl = StayRemoteImpl()
    private val commonRemoteImpl = CommonRemoteImpl()

    private val googleAddressRemoteImpl by lazy {
        GoogleAddressRemoteImpl()
    }

    private val calendarImpl = CalendarImpl()
    private val recentlyLocalImpl = RecentlyLocalImpl()

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
    private var showRoomPriceType: PriceType = PriceType.TOTAL
    private var bedTypeFilter: LinkedHashSet<String> = linkedSetOf()
    private var facilitiesFilter: LinkedHashSet<String> = linkedSetOf()
    private var tempBedTypeFilter: LinkedHashSet<String> = linkedSetOf()
    private var tempFacilitiesFilter: LinkedHashSet<String> = linkedSetOf()

    private var checkOneTime = false

    private val bookDateTime = StayBookDateTime()
    private val commonDateTime = CommonDateTime()

    override fun createInstanceViewInterface(): StayDetailInterface.ViewInterface {
        return StayDetailView(activity, this)
    }

    override fun constructorInitialize(activity: StayDetailActivity) {
        setContentView(R.layout.activity_stay_detail_data)

        isRefresh = false
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

                    showRoomPriceType = if (bookDateTime.nights == 1) PriceType.TOTAL else PriceType.AVERAGE

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

            gradientType = try {
                StayDetailActivity.TransGradientType.valueOf(intent.getStringExtra(StayDetailActivity.INTENT_EXTRA_DATA_CALL_GRADIENT_TYPE))
            } catch (e: Exception) {
                StayDetailActivity.TransGradientType.NONE
            }

            stayIndex = intent.getIntExtra(StayDetailActivity.INTENT_EXTRA_DATA_STAY_INDEX, 0)
            stayName = intent.getStringExtra(StayDetailActivity.INTENT_EXTRA_DATA_STAY_NAME)
            defaultImageUrl = intent.getStringExtra(StayDetailActivity.INTENT_EXTRA_DATA_IMAGE_URL)
            viewPrice = intent.getIntExtra(StayDetailActivity.INTENT_EXTRA_DATA_LIST_PRICE, StayDetailActivity.NONE_PRICE)

            bookDateTime.setCheckInDateTime(intent.getStringExtra(StayDetailActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME))
                    .setCheckOutDateTime(intent.getStringExtra(StayDetailActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME))
                    .validate().runFalse { throw IllegalArgumentException() }

            showRoomPriceType = if (bookDateTime.nights == 1) PriceType.TOTAL else PriceType.AVERAGE

            intent.getStringArrayListExtra(StayDetailActivity.REQUEST_CODE_BEDTYPE_FILTER)?.let { bedTypeFilter.addAll(it) }
            intent.getStringArrayListExtra(StayDetailActivity.REQUEST_CODE_FACILITIES_FILTER)?.let { facilitiesFilter.addAll(it) }

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

        if (isTransitionEnabled()) {
            screenLock(false)

            onRefresh(viewInterface.getSharedElementTransition(gradientType), screenLockDelay(2))
        } else if (!hasDeepLink) {
            isRefresh = true
        }
    }

    private fun isTransitionEnabled(): Boolean {
        return !hasDeepLink && isUsedMultiTransition
    }

    private fun screenLockDelay(delay: Int): Disposable {

        val disposable = Completable.timer(2, TimeUnit.SECONDS).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe { screenLock(true) }

        addCompositeDisposable(disposable)

        return disposable
    }

    override fun onStart() {
        super.onStart()

        isRefresh.runTrue { onRefresh(true) }
    }

    override fun onResume() {
        super.onResume()

        isRefresh.runTrue { onRefresh(true) }

        if (!DailyHotel.isLogin() && DailyRemoteConfigPreference.getInstance(activity).isKeyRemoteConfigRewardStickerCampaignEnabled && stayDetail != null) {
            viewInterface.startRewardStickerAnimation()
        }
    }

    override fun onPause() {
        super.onPause()

        viewInterface.stopRewardStickerAnimation()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onPostFinish() {
        super.onPostFinish()

        if (!isUsedMultiTransition) activity.overridePendingTransition(R.anim.hold, R.anim.slide_out_right)
    }

    override fun onBackPressed(): Boolean {
        when (status) {
            Status.FINISH -> return false

            Status.ROOM_FILTER -> {
                onCloseRoomFilterClick()
                return true
            }

            Status.NONE -> return false

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

                    Completable.timer(300, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).subscribe { activity.onBackPressed() }

                    return true
                }
            }
        }

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

            StayDetailActivity.REQUEST_CODE_DOWNLOAD_COUPON -> onCouponActivityResult(resultCode, intent)

            StayDetailActivity.REQUEST_CODE_LOGIN_IN_BY_COUPON -> onLoginByCouponActivityResult(resultCode, intent)

            StayDetailActivity.REQUEST_CODE_WISH_DIALOG -> onWishDialogActivityResult(resultCode, intent)

            StayDetailActivity.REQUEST_CODE_ROOM -> onRoomActivityResult(resultCode, intent)
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

                        val previousOneNights = bookDateTime.nights == 1

                        bookDateTime.setBookDateTime(checkInDateTime, checkOutDateTime)
                        isRefresh = true

                        val currentOneNights = bookDateTime.nights == 1

                        if (previousOneNights && !currentOneNights) {
                            onPriceTypeClick(PriceType.AVERAGE)
                        } else {
                            onPriceTypeClick(if (currentOneNights) PriceType.TOTAL else showRoomPriceType)
                        }

                        viewInterface.scrollRoomInformation()
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

    private fun onCouponActivityResult(resultCode: Int, intent: Intent?) {
        intent?.let {
            it.getBooleanExtra(SelectStayCouponDialogActivity.INTENT_EXTRA_HAS_DOWNLOADABLE_COUPON, true).let {
                viewInterface.setCouponButtonEnabled(it)

                stayDetail?.benefitInformation?.coupon?.couponDiscount?.let {
                    viewInterface.setCouponButtonText(getString(R.string.label_detail_complete_coupon_download,
                            DailyTextUtils.getPriceFormat(activity, it, false)), false)
                }
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

    private fun onWishDialogActivityResult(resultCode: Int, intent: Intent?) {
        when (resultCode) {
            BaseActivity.RESULT_CODE_REFRESH,
            Activity.RESULT_OK -> {
                intent?.let {
                    val wish = it.getBooleanExtra(WishDialogActivity.INTENT_EXTRA_DATA_WISH, false)

                    stayDetail?.let {
                        it.wish = wish
                        it.wishCount += if (wish) 1 else -1

                        notifyWishDataSetChanged()

                        setResult(BaseActivity.RESULT_CODE_REFRESH)
                    }
                }
            }

            else -> notifyWishDataSetChanged()
        }
    }

    private fun onRoomActivityResult(resultCode: Int, intent: Intent?) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                intent?.let {
                    val roomIndex = it.getIntExtra(StayRoomsActivity.INTENT_EXTRA_ROOM_INDEX, -1)

                    stayDetail?.let {
                        val room = getRoom(it.roomInformation?.roomList, roomIndex) ?: return

                        startActivityForResult(StayPaymentActivity.newInstance(activity, it.index,
                                it.baseInformation?.name,
                                it.imageList?.get(0)?.imageMap?.bigUrl,
                                room.index, room.amountInformation.discountTotal, room.name,
                                bookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT),
                                bookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT),
                                false, it.baseInformation?.category,
                                it.addressInformation?.latitude ?: 0.0,
                                it.addressInformation?.longitude ?: 0.0,
                                analytics.getStayPaymentAnalyticsParam(it, room)),
                                StayDetailActivity.REQUEST_CODE_PAYMENT)
                    }
                }
            }
        }
    }

    private fun getRoom(roomList: List<Room>?, roomIndex: Int): Room? {
        roomList?.forEach {
            if (it.index == roomIndex) {
                return it
            }
        }

        return null
    }

    @Synchronized
    override fun onRefresh(showProgress: Boolean) {
        if (isFinish || !isRefresh) return

        if (!bookDateTime.validate()) {
            Util.restartApp(activity)
            return
        }

        isRefresh = false
        screenLock(showProgress)

        onRefresh(Observable.just(true), null)
    }

    private fun onRefresh(observable: Observable<Boolean>, disposable: Disposable?) {
        addCompositeDisposable(Observable.zip(observable,
                stayRemoteImpl.getDetail(stayIndex, bookDateTime),
                calendarImpl.getStayUnavailableCheckInDates(stayIndex, DAYS_OF_MAX_COUNT, false),
                commonRemoteImpl.commonDateTime, Function4<Boolean, StayDetail, List<String>, CommonDateTime, StayDetail> { _, stayDetail, soldOutDayList, commonDateTime ->
            this@StayDetailPresenter.commonDateTime.setDateTime(commonDateTime)
            this@StayDetailPresenter.soldOutDays = soldOutDayList.map { it.replace("-".toRegex(), "").toInt() }.toIntArray()
            this@StayDetailPresenter.stayDetail = stayDetail

            writeRecentlyViewedPlace(stayDetail)

            stayDetail
        }).observeOn(AndroidSchedulers.mainThread()).subscribe({ stayDetail ->
            notifyDetailDataSetChanged()
            notifyWishDataSetChanged()
            notifyRewardDataSetChanged()

            DailyPreference.getInstance(activity).isWishTooltip.runTrue { showWishTooltip() }

            stayDetail.trueReviewInformation?.reviewTotalCount.takeGreaterThanZero { analytics.onEventShowTrueReview(activity, stayDetail.index) }
            stayDetail.benefitInformation?.coupon?.couponDiscount.takeGreaterThanZero { analytics.onEventShowCoupon(activity, stayDetail.index) }
            stayDetail.baseInformation?.awards?.title.takeNotEmpty { analytics.onEventTrueAwards(activity, stayDetail.index) }

            unLockAll()
            disposable?.dispose()
        }, {
            ExLog.e(it.toString())

            disposable?.dispose()
            onHandleErrorAndFinish(it)

            setResult(BaseActivity.RESULT_CODE_REFRESH)
        }))
    }

    private fun writeRecentlyViewedPlace(stayDetail: StayDetail) {
        val regionName = stayDetail.province?.name
        val observable: Observable<String> =
                if (regionName.isTextEmpty())
                    googleAddressRemoteImpl.getLocationAddress(stayDetail.addressInformation?.latitude
                            ?: 0.0
                            , stayDetail.addressInformation?.longitude ?: 0.0).map({ it.address })
                else Observable.just(regionName)

        val imageUrl = if (defaultImageUrl.isTextEmpty()) stayDetail.imageList?.get(0)?.imageMap?.bigUrl else defaultImageUrl

        addCompositeDisposable(observable.flatMap({
            recentlyLocalImpl.addRecentlyItem(activity, Constants.ServiceType.HOTEL,
                    stayDetail.index, stayDetail.baseInformation?.name, null,
                    imageUrl, it, false)
        }).subscribe({}, { ExLog.e(it.toString()) }))
    }

    private fun showWishTooltip() {
        viewInterface.showWishTooltip()

        addCompositeDisposable(Completable.timer(5, TimeUnit.SECONDS).subscribeOn(Schedulers.newThread())
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

        viewInterface.showShareDialog()

        analytics.onEventShare(activity)

        unLockAll()
    }

    override fun onWishClick() {
        if (lock()) return

        stayDetail?.let {
            val wish = !it.wish
            val wishCount = it.wishCount + if (wish) 1 else -1

            notifyWishDataSetChanged(wishCount, wish)

            startActivityForResult(WishDialogActivity.newInstance(activity, Constants.ServiceType.HOTEL,
                    stayIndex, wish, AnalyticsManager.Screen.DAILYHOTEL_DETAIL), StayDetailActivity.REQUEST_CODE_WISH_DIALOG)

            analytics.onEventWishClick(activity, bookDateTime, it, viewPrice, wish)
        } ?: Util.restartApp(activity)
    }

    override fun onShareKakaoClick() {
        if (lock()) return

        stayDetail?.let { stayDetail ->
            try {
                val name: String? = DailyUserPreference.getInstance(activity).name
                val urlFormat = "https://mobile.dailyhotel.co.kr/stay/%d?dateCheckIn=%s&stays=%d&utm_source=share&utm_medium=stay_detail_kakaotalk"
                val longUrl = String.format(Locale.KOREA, urlFormat, stayDetail.index, bookDateTime.getCheckInDateTime("yyyy-MM-dd"), bookDateTime.nights)

                addCompositeDisposable(commonRemoteImpl.getShortUrl(longUrl).observeOn(AndroidSchedulers.mainThread()).subscribe({ shortUrl ->
                    unLockAll()

                    KakaoLinkManager.newInstance(activity).shareStay(name, stayDetail.baseInformation?.name, stayDetail.addressInformation?.address,
                            stayDetail.index, stayDetail.imageList?.get(0)?.imageMap?.smallUrl, shortUrl, bookDateTime)
                }, {
                    unLockAll()

                    KakaoLinkManager.newInstance(activity).shareStay(name, stayDetail.baseInformation?.name, stayDetail.addressInformation?.address,
                            stayDetail.index, stayDetail.imageList?.get(0)?.imageMap?.smallUrl,
                            "https://mobile.dailyhotel.co.kr/stay/" + stayDetail.index, bookDateTime)
                }))

                analytics.onEventShareKakaoClick(activity, DailyHotel.isLogin(), DailyUserPreference.getInstance(activity).type,
                        DailyUserPreference.getInstance(activity).isBenefitAlarm, stayDetail.index, stayDetail.baseInformation?.name)
            } catch (e: Exception) {
                unLockAll()

                ExLog.e(e.toString())
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
                val message = getString(R.string.message_detail_stay_share_sms, name, stayDetail.baseInformation?.name,
                        bookDateTime.getCheckInDateTime("yyyy.MM.dd(EEE)"),
                        bookDateTime.getCheckOutDateTime("yyyy.MM.dd(EEE)"),
                        bookDateTime.nights, bookDateTime.nights + 1, stayDetail.addressInformation?.address)

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
        if (stayDetail.filterIf({ !it.imageList.isNotNullAndNotEmpty() }) || lock()) return

        stayDetail?.let {
            startActivityForResult(ImageListActivity.newInstance(activity, it.baseInformation?.name,
                    it.imageList, position,
                    ImageListAnalyticsParam().apply { serviceType = Constants.ServiceType.HOTEL }),
                    StayDetailActivity.REQUEST_CODE_IMAGE_LIST)

            analytics.onEventImageClick(activity, it.baseInformation?.name)
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
        return stayDetail.filterIf({ !it.roomInformation?.roomList.isNotNullAndNotEmpty() }, true)
    }

    override fun onRoomFilterClick() {
        if (lock()) return

        status = Status.ROOM_FILTER

        stayDetail?.let {
            tempBedTypeFilter.clear()
            tempBedTypeFilter.addAll(bedTypeFilter)
            tempFacilitiesFilter.clear()
            tempFacilitiesFilter.addAll(facilitiesFilter)

            viewInterface.setSelectedRoomFilter(tempBedTypeFilter, tempFacilitiesFilter)
            viewInterface.setSelectedRoomFilterCount(getRoomFilterCount(it.roomInformation?.roomList, tempBedTypeFilter, tempFacilitiesFilter))

            addCompositeDisposable(viewInterface.showRoomFilter().observeOn(AndroidSchedulers.mainThread()).subscribe { unLockAll() })

            analytics.onEventRoomFilterClick(activity)
        } ?: Util.restartApp(activity)

    }

    override fun onMapClick() {
        if (lock()) return

        stayDetail?.let {
            if (Util.isGooglePlayServicesAvailable(activity)) {
                startActivityForResult(ZoomMapActivity.newInstance(activity, ZoomMapActivity.SourceType.HOTEL,
                        it.baseInformation?.name, it.addressInformation?.address,
                        it.addressInformation?.latitude ?: 0.0, it.addressInformation?.longitude
                        ?: 0.0, false), StayDetailActivity.REQUEST_CODE_MAP)
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

            analytics.onEventMapClick(activity, it.baseInformation?.name)
        } ?: Util.restartApp(activity)
    }

    override fun onClipAddressClick() {
        if (lock()) return

        DailyTextUtils.clipText(activity, stayDetail?.addressInformation?.address)
        DailyToast.showToast(activity, R.string.message_detail_copy_address, DailyToast.LENGTH_SHORT)

        stayDetail?.let { analytics.onEventClipAddressClick(activity, it.baseInformation?.name) }

        unLockAll()
    }

    override fun onNavigatorClick() {
        if (lock()) return

        stayDetail?.let {
            val analyticsParam = NavigatorAnalyticsParam().apply {
                category = AnalyticsManager.Category.HOTEL_BOOKINGS
                action = AnalyticsManager.Action.HOTEL_DETAIL_NAVIGATION_APP_CLICKED
            }

            startActivityForResult(NavigatorDialogActivity.newInstance(activity, it.baseInformation?.name,
                    it.addressInformation?.latitude ?: 0.0, it.addressInformation?.longitude
                    ?: 0.0, false, analyticsParam), StayDetailActivity.REQUEST_CODE_NAVIGATOR)
        } ?: Util.restartApp(activity)
    }

    override fun onConciergeClick() {
        if (lock()) return

        viewInterface.showConciergeDialog(DialogInterface.OnDismissListener { unLockAll() })

        analytics.onEventConciergeClick(activity)
    }

    override fun onMoreRoomClick(expanded: Boolean) {
        if (lock()) return

        stayDetail?.let {
            val completable: Completable = if (expanded) {
                if (checkedRoomFilter()) {
                    setResetRoomFilter()
                    setRoomFilter(bookDateTime, it.roomInformation?.roomList, bedTypeFilter, facilitiesFilter)
                    viewInterface.scrollRoomInformation()

                    analytics.onEventResetFilterAndShowAllRoom(activity)

                    viewInterface.showMoreRooms(false)
                } else {
                    viewInterface.hideMoreRooms()

                    analytics.onEventUnfoldRoom(activity, false)

                    Completable.complete()
                }
            } else {
                if (checkedRoomFilter()) {
                    val showViewRoomMaxCount = 5

                    if (getRoomFilterCount(stayDetail?.roomInformation?.roomList, bedTypeFilter, facilitiesFilter) > showViewRoomMaxCount) {
                        analytics.onEventFoldRoom(activity, true)
                        viewInterface.showMoreRooms(true)
                    } else {
                        setResetRoomFilter()
                        setRoomFilter(bookDateTime, it.roomInformation?.roomList, bedTypeFilter, facilitiesFilter)
                        viewInterface.scrollRoomInformation()

                        analytics.onEventResetFilterAndShowAllRoom(activity)

                        viewInterface.showMoreRooms(false)
                    }
                } else {
                    analytics.onEventFoldRoom(activity, false)
                    viewInterface.showMoreRooms(true)
                }
            }

            addCompositeDisposable(completable.observeOn(AndroidSchedulers.mainThread()).subscribe {
                applyMoreRoomAction()
                unLockAll()
            })
        } ?: Util.restartApp(activity)
    }

    override fun onPriceTypeClick(priceType: PriceType) {
        if (lock()) return

        showRoomPriceType = priceType
        viewInterface.setPriceAverageType(showRoomPriceType.compareTo(PriceType.AVERAGE) == 0)

        unLockAll()
    }

    override fun onConciergeFaqClick() {
        startActivity(FAQActivity.newInstance(activity))

        analytics.onEventFaqClick(activity)
    }

    override fun onConciergeHappyTalkClick() {
        stayDetail?.let {
            startActivityForResult(HappyTalkCategoryDialog.newInstance(activity, HappyTalkCategoryDialog.CallScreen.SCREEN_STAY_DETAIL
                    , it.index, 0, it.baseInformation?.name), StayDetailActivity.REQUEST_CODE_HAPPYTALK)

            analytics.onEventHappyTalkClick(activity)
        } ?: Util.restartApp(activity)
    }

    override fun onConciergeCallClick() {
        startActivityForResult(CallDialogActivity.newInstance(activity), StayDetailActivity.REQUEST_CODE_CALL)

        analytics.onEventCallClick(activity)
    }

    override fun onRoomClick(room: Room) {
        if (lock()) return

        stayDetail?.let { stayDetail ->
            stayDetail.roomInformation?.let {
                it.roomList.takeNotEmpty {
                    startActivityForResult(StayRoomsActivity.newInstance(activity, getFilteredRoomList(it, bedTypeFilter, facilitiesFilter),
                            getRoomPosition(it, room),
                            bookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT),
                            bookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT),
                            stayDetail.index,
                            stayDetail.baseInformation?.category,
                            stayDetail.activeReward), StayDetailActivity.REQUEST_CODE_ROOM)
                }
            }
        } ?: Util.restartApp(activity)
    }

    private fun getRoomPosition(roomList: List<Room>?, stayRoom: Room?): Int {
        roomList?.forEachIndexed { index, room ->
            if (room.index == stayRoom?.index) {
                return index
            }
        }

        return 0
    }

    override fun onTrueReviewClick() {
        if (lock()) return

        stayDetail?.let {
            val analyticsParam = TrueReviewAnalyticsParam().apply {
                category = it.baseInformation?.category
            }

            val reviewScores = ReviewScores().apply {
                reviewScoreTotalCount = it.trueReviewInformation?.reviewTotalCount ?: 0
                reviewScoreList = it.trueReviewInformation?.reviewScores?.map {
                    ReviewScore().apply {
                        type = it.type
                        scoreAverage = it.average
                    }
                }
            }

            startActivityForResult(StayTrueReviewActivity.newInstance(activity, it.index,
                    reviewScores, analyticsParam), StayDetailActivity.REQUEST_CODE_TRUE_VIEW)

            analytics.onEventTrueReviewClick(activity)
        } ?: Util.restartApp(activity)
    }

    override fun onTrueVRClick() {
        if (lock()) return

        stayDetail?.let { stayDetail ->
            if (DailyPreference.getInstance(activity).isTrueVRCheckDataGuide) {

                startActivityForResult(TrueVRActivity.newInstance(activity, stayDetail.index,
                        stayDetail.vrInformation?.map {
                            TrueVR().apply {
                                name = it.name
                                type = it.type
                                typeIndex = it.typeIndex
                                url = it.url
                            }
                        }, Constants.PlaceType.HOTEL, stayDetail.baseInformation?.category), StayDetailActivity.REQUEST_CODE_TRUE_VR)
            } else {
                viewInterface.showVRDialog(CompoundButton.OnCheckedChangeListener { _, isChecked ->
                    DailyPreference.getInstance(activity).isTrueVRCheckDataGuide = isChecked
                }, View.OnClickListener {
                    startActivityForResult(TrueVRActivity.newInstance(activity, stayDetail.index,
                            stayDetail.vrInformation?.map {
                                TrueVR().apply {
                                    name = it.name
                                    type = it.type
                                    typeIndex = it.typeIndex
                                    url = it.url
                                }
                            },
                            Constants.PlaceType.HOTEL, stayDetail.baseInformation?.category), StayDetailActivity.REQUEST_CODE_TRUE_VR)
                }, DialogInterface.OnDismissListener { unLockAll() })
            }

            analytics.onEventTrueVRClick(activity, stayDetail.index)
        } ?: Util.restartApp(activity)
    }

    override fun onDownloadCouponClick() {
        if (lock()) return

        stayDetail?.let { stayDetail ->
            if (DailyHotel.isLogin()) {
                val intent = SelectStayCouponDialogActivity.newInstance(activity, stayDetail.index,
                        bookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT),
                        bookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT),
                        stayDetail.baseInformation?.category
                                ?: toString(), stayDetail.baseInformation?.name ?: "")
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

            analytics.onEventDownloadCoupon(activity, stayDetail.baseInformation?.name)
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
            viewInterface.showTrueAwardsDialog(it.baseInformation?.awards, DialogInterface.OnDismissListener { unLockAll() })

            analytics.onEventTrueAwardsClick(activity, it.index)
        } ?: Util.restartApp(activity)
    }

    override fun onShowRoomClick() {
        if (lock()) return

        stayDetail?.let { stayDetail ->
            stayDetail.roomInformation?.let {
                it.roomList.takeNotEmpty {
                    startActivityForResult(StayRoomsActivity.newInstance(activity, getFilteredRoomList(it, bedTypeFilter, facilitiesFilter),
                            0,
                            bookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT),
                            bookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT),
                            stayDetail.index,
                            stayDetail.baseInformation?.category,
                            stayDetail.activeReward), StayDetailActivity.REQUEST_CODE_ROOM)
                }
            }
        } ?: Util.restartApp(activity)
    }

    override fun onRoomInformationClick() {
        if (lock()) return

        viewInterface.scrollRoomInformation()

        unLockAll()
    }

    override fun onStayInformationClick() {
        if (lock()) return

        viewInterface.scrollStayInformation()

        unLockAll()
    }

    override fun onSelectedBedTypeFilter(selected: Boolean, bedType: String) {
        if (selected) {
            tempBedTypeFilter.add(bedType)
        } else {
            tempBedTypeFilter.remove(bedType)
        }

        stayDetail?.let {
            viewInterface.setSelectedRoomFilterCount(getRoomFilterCount(it.roomInformation?.roomList, tempBedTypeFilter, tempFacilitiesFilter))
        } ?: Util.restartApp(activity)
    }

    override fun onSelectedFacilitiesFilter(selected: Boolean, facilities: String) {
        if (selected) {
            tempFacilitiesFilter.add(facilities)
        } else {
            tempFacilitiesFilter.remove(facilities)
        }

        stayDetail?.let {
            viewInterface.setSelectedRoomFilterCount(getRoomFilterCount(it.roomInformation?.roomList, tempBedTypeFilter, tempFacilitiesFilter))
        } ?: Util.restartApp(activity)
    }

    override fun onCloseRoomFilterClick() {
        if (lock()) return

        status = Status.BOOKING

        tempBedTypeFilter.clear()
        tempFacilitiesFilter.clear()

        addCompositeDisposable(viewInterface.hideRoomFilter().observeOn(AndroidSchedulers.mainThread()).subscribe { unLockAll() })
    }

    override fun onResetRoomFilterClick() {
        if (lock()) return

        tempBedTypeFilter.clear()
        tempFacilitiesFilter.clear()

        stayDetail?.let {
            viewInterface.setSelectedRoomFilter(tempBedTypeFilter, tempFacilitiesFilter)
            viewInterface.setSelectedRoomFilterCount(it.roomInformation?.roomList?.size ?: 0)
        }

        unLockAll()
    }

    private fun setResetRoomFilter() {
        bedTypeFilter.clear()
        facilitiesFilter.clear()

        stayDetail?.let {
            viewInterface.setSelectedRoomFilter(bedTypeFilter, facilitiesFilter)
            viewInterface.setSelectedRoomFilterCount(it.roomInformation?.roomList?.size ?: 0)
        }
    }

    override fun onScrolledBaseInformation() {
        analytics.onScreen(activity)
    }

    override fun onScrolledRoomInformation() {
        analytics.onScreenRoomInformation(activity)
    }

    override fun onScrolledStayInformation() {
        analytics.onScreenStayInformation(activity)
    }

    override fun onConfirmRoomFilterClick() {
        if (lock()) return

        bedTypeFilter.clear()
        bedTypeFilter.addAll(tempBedTypeFilter)

        facilitiesFilter.clear()
        facilitiesFilter.addAll(tempFacilitiesFilter)

        stayDetail?.let {
            notifyRoomDataSetChanged()
            viewInterface.scrollRoomInformation()
            unLockAll()
            onCloseRoomFilterClick()

            analytics.onEventConfirmRoomFilterClick(activity, bedTypeFilter, facilitiesFilter)
        } ?: Util.restartApp(activity)
    }

    private fun notifyRoomDataSetChanged() {
        stayDetail?.let {
            if (it.roomInformation?.roomList.isNotNullAndNotEmpty()) {
                setRoomFilter(bookDateTime, it.roomInformation?.roomList, bedTypeFilter, facilitiesFilter)

                if (getRoomFilterCount(it.roomInformation?.roomList, bedTypeFilter, facilitiesFilter) == 0) {
                    viewInterface.setEmptyRoomVisible(true, checkedRoomFilter())
                    viewInterface.setEmptyRoomText(activity.getString(R.string.message_stay_empty_room))
                    viewInterface.setActionButtonEnabled(false)
                    viewInterface.setRoomActionButtonVisible(false)
                } else {
                    viewInterface.setEmptyRoomVisible(false, checkedRoomFilter())
                    viewInterface.setActionButtonEnabled(true)
                    viewInterface.setPriceAverageTypeVisible(bookDateTime.nights > 1)
                }

                applyMoreRoomAction()

                viewInterface.setActionButtonText(getString(R.string.label_stay_detail_view_room_detail))
                viewInterface.setPriceAverageType(showRoomPriceType.compareTo(PriceType.AVERAGE) == 0)
            } else {
                setRoomFilter(bookDateTime, null, bedTypeFilter, facilitiesFilter)

                viewInterface.setEmptyRoomVisible(true, checkedRoomFilter())
                viewInterface.setActionButtonEnabled(false)
                viewInterface.setActionButtonText(getString(R.string.label_soldout))
                viewInterface.setEmptyRoomText(activity.getString(R.string.message_stay_soldout_room))
            }
        }
    }

    private fun notifyDetailDataSetChanged() {
        stayDetail?.let {
            if (defaultImageUrl.isTextEmpty() && it.imageList.isNotNullAndNotEmpty()) {
                defaultImageUrl = it.imageList?.get(0)?.imageMap?.bigUrl
            }

            viewInterface.apply {
                setScrollViewVisible(true)
                setVRVisible(DailyPreference.getInstance(activity).trueVRSupport > 0 && it.vrInformation.isNotNullAndNotEmpty())
                setMoreImageVisible(it.imageList.isNotNullAndNotEmpty() && it.imageList!!.size > 1)

                it.imageList.takeNotEmpty { setImageList(it) }
                it.baseInformation?.let { setBaseInformation(it, bookDateTime.nights > 1, isSoldOut()) }

                setTrueReviewInformationVisible(it.trueReviewInformation.letNotNullTrueElseNullFalse { setTrueReviewInformation(it) })

                if (hasBenefitContents(it.benefitInformation)) {
                    setBenefitInformationVisible(true)
                    setBenefitInformation(it.benefitInformation!!)
                } else {
                    setBenefitInformationVisible(false)
                }

                notifyRoomDataSetChanged()

                setDailyCommentVisible(it.dailyCommentList.letNotNullTrueElseNullFalse { setDailyComment(it) })
                setFacilities(it.totalRoomCount, it.facilitiesList)
                setAddressInformationVisible(it.addressInformation.letNotNullTrueElseNullFalse { setAddressInformation(it) })
                setCheckTimeInformationVisible(it.checkTimeInformation.letNotNullTrueElseNullFalse { setCheckTimeInformation(it) })

                if (hasDetailInformation(it.detailInformation, it.breakfastInformation)) {
                    setDetailInformationVisible(true)
                    setDetailInformation(it.detailInformation, it.breakfastInformation)
                } else {
                    setDetailInformationVisible(false)
                }

                if (hasRefundInformation(it.refundInformation)) {
                    setCancellationAndRefundPolicyVisible(true)
                    setCancellationAndRefundPolicy(it.refundInformation!!, it.hasNRDRoom)
                } else {
                    setCancellationAndRefundPolicyVisible(false)
                }

                if (hasCheckInformation(it.checkInformation)) {
                    setCheckInformationVisible(true);
                    setCheckInformation(it.checkInformation!!)
                } else {
                    setCheckInformationVisible(false);
                }
                setConciergeInformation()

                viewInterface.setSelectedRoomFilterCount(getRoomFilterCount(it.roomInformation?.roomList, bedTypeFilter, facilitiesFilter))
            }

            if (checkOneTime == false) {
                checkOneTime = true

                when {
                    isSoldOut() -> {
                        setResult(BaseActivity.RESULT_CODE_REFRESH, Intent().putExtra(StayDetailActivity.INTENT_EXTRA_DATA_SOLD_OUT, true))

                        viewInterface.showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_stay_detail_sold_out)//
                                , getString(R.string.label_changing_date), { onCalendarClick() }, null, true)
                    }

                    getRoomFilterCount(it.roomInformation?.roomList, bedTypeFilter, facilitiesFilter) == 0 -> {
                        viewInterface.showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_stay_filtered_empty_room)//
                                , getString(R.string.dialog_btn_text_confirm), { _ ->
                            setResetRoomFilter()
                            setRoomFilter(bookDateTime, it.roomInformation?.roomList, bedTypeFilter, facilitiesFilter)
                            notifyRoomDataSetChanged()
                        }, null, true)
                    }

                    else -> checkChangedPrice(hasDeepLink, it, viewPrice, true)
                }
            }

            status = if (isSoldOut()) {
                analytics.onScreenSoldOut(activity)
                Status.SOLD_OUT
            } else {
                analytics.onScreen(activity, bookDateTime, stayDetail, viewPrice, bedTypeFilter, facilitiesFilter)
                Status.BOOKING
            }

            when {
                showCalendar -> {
                    showCalendar = false

                    if (!isSoldOut()) onCalendarClick()
                }

                showTrueVR -> {
                    showTrueVR = false

                    if (DailyPreference.getInstance(activity).trueVRSupport > 0) {
                        it.vrInformation.isNotNullAndNotEmpty().runTrue { onTrueVRClick() }
                    } else {
                        viewInterface.showSimpleDialog(null, getString(R.string.message_truevr_not_support_hardware), getString(R.string.dialog_btn_text_confirm), null)
                    }
                }
            }

            hasDeepLink = false
        } ?: Util.restartApp(activity)
    }

    private fun hasBenefitContents(benefitInformation: StayDetail.BenefitInformation?): Boolean {
        return benefitInformation != null
                && (!benefitInformation.title.isTextEmpty()
                || benefitInformation.contentList.isNotNullAndNotEmpty()
                || (benefitInformation.coupon != null && benefitInformation.coupon?.couponDiscount != 0))
    }

    private fun hasDetailInformation(detailInformation: StayDetail.DetailInformation?, breakfastInformation: StayDetail.BreakfastInformation?): Boolean {
        if (detailInformation?.itemList.isNotNullAndNotEmpty()) return true

        if (breakfastInformation?.items.isNotNullAndNotEmpty()) return true

        if (breakfastInformation?.descriptionList.isNotNullAndNotEmpty()) return true

        return false
    }

    private fun hasRefundInformation(refundInformation: StayDetail.RefundInformation?): Boolean {
        if (refundInformation?.contentList.isNotNullAndNotEmpty()) return true

        return false
    }

    private fun hasCheckInformation(checkInformation: StayDetail.CheckInformation?): Boolean {
        return checkInformation != null
                && (!checkInformation.title.isTextEmpty()
                || checkInformation.contentList.isNotNullAndNotEmpty()
                || checkInformation.waitingForBooking)
    }

    private fun checkChangedPrice(isDeepLink: Boolean, stayDetail: StayDetail, listViewPrice: Int, compareListPrice: Boolean) {
        if (!isDeepLink && compareListPrice) {
            val hasPrice = if (listViewPrice == StayDetailActivity.NONE_PRICE) {
                true
            } else {
                stayDetail.roomInformation?.roomList?.any { listViewPrice == it.amountInformation.discountAverage }
            }

            if (hasPrice != true) {
                setResult(BaseActivity.RESULT_CODE_REFRESH,
                        Intent().putExtra(com.daily.dailyhotel.screen.home.stay.inbound.detail.StayDetailActivity.INTENT_EXTRA_DATA_CHANGED_PRICE, true))

                viewInterface.showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_stay_detail_changed_price)//
                        , getString(R.string.dialog_btn_text_confirm), null)

                analytics.onEventChangedPrice(activity, isDeepLink, stayDetail.baseInformation?.name, false)
            }
        }
    }

    private fun notifyRewardDataSetChanged() {
        stayDetail?.let {
            if (it.activeReward && it.baseInformation?.provideRewardSticker == true) {
                viewInterface.setRewardVisible(true)

                if (DailyHotel.isLogin()) {
                    viewInterface.setRewardMemberInformation(DailyRemoteConfigPreference.getInstance(activity).keyRemoteConfigRewardStickerCardTitleMessage,
                            getString(R.string.label_reward_go_reward), it.rewardStickerCount,
                            DailyRemoteConfigPreference.getInstance(activity).getKeyRemoteConfigRewardStickerMemberMessage(it.rewardStickerCount))

                    viewInterface.stopRewardStickerAnimation()
                } else {
                    val campaignEnabled = DailyRemoteConfigPreference.getInstance(activity).isKeyRemoteConfigRewardStickerCampaignEnabled
                    val campaignFreeNights: Int
                    val descriptionText: String

                    if (campaignEnabled) {
                        campaignFreeNights = DailyRemoteConfigPreference.getInstance(activity).keyRemoteConfigRewardStickerNonMemberCampaignFreeNights
                        descriptionText = DailyRemoteConfigPreference.getInstance(activity).keyRemoteConfigRewardStickerNonMemberCampaignMessage
                    } else {
                        campaignFreeNights = 0
                        descriptionText = DailyRemoteConfigPreference.getInstance(activity).keyRemoteConfigRewardStickerNonMemberDefaultMessage
                    }

                    viewInterface.setRewardNonMemberInformation(DailyRemoteConfigPreference.getInstance(activity).keyRemoteConfigRewardStickerCardTitleMessage,
                            getString(R.string.label_reward_login), campaignFreeNights, descriptionText)
                            .also { campaignEnabled.runTrue { viewInterface.startRewardStickerAnimation() } }
                }
            } else {
                viewInterface.setRewardVisible(false)
            }
        } ?: Util.restartApp(activity)
    }

    private fun notifyWishDataSetChanged() {
        stayDetail?.let { notifyWishDataSetChanged(it.wishCount, it.wish) }
                ?: Util.restartApp(activity)
    }

    private fun notifyWishDataSetChanged(wishCount: Int, myWish: Boolean) {
        stayDetail?.let {
            viewInterface.setWishCount(wishCount)
            viewInterface.setWishSelected(myWish)
        } ?: Util.restartApp(activity)
    }

    private fun checkedRoomFilter(): Boolean {
        return !bedTypeFilter.isEmpty() || !facilitiesFilter.isEmpty()
    }

    private fun setRoomFilter(bookDateTime: StayBookDateTime, roomList: List<Room>?, bedTypeFilter: LinkedHashSet<String>, facilitiesFilter: LinkedHashSet<String>) {
        val calendarText = String.format(Locale.KOREA, "%s-%s돋%d박",
                bookDateTime.getCheckInDateTime("M.d(EEE)"),
                bookDateTime.getCheckOutDateTime("M.d(EEE)"),
                bookDateTime.nights)

        val startIndex = calendarText.indexOf('돋')
        val spannableString = SpannableString(calendarText)
        spannableString.setSpan(DailyImageSpan(activity, R.drawable.layerlist_over_bffffff_s2_p2, DailyImageSpan.ALIGN_VERTICAL_CENTER), startIndex, startIndex + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        if (roomList == null || roomList.isEmpty()) {
            viewInterface.setRoomFilterInformation(spannableString, 0)
        } else {
            val filteredRoomList = getFilteredRoomList(roomList, bedTypeFilter, facilitiesFilter)

            viewInterface.setRoomFilterInformation(spannableString, bedTypeFilter.size + facilitiesFilter.size)
            viewInterface.setRoomList(filteredRoomList)
        }
    }

    private fun applyMoreRoomAction() {
        if (viewInterface.isShowMoreRooms()) {
            viewInterface.setRoomActionButtonVisible(true)

            if (checkedRoomFilter()) {
                viewInterface.setRoomActionButtonText(getString(R.string.label_stay_detail_reset_room_filter),
                        R.drawable.ic_refresh,
                        0,
                        ScreenUtils.dpToPx(activity, 8.0),
                        R.color.default_text_c4d4d4d,
                        R.drawable.shape_fillrect_le8e8e9_bfafafb_r3)
            } else {
                viewInterface.setRoomActionButtonText(getString(R.string.label_collapse), 0, R.drawable.vector_roomlist_ic_sub_v, ScreenUtils.dpToPx(activity, 4.0))
            }
        } else {
            val showViewRoomMaxCount = 5
            val filteredRoomCount = getRoomFilterCount(stayDetail?.roomInformation?.roomList, bedTypeFilter, facilitiesFilter)

            when {
                filteredRoomCount == 0 -> viewInterface.setRoomActionButtonVisible(false)

                filteredRoomCount > showViewRoomMaxCount -> {
                    viewInterface.setRoomActionButtonVisible(true)
                    viewInterface.setRoomActionButtonText(getString(R.string.label_stay_detail_show_more_rooms, filteredRoomCount - showViewRoomMaxCount))
                }

                else -> {
                    if (checkedRoomFilter()) {
                        viewInterface.setRoomActionButtonVisible(true)
                        viewInterface.setRoomActionButtonText(getString(R.string.label_stay_detail_reset_room_filter),
                                R.drawable.ic_refresh,
                                0,
                                ScreenUtils.dpToPx(activity, 8.0),
                                R.color.default_text_c4d4d4d,
                                R.drawable.shape_fillrect_le8e8e9_bfafafb_r3)
                    } else {
                        viewInterface.setRoomActionButtonVisible(false)
                    }
                }
            }
        }
    }

    private fun getFilteredRoomList(roomList: List<Room>?, bedTypeFilter: LinkedHashSet<String>, facilitiesFilter: LinkedHashSet<String>): List<Room> {
        val filteredRoomSet = LinkedHashSet<Room>()

        roomList.takeNotEmpty {
            if (bedTypeFilter.isEmpty() && facilitiesFilter.isEmpty()) {
                filteredRoomSet.addAll(it)
            } else {
                if (bedTypeFilter.size == 0) {
                    it.forEach loop@{ room ->
                        facilitiesFilter.forEach {
                            if (!room.amenityList.map { it.toUpperCase() }.contains(it.toUpperCase())) {
                                return@loop
                            }
                        }

                        filteredRoomSet.add(room)
                    }
                } else {
                    it.forEach loop@{ room ->
                        bedTypeFilter.forEach {
                            if (room.bedInformation.filterList.map { it.toUpperCase() }.contains(it.toUpperCase())) {
                                facilitiesFilter.forEach {
                                    if (!room.amenityList.map { it.toUpperCase() }.contains(it.toUpperCase())) {
                                        return@loop
                                    }
                                }

                                filteredRoomSet.add(room)
                            }
                        }
                    }
                }
            }
        }

        return filteredRoomSet.toList()
    }

    private fun getRoomFilterCount(roomList: List<Room>?, bedTypeFilter: LinkedHashSet<String>, facilitiesFilter: LinkedHashSet<String>): Int {
        return getFilteredRoomList(roomList, bedTypeFilter, facilitiesFilter).size
    }
}