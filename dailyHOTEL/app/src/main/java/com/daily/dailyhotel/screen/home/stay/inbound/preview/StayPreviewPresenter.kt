package com.daily.dailyhotel.screen.home.stay.inbound.preview

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import com.daily.base.util.DailyTextUtils
import com.daily.base.util.FontManager
import com.daily.dailyhotel.base.BaseExceptionPresenter
import com.daily.dailyhotel.entity.*
import com.daily.dailyhotel.repository.remote.StayRemoteImpl
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Consumer

class StayPreviewPresenter(activity: StayPreviewActivity)
    : BaseExceptionPresenter<StayPreviewActivity, StayPreviewInterface.ViewInterface>(activity), StayPreviewInterface.OnEventListener {

    private lateinit var stayRemoteImpl: StayRemoteImpl

    private lateinit var stayBookDateTime: StayBookDateTime
    private var stayIndex: Int = -1
    private lateinit var stayName: String
    private lateinit var stayGrade: Stay.Grade
    private var viewPrice: Int = StayPreviewActivity.SKIP_CHECK_PRICE_VALUE

    private lateinit var stayDetail: StayDetail
    private var trueReviewCount: Int = 0

    private val analytics: StayPreviewInterface.AnalyticsInterface by lazy {
        StayPreviewAnalyticsImpl()
    }

    override fun createInstanceViewInterface(): StayPreviewInterface.ViewInterface {
        return StayPreviewView(activity, this)
    }

    override fun constructorInitialize(activity: StayPreviewActivity) {
        setContentView(R.layout.activity_stay_preview_data)

        stayRemoteImpl = StayRemoteImpl(activity)

        isRefresh = true
    }

    override fun onIntent(intent: Intent?): Boolean {
        if (intent == null) {
            return true
        }

        var checkInDateTime = intent.getStringExtra(StayPreviewActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME)
        var checkOutDateTime = intent.getStringExtra(StayPreviewActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME)

        try {
            stayBookDateTime = StayBookDateTime(checkInDateTime, checkOutDateTime)
        } catch (e: Exception) {
            return false
        }

        stayIndex = intent.getIntExtra(StayPreviewActivity.INTENT_EXTRA_DATA_STAY_INDEX, 0)

        if (stayIndex <= 0) {
            return false
        }

        stayName = intent.getStringExtra(StayPreviewActivity.INTENT_EXTRA_DATA_STAY_NAME)
        var grade = intent.getStringExtra(StayPreviewActivity.INTENT_EXTRA_DATA_STAY_GRADE)

        try {
            stayGrade = Stay.Grade.valueOf(grade)
        } catch (e: Exception) {
            stayGrade = Stay.Grade.etc
        }

        viewPrice = intent.getIntExtra(StayPreviewActivity.INTENT_EXTRA_DATA_STAY_VIEW_PRICE, StayPreviewActivity.SKIP_CHECK_PRICE_VALUE)

        return true
    }

    override fun onNewIntent(intent: Intent?) {
    }

    override fun onPostCreate() {
        notifyDataSetChanged()
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

        addCompositeDisposable(viewInterface.hidePreviewAnimation().observeOn(AndroidSchedulers.mainThread()).subscribe { finish() })

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
    }

    @Synchronized
    override fun onRefresh(showProgress: Boolean) {
        if (isFinish || !isRefresh) {
            return
        }

        isRefresh = false
        screenLock(showProgress)

        addCompositeDisposable(Observable.zip(stayRemoteImpl.getDetail(stayIndex, stayBookDateTime), stayRemoteImpl.getReviewScores(stayIndex)
                , BiFunction<StayDetail, ReviewScores, Int> { stayDetail, reviewScores ->
            this@StayPreviewPresenter.stayDetail = stayDetail
            reviewScores.reviewScoreTotalCount
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(Consumer<Int> { trueReviewCount ->
            this@StayPreviewPresenter.trueReviewCount = trueReviewCount

            notifyDataSetChanged()

            unLockAll()
        }, Consumer<Throwable> { throwable -> onHandleError(throwable) }))
    }

    override fun onBackClick() {
        activity.onBackPressed()
    }

    override fun onDetailClick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onWishClick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onKakaoClick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMapClick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    internal fun notifyDataSetChanged() {
        viewInterface.setName(stayName)

        if (::stayDetail.isInitialized) {
            val soldOut = stayDetail.roomList == null || stayDetail.roomList.isEmpty()

            viewInterface.setCategory(stayDetail.grade.getName(activity), stayDetail.activeReward)
            viewInterface.setImages(stayDetail.imageInformationList)

            notifyRoomInformationDataSetChanged(soldOut, stayDetail.roomList)
            notifyReviewInformationDataSetChanged(trueReviewCount, stayDetail.wishCount)

            viewInterface.setBookingButtonText(if (soldOut) getString(R.string.label_booking_view_detail) else getString(R.string.label_preview_booking))
        } else {
            viewInterface.setCategory(stayGrade?.getName(activity), false)
        }
    }

    private fun notifyRoomInformationDataSetChanged(soldOut: Boolean, roomList: List<StayRoom>?) {
        val roomTypeCountText: String = if (soldOut) getString(R.string.message_preview_changed_price) else getRoomTypeCountText(roomList)
        val rangePriceText: String? = getRangePriceText(roomList)

        viewInterface.setRoomInformation(roomTypeCountText, !soldOut && stayBookDateTime.nights > 1, !soldOut, rangePriceText)
    }

    private fun notifyReviewInformationDataSetChanged(trueReviewCount: Int, wishCount: Int) {
        when {
            trueReviewCount > 0 && wishCount > 0 -> {
                viewInterface.setReviewInformationVisible(true)

                val trueReviewCountText = getTrueReviewCountText(trueReviewCount)
                val wishCountText = getWishCountText(wishCount)

                viewInterface.setReviewInformation(true, trueReviewCountText, true, wishCountText)
            }

            trueReviewCount > 0 -> {
                viewInterface.setReviewInformationVisible(true)

                val trueReviewCountText = getTrueReviewCountText(trueReviewCount)

                viewInterface.setReviewInformation(true, trueReviewCountText, false, null)
            }

            wishCount > 0 -> {
                viewInterface.setReviewInformationVisible(true)

                val wishCountText = getWishCountText(wishCount)

                viewInterface.setReviewInformation(false, null, true, wishCountText)
            }

            else -> viewInterface.setReviewInformationVisible(false)
        }
    }

    private fun getRoomTypeCountText(roomList: List<StayRoom>?): String {
        return if (roomList == null || roomList.isEmpty()) {
            getString(R.string.message_preview_changed_price)
        } else {
            getString(R.string.label_detail_stay_product_count, roomList.size)
        }
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

    private fun getTrueReviewCountText(count: Int): SpannableStringBuilder {
        val trueReviewCountText = getString(R.string.label_detail_truereview_count, DailyTextUtils.formatIntegerToString(count))
        val spannableStringBuilder = SpannableStringBuilder(trueReviewCountText)

        spannableStringBuilder.setSpan(CustomFontTypefaceSpan(FontManager.getInstance(activity).demiLightTypeface),
                trueReviewCountText.indexOf(" "), trueReviewCountText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        return spannableStringBuilder
    }

    private fun getWishCountText(count: Int): SpannableStringBuilder {
        val wishCountText = getString(R.string.label_detail_wish_count, DailyTextUtils.formatIntegerToString(count))
        val spannableStringBuilder = SpannableStringBuilder(wishCountText)

        spannableStringBuilder.setSpan( //
                CustomFontTypefaceSpan(FontManager.getInstance(activity).demiLightTypeface),
                wishCountText.indexOf(" "), wishCountText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        return spannableStringBuilder
    }
}