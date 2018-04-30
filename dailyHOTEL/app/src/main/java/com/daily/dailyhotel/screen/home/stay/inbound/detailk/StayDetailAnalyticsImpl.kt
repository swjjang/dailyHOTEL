package com.daily.dailyhotel.screen.home.stay.inbound.detailk

import android.app.Activity
import com.daily.dailyhotel.entity.StayBookDateTime
import com.daily.dailyhotel.entity.StayDetailk
import com.daily.dailyhotel.entity.StayRoom
import com.daily.dailyhotel.parcel.analytics.StayDetailAnalyticsParam
import com.daily.dailyhotel.parcel.analytics.StayPaymentAnalyticsParam

class StayDetailAnalyticsImpl : StayDetailInterface.AnalyticsInterface {

    private var analyticsParam: StayDetailAnalyticsParam? = null

    override fun setAnalyticsParam(analyticsParam: StayDetailAnalyticsParam) {
        this.analyticsParam = analyticsParam
    }

    override fun getStayPaymentAnalyticsParam(stayDetail: StayDetailk, stayRoom: StayRoom): StayPaymentAnalyticsParam {

        return StayPaymentAnalyticsParam()
    }

    override fun onScreen(activity: Activity, stayBookDateTime: StayBookDateTime, stayDetail: StayDetailk?, priceFromList: Int) {
    }

    override fun onScreenRoomList(activity: Activity, stayBookDateTime: StayBookDateTime, stayDetail: StayDetailk, priceFromList: Int) {
    }

    override fun onEventRoomListOpenClick(activity: Activity, stayName: String) {
    }

    override fun onEventRoomListCloseClick(activity: Activity, stayName: String) {
    }

    override fun onEventRoomClick(activity: Activity, roomName: String) {
    }

    override fun onEventShareKakaoClick(activity: Activity, login: Boolean, userType: String, benefitAlarm: Boolean, stayIndex: Int, stayName: String?) {
    }

    override fun onEventLinkCopyClick(activity: Activity) {
    }

    override fun onEventMoreShareClick(activity: Activity) {
    }

    override fun onEventDownloadCoupon(activity: Activity, stayName: String?) {
    }

    override fun onEventDownloadCouponByLogin(activity: Activity, login: Boolean) {
    }

    override fun onEventShare(activity: Activity) {
    }

    override fun onEventChangedPrice(activity: Activity, deepLink: Boolean, stayName: String, soldOut: Boolean) {
    }

    override fun onEventCalendarClick(activity: Activity) {
    }

    override fun onEventBookingClick(activity: Activity, stayBookDateTime: StayBookDateTime, stayIndex: Int,
                                     stayName: String, roomName: String, discountPrice: Int, category: String,
                                     provideRewardSticker: Boolean, isOverseas: Boolean) {
    }

    override fun onEventTrueReviewClick(activity: Activity) {
    }

    override fun onEventTrueVRClick(activity: Activity, stayIndex: Int) {
    }

    override fun onEventImageClick(activity: Activity, stayName: String?) {
    }

    override fun onEventConciergeClick(activity: Activity) {
    }

    override fun onEventMapClick(activity: Activity, stayName: String?) {
    }

    override fun onEventClipAddressClick(activity: Activity, stayName: String?) {
    }

    override fun onEventWishClick(activity: Activity, stayBookDateTime: StayBookDateTime, stayDetail: StayDetailk, priceFromList: Int, myWish: Boolean) {
    }

    override fun onEventCallClick(activity: Activity) {
    }

    override fun onEventFaqClick(activity: Activity) {
    }

    override fun onEventHappyTalkClick(activity: Activity) {
    }

    override fun onEventShowTrueReview(activity: Activity, stayIndex: Int) {
    }

    override fun onEventShowCoupon(activity: Activity, stayIndex: Int) {
    }

    override fun onEventTrueAwards(activity: Activity, stayIndex: Int) {
    }

    override fun onEventTrueAwardsClick(activity: Activity, stayIndex: Int) {
    }

}