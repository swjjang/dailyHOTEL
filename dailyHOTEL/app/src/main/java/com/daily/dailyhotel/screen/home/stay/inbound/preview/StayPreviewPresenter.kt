package com.daily.dailyhotel.screen.home.stay.inbound.preview

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.view.View
import com.daily.base.BaseActivity
import com.daily.base.util.DailyTextUtils
import com.daily.base.util.FontManager
import com.daily.dailyhotel.base.BaseExceptionPresenter
import com.daily.dailyhotel.entity.ReviewScores
import com.daily.dailyhotel.entity.StayBookDateTime
import com.daily.dailyhotel.entity.StayDetail
import com.daily.dailyhotel.entity.StayRoom
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl
import com.daily.dailyhotel.repository.remote.StayRemoteImpl
import com.daily.dailyhotel.screen.common.dialog.wish.WishDialogActivity
import com.daily.dailyhotel.storage.preference.DailyUserPreference
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.util.Constants
import com.twoheart.dailyhotel.util.KakaoLinkManager
import com.twoheart.dailyhotel.util.Util
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import java.util.*

class StayPreviewPresenter(activity: StayPreviewActivity)
    : BaseExceptionPresenter<StayPreviewActivity, StayPreviewInterface.ViewInterface>(activity), StayPreviewInterface.OnEventListener {

    private val stayRemoteImpl: StayRemoteImpl by lazy {
        StayRemoteImpl()
    }

    private val commonRemoteImpl: CommonRemoteImpl by lazy {
        CommonRemoteImpl()
    }

    private lateinit var bookDateTime: StayBookDateTime
    private var stayIndex: Int = 0
    private lateinit var stayName: String
    private lateinit var stayGrade: String
    private var viewPrice: Int = StayPreviewActivity.SKIP_CHECK_PRICE_VALUE

    private lateinit var detail: StayDetail
    private var trueReviewCount: Int = 0

    private val analytics: StayPreviewInterface.AnalyticsInterface by lazy {
        StayPreviewAnalyticsImpl()
    }

    override fun createInstanceViewInterface(): StayPreviewInterface.ViewInterface {
        return StayPreviewView(activity, this)
    }

    override fun constructorInitialize(activity: StayPreviewActivity) {
        setContentView(R.layout.activity_stay_preview_data)

        isRefresh = true
    }

    override fun onIntent(intent: Intent?): Boolean {
        return intent?.let {
            val checkInDateTime = it.getStringExtra(StayPreviewActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME)
            val checkOutDateTime = it.getStringExtra(StayPreviewActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME)

            try {
                bookDateTime = StayBookDateTime(checkInDateTime, checkOutDateTime)
            } catch (e: Exception) {
                return false
            }

            stayIndex = it.getIntExtra(StayPreviewActivity.INTENT_EXTRA_DATA_STAY_INDEX, 0)

            if (stayIndex <= 0) {
                return false
            }

            stayName = it.getStringExtra(StayPreviewActivity.INTENT_EXTRA_DATA_STAY_NAME)
            stayGrade = it.getStringExtra(StayPreviewActivity.INTENT_EXTRA_DATA_STAY_GRADE)
            viewPrice = it.getIntExtra(StayPreviewActivity.INTENT_EXTRA_DATA_STAY_VIEW_PRICE, StayPreviewActivity.SKIP_CHECK_PRICE_VALUE)

            true
        } ?: true
    }

    override fun onNewIntent(intent: Intent?) {
    }

    override fun onPostCreate() {
        notifyDataSetChanged()

        addCompositeDisposable(viewInterface.showAnimation().subscribe())
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
        if (lock()) {
            return true
        }

        hideAnimationAfterFinish()

        analytics.onEventBackClick(activity)

        return true
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
            StayPreviewActivity.REQUEST_CODE_WISH_DIALOG -> onWishDialogActivityResult(resultCode, intent)
        }
    }

    private fun onWishDialogActivityResult(resultCode: Int, intent: Intent?) {
        when (resultCode) {
            Activity.RESULT_OK -> {

                intent?.let {
                    val wish = it.getBooleanExtra(WishDialogActivity.INTENT_EXTRA_DATA_WISH, false)

                    setResult(BaseActivity.RESULT_CODE_REFRESH, Intent().putExtra(StayPreviewActivity.INTENT_EXTRA_DATA_WISH, wish))
                }

                hideAnimationAfterFinish()
            }

            else -> viewInterface.setWish(detail.myWish)
        }
    }

    private fun hideAnimationAfterFinish(subFunction: (() -> Unit)? = null) {
        addCompositeDisposable(viewInterface.hideAnimation().observeOn(AndroidSchedulers.mainThread()).subscribe {
            subFunction?.invoke()
            finish()
        })
    }

    @Synchronized
    override fun onRefresh(showProgress: Boolean) {
        if (isFinish || !isRefresh) {
            return
        }

        isRefresh = false
        screenLock(showProgress)

        addCompositeDisposable(Observable.zip(stayRemoteImpl.getDetail(stayIndex, bookDateTime), stayRemoteImpl.getReviewScores(stayIndex)
                , BiFunction<StayDetail, ReviewScores, Int> { stayDetail, reviewScores ->
            this@StayPreviewPresenter.detail = stayDetail

            analytics.onScreen(activity, stayDetail.category)

            reviewScores.reviewScoreTotalCount
        }).observeOn(AndroidSchedulers.mainThread()).subscribe({ trueReviewCount ->
            this@StayPreviewPresenter.trueReviewCount = trueReviewCount

            notifyDataSetChanged()

            unLockAll()
        }, { throwable ->
            onHandleError(throwable)
            hideAnimationAfterFinish()
        }))
    }

    override fun onBackClick() {
        activity.onBackPressed()
    }

    override fun onDetailClick() {
        setResult(Activity.RESULT_OK)
        onBackClick()

        analytics.onEventDetailClick(activity)
    }

    override fun onWishClick() {
        if (!::detail.isInitialized || lock()) {
            return
        }

        val changeWish = !detail.myWish

        viewInterface.setWish(changeWish)

        analytics.onEventWishClick(activity, changeWish)

        startActivityForResult(WishDialogActivity.newInstance(activity, Constants.ServiceType.HOTEL//
                , stayIndex, changeWish, AnalyticsManager.Screen.PEEK_POP), StayPreviewActivity.REQUEST_CODE_WISH_DIALOG)
    }

    override fun onKakaoClick() {
        if (!::detail.isInitialized || lock()) {
            return
        }

        analytics.onEventKakaoClick(activity)

        try {
            activity.packageManager.getPackageInfo("com.kakao.talk", PackageManager.GET_META_DATA)
        } catch (e: PackageManager.NameNotFoundException) {
            viewInterface.showSimpleDialog(null, getString(R.string.dialog_msg_not_installed_kakaotalk)//
                    , getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no)//
                    , View.OnClickListener { Util.installPackage(activity, "com.kakao.talk") }
                    , null, null, DialogInterface.OnDismissListener { onBackClick() }, true)

            unLockAll()
            return
        }

        screenLock(true)

        val name = DailyUserPreference.getInstance(activity).name
        val urlFormat = "https://mobile.dailyhotel.co.kr/stay/%d?dateCheckIn=%s&stays=%d&utm_source=share&utm_medium=stay_detail_kakaotalk"
        val longUrl = String.format(Locale.KOREA, urlFormat, stayIndex
                , bookDateTime.getCheckInDateTime("yyyy-MM-dd")
                , bookDateTime.nights)

        // flatMapCompletable 을 사용하고 싶었는데 shortUrl 을 넘겨주어야 하는게 쉽지 않다.
        addCompositeDisposable(commonRemoteImpl.getShortUrl(longUrl).observeOn(AndroidSchedulers.mainThread()).subscribe({ shortUrl ->
            hideAnimationAfterFinish { startKakaoLinkApplication(name, detail, bookDateTime, shortUrl) }
        }, {
            val mobileWebUrl = "https://mobile.dailyhotel.co.kr/stay/" + detail.index

            hideAnimationAfterFinish { startKakaoLinkApplication(name, detail, bookDateTime, mobileWebUrl) }
        }))
    }

    private fun startKakaoLinkApplication(userName: String, detail: StayDetail, bookDateTime: StayBookDateTime, url: String) {
        KakaoLinkManager.newInstance(activity).shareStay(userName
                , detail.name, detail.address, detail.index
                , detail.imageInformationList?.get(0)?.imageMap?.mediumUrl
                , url, bookDateTime)
    }

    override fun onMapClick() {
        if (!::detail.isInitialized || lock()) {
            return
        }

        hideAnimationAfterFinish { Util.shareNaverMap(activity, detail.name, detail.latitude.toString(), detail.longitude.toString()) }

        analytics.onEventMapClick(activity)
    }

    override fun onCloseClick() {
        if (lock()) {
            return
        }

        hideAnimationAfterFinish()

        analytics.onEventCloseClick(activity)
    }

    internal fun notifyDataSetChanged() {
        viewInterface.setName(stayName)

        if (::detail.isInitialized) {
            val soldOut = !detail.hasRooms()

            viewInterface.setCategory(detail.grade.getName(activity), detail.activeReward)
            viewInterface.setImages(detail.imageInformationList.map { it.imageMap.smallUrl }.toTypedArray())

            notifyRoomInformationDataSetChanged(soldOut, detail.roomList)
            notifyReviewInformationDataSetChanged(trueReviewCount, detail.wishCount)

            viewInterface.setWish(detail.myWish)
            viewInterface.setBookingButtonText(if (soldOut) getString(R.string.label_booking_view_detail) else getString(R.string.label_preview_booking))
        } else {
            viewInterface.setCategory(stayGrade, false)
        }
    }

    private fun notifyRoomInformationDataSetChanged(soldOut: Boolean, roomList: List<StayRoom>?) {
        val roomTypeCountText: String = if (soldOut) getString(R.string.message_preview_changed_price)
        else getRoomTypeCountText(roomList?.size ?: 0)

        viewInterface.setRoomInformation(roomTypeCountText, !soldOut && bookDateTime.nights > 1, !soldOut, getRangePriceText(roomList))
    }

    private fun notifyReviewInformationDataSetChanged(trueReviewCount: Int, wishCount: Int) {
        when {
            trueReviewCount > 0 && wishCount > 0 -> {
                viewInterface.setReviewInformationVisible(true)
                viewInterface.setReviewInformation(true, getTrueReviewCountText(trueReviewCount), true, getWishCountText(wishCount))
            }

            trueReviewCount > 0 -> {
                viewInterface.setReviewInformationVisible(true)
                viewInterface.setReviewInformation(true, getTrueReviewCountText(trueReviewCount), false, null)
            }

            wishCount > 0 -> {
                viewInterface.setReviewInformationVisible(true)
                viewInterface.setReviewInformation(false, null, true, getWishCountText(wishCount))
            }

            else -> viewInterface.setReviewInformationVisible(false)
        }
    }

    private fun getRoomTypeCountText(count: Int): String {
        return if (count == 0) getString(R.string.message_preview_changed_price) else getString(R.string.label_detail_stay_product_count, count)
    }

    private fun getRangePriceText(roomList: List<StayRoom>?): String? {
        return roomList?.let {
            var minPrice = Int.MAX_VALUE
            var maxPrice = Int.MIN_VALUE

            it.forEach {
                minPrice = Math.min(minPrice, it.discountAverage)
                maxPrice = Math.max(maxPrice, it.discountAverage)
            }

            if (minPrice == Int.MAX_VALUE || minPrice <= 0 || maxPrice == Int.MIN_VALUE || maxPrice == 0) {
                return null
            }

            if (minPrice == maxPrice) {
                DailyTextUtils.getPriceFormat(activity, maxPrice, false)
            } else {
                DailyTextUtils.getPriceFormat(activity, minPrice, false) + " ~ " + DailyTextUtils.getPriceFormat(activity, maxPrice, false)
            }
        }
    }

    internal fun getTrueReviewCountText(count: Int): SpannableStringBuilder {
        return getCountTextSpannableStringBuilder(getString(R.string.label_detail_truereview_count, DailyTextUtils.formatIntegerToString(count)))
    }

    internal fun getWishCountText(count: Int): SpannableStringBuilder {
        return getCountTextSpannableStringBuilder(getString(R.string.label_detail_wish_count, DailyTextUtils.formatIntegerToString(count)))
    }

    private fun getCountTextSpannableStringBuilder(countText: String): SpannableStringBuilder {
        return SpannableStringBuilder(countText).apply {
            setSpan(CustomFontTypefaceSpan(FontManager.getInstance(activity).demiLightTypeface),
                    countText.indexOf(" "), countText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
}