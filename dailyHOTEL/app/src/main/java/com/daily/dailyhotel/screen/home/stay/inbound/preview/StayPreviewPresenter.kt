package com.daily.dailyhotel.screen.home.stay.inbound.preview

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import com.daily.base.BaseActivity
import com.daily.base.util.DailyTextUtils
import com.daily.base.util.FontManager
import com.daily.dailyhotel.base.BaseExceptionPresenter
import com.daily.dailyhotel.entity.ReviewScores
import com.daily.dailyhotel.entity.Room
import com.daily.dailyhotel.entity.StayBookDateTime
import com.daily.dailyhotel.entity.StayDetail
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl
import com.daily.dailyhotel.repository.remote.StayRemoteImpl
import com.daily.dailyhotel.screen.common.dialog.wish.WishDialogActivity
import com.daily.dailyhotel.storage.preference.DailyUserPreference
import com.daily.dailyhotel.util.isNotNullAndNotEmpty
import com.daily.dailyhotel.util.runTrue
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

    private val analytics = StayPreviewAnalyticsImpl()
    private val stayRemoteImpl = StayRemoteImpl()
    private val commonRemoteImpl by lazy {
        CommonRemoteImpl()
    }

    private val bookDateTime = StayBookDateTime()
    private var stayIndex: Int = 0
    private lateinit var stayName: String
    private lateinit var stayGrade: String
    private var viewPrice: Int = StayPreviewActivity.SKIP_CHECK_PRICE_VALUE

    private lateinit var detail: StayDetail
    private var trueReviewCount: Int = 0

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
                bookDateTime.setBookDateTime(checkInDateTime, checkOutDateTime)
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

        isRefresh.runTrue { onRefresh(true) }
    }

    override fun onResume() {
        super.onResume()

        isRefresh.runTrue { onRefresh(true) }
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

            else -> viewInterface.setWish(detail.wish)
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
        takeIf { !isFinish && isRefresh }.let {
            isRefresh = false
            screenLock(showProgress)

            addCompositeDisposable(Observable.zip(stayRemoteImpl.getDetail(stayIndex, bookDateTime), stayRemoteImpl.getReviewScores(stayIndex)
                    , BiFunction<StayDetail, ReviewScores, Int> { stayDetail, reviewScores ->
                this@StayPreviewPresenter.detail = stayDetail

                analytics.onScreen(activity, stayDetail.baseInformation?.category)

                reviewScores.reviewScoreTotalCount
            }).observeOn(AndroidSchedulers.mainThread()).subscribe({ trueReviewCount ->
                this@StayPreviewPresenter.trueReviewCount = trueReviewCount

                notifyDataSetChanged()

                unLockAll()
            }, { throwable ->
                onHandleErrorAndFinish(throwable)
            }))
        }
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
        takeIf { it::detail.isInitialized && !lock() }.let {
            val changeWish = !detail.wish

            viewInterface.setWish(changeWish)

            analytics.onEventWishClick(activity, changeWish)

            startActivityForResult(WishDialogActivity.newInstance(activity, Constants.ServiceType.HOTEL//
                    , stayIndex, changeWish, AnalyticsManager.Screen.PEEK_POP), StayPreviewActivity.REQUEST_CODE_WISH_DIALOG)
        }
    }

    override fun onKakaoClick() {
        takeIf { it::detail.isInitialized && !lock() }.let {
            analytics.onEventKakaoClick(activity)

            screenLock(true)

            val name: String? = DailyUserPreference.getInstance(activity).name
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
    }

    private fun startKakaoLinkApplication(userName: String?, detail: StayDetail, bookDateTime: StayBookDateTime, url: String) {
        KakaoLinkManager.newInstance(activity).shareStay(userName
                , detail.baseInformation?.name, detail.addressInformation?.address, detail.index
                , detail.imageList?.get(0)?.imageMap?.smallUrl
                , url, bookDateTime)
    }

    override fun onMapClick() {
        takeIf { it::detail.isInitialized && !lock() }.let {
            hideAnimationAfterFinish {
                Util.shareNaverMap(activity,
                        detail.baseInformation?.name,
                        detail.addressInformation?.latitude.toString(),
                        detail.addressInformation?.longitude.toString())
            }

            analytics.onEventMapClick(activity)
        }
    }

    override fun onCloseClick() {
        takeIf { !lock() }.let {
            hideAnimationAfterFinish()

            analytics.onEventCloseClick(activity)
        }
    }

    internal fun notifyDataSetChanged() {
        viewInterface.setName(stayName)

        if (::detail.isInitialized) {
            val soldOut = isSoldOut(detail)

            viewInterface.setCategory(detail.baseInformation?.grade?.getName(activity), detail.activeReward)
            viewInterface.setImages(detail.imageList?.map { it.imageMap.smallUrl }?.toTypedArray())

            notifyRoomInformationDataSetChanged(soldOut, detail.roomInformation?.roomList)
            notifyReviewInformationDataSetChanged(trueReviewCount, detail.wishCount)

            viewInterface.setWish(detail.wish)
            viewInterface.setBookingButtonText(if (soldOut) getString(R.string.label_booking_view_detail) else getString(R.string.label_preview_booking))
        } else {
            viewInterface.setCategory(stayGrade, false)
        }
    }

    private fun isSoldOut(detail: StayDetail): Boolean {
        return !detail.roomInformation?.roomList.isNotNullAndNotEmpty()
    }

    private fun notifyRoomInformationDataSetChanged(soldOut: Boolean, roomList: List<Room>?) {
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

    private fun getRangePriceText(roomList: List<Room>?): String? {
        return roomList?.let {
            var minPrice = Int.MAX_VALUE
            var maxPrice = Int.MIN_VALUE

            it.forEach {
                minPrice = Math.min(minPrice, it.amountInformation.discountAverage)
                maxPrice = Math.max(maxPrice, it.amountInformation.discountAverage)
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