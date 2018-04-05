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

    private lateinit var stayRemoteImpl: StayRemoteImpl
    private lateinit var commonRemoteImpl: CommonRemoteImpl

    private lateinit var stayBookDateTime: StayBookDateTime
    private var stayIndex: Int = -1
    private lateinit var stayName: String
    private lateinit var stayGrade: String
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
        commonRemoteImpl = CommonRemoteImpl(activity)

        isRefresh = true
    }

    override fun onIntent(intent: Intent?): Boolean {
        if (intent == null) {
            return true
        }

        val checkInDateTime = intent.getStringExtra(StayPreviewActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME)
        val checkOutDateTime = intent.getStringExtra(StayPreviewActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME)

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
        stayGrade = intent.getStringExtra(StayPreviewActivity.INTENT_EXTRA_DATA_STAY_GRADE)

        viewPrice = intent.getIntExtra(StayPreviewActivity.INTENT_EXTRA_DATA_STAY_VIEW_PRICE, StayPreviewActivity.SKIP_CHECK_PRICE_VALUE)

        return true
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

        addCompositeDisposable(viewInterface.hideAnimation().observeOn(AndroidSchedulers.mainThread()).subscribe { finish() })

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

                addCompositeDisposable(viewInterface.hideAnimation().observeOn(AndroidSchedulers.mainThread()).subscribe { finish() })
            }

            else -> viewInterface.setWish(stayDetail.myWish)
        }
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

            analytics.onScreen(activity, stayDetail.category)

            reviewScores.reviewScoreTotalCount
        }).observeOn(AndroidSchedulers.mainThread()).subscribe({ trueReviewCount ->
            this@StayPreviewPresenter.trueReviewCount = trueReviewCount

            notifyDataSetChanged()

            unLockAll()
        }, { throwable -> onHandleError(throwable) }))
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
        if (!::stayDetail.isInitialized || lock()) {
            return
        }

        val changeWish = !stayDetail.myWish

        viewInterface.setWish(changeWish)

        analytics.onEventWishClick(activity, changeWish)

        startActivityForResult(WishDialogActivity.newInstance(getActivity(), Constants.ServiceType.HOTEL//
                , stayIndex, changeWish, AnalyticsManager.Screen.PEEK_POP), StayPreviewActivity.REQUEST_CODE_WISH_DIALOG)
    }

    override fun onKakaoClick() {
        if (!::stayDetail.isInitialized || lock()) {
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

        val DATE_FORMAT = "yyyy-MM-dd"
        val name = DailyUserPreference.getInstance(activity).name
        val urlFormat = "https://mobile.dailyhotel.co.kr/stay/%d?dateCheckIn=%s&stays=%d&utm_source=share&utm_medium=stay_detail_kakaotalk"
        val longUrl = String.format(Locale.KOREA, urlFormat, stayIndex
                , stayBookDateTime.getCheckInDateTime(DATE_FORMAT)
                , stayBookDateTime.nights)

        addCompositeDisposable(commonRemoteImpl.getShortUrl(longUrl).observeOn(AndroidSchedulers.mainThread()).subscribe({ shortUrl ->
            addCompositeDisposable(viewInterface.hideAnimation().observeOn(AndroidSchedulers.mainThread()).subscribe {
                KakaoLinkManager.newInstance(activity).shareStay(name
                        , stayDetail.name
                        , stayDetail.address
                        , stayDetail.index
                        , if (stayDetail.imageInformationList == null || stayDetail.imageInformationList.size == 0) null else stayDetail.imageInformationList.get(0).imageMap.mediumUrl
                        , shortUrl
                        , stayBookDateTime)

                finish()
            })

        }, {
            addCompositeDisposable(viewInterface.hideAnimation().observeOn(AndroidSchedulers.mainThread()).subscribe {
                KakaoLinkManager.newInstance(activity).shareStay(name
                        , stayDetail.name
                        , stayDetail.address
                        , stayDetail.index
                        , if (stayDetail.imageInformationList == null || stayDetail.imageInformationList.size == 0) null else stayDetail.imageInformationList.get(0).imageMap.mediumUrl
                        , "https://mobile.dailyhotel.co.kr/stay/" + stayDetail.index
                        , stayBookDateTime)

                finish()
            })
        }))
    }

    override fun onMapClick() {
        if (!::stayDetail.isInitialized || lock()) {
            return
        }

        addCompositeDisposable(viewInterface.hideAnimation().observeOn(AndroidSchedulers.mainThread()).subscribe {
            Util.shareNaverMap(activity, stayDetail.name//
                    , stayDetail.latitude.toString(), stayDetail.longitude.toString())

            finish()
        })

        analytics.onEventMapClick(activity)
    }

    override fun onCloseClick() {
        if (lock()) {
            return
        }

        addCompositeDisposable(viewInterface.hideAnimation().observeOn(AndroidSchedulers.mainThread()).subscribe { finish() })

        analytics.onEventCloseClick(activity)
    }


    internal fun notifyDataSetChanged() {
        viewInterface.setName(stayName)

        if (::stayDetail.isInitialized) {
            val soldOut = stayDetail.roomList == null || stayDetail.roomList.isEmpty()

            viewInterface.setCategory(stayDetail.grade.getName(activity), stayDetail.activeReward)
            viewInterface.setImages(stayDetail.imageInformationList)

            notifyRoomInformationDataSetChanged(soldOut, stayDetail.roomList)
            notifyReviewInformationDataSetChanged(trueReviewCount, stayDetail.wishCount)

            viewInterface.setWish(stayDetail.myWish)
            viewInterface.setBookingButtonText(if (soldOut) getString(R.string.label_booking_view_detail) else getString(R.string.label_preview_booking))
        } else {
            viewInterface.setCategory(stayGrade, false)
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