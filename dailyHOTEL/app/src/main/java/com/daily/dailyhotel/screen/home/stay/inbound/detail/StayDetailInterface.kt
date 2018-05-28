package com.daily.dailyhotel.screen.home.stay.inbound.detail

import android.app.Activity
import android.content.DialogInterface
import android.view.View
import android.widget.CompoundButton
import com.daily.base.BaseAnalyticsInterface
import com.daily.base.BaseDialogViewInterface
import com.daily.base.OnBaseEventListener
import com.daily.dailyhotel.entity.*
import com.daily.dailyhotel.parcel.analytics.StayDetailAnalyticsParam
import com.daily.dailyhotel.parcel.analytics.StayPaymentAnalyticsParam
import com.twoheart.dailyhotel.R
import io.reactivex.Completable
import io.reactivex.Observable

interface StayDetailInterface {
    interface ViewInterface : BaseDialogViewInterface {

        fun setInitializedLayout(name: String?, url: String?)

        fun setTransitionVisible(visible: Boolean)

        fun getSharedElementTransition(gradientType: StayDetailActivity.TransGradientType): Observable<Boolean>

        fun setSharedElementTransitionEnabled(enabled: Boolean, gradientType: StayDetailActivity.TransGradientType)


        fun showWishTooltip()

        fun hideWishTooltip()


        fun showTabLayout()

        fun hideTabLayout()


        fun setWishCount(count: Int)

        fun setWishSelected(selected: Boolean)

        fun setVRVisible(visible: Boolean)

        fun setMoreImageVisible(visible: Boolean)

        fun setImageList(imageList: List<DetailImageInformation>)

        fun setScrollViewVisible(visible: Boolean)

        fun setBaseInformation(baseInformation: StayDetail.BaseInformation, nightsEnabled: Boolean, soldOut: Boolean)

        fun setTrueReviewInformationVisible(visible: Boolean)

        fun setTrueReviewInformation(trueReviewInformation: StayDetail.TrueReviewInformation)

        fun setBenefitInformationVisible(visible: Boolean)

        fun setBenefitInformation(benefitInformation: StayDetail.BenefitInformation)

        fun setCouponButtonEnabled(enabled: Boolean)

        fun setCouponButtonText(text: String, iconVisible: Boolean = true)

        fun setEmptyRoomText(text: String?)

        fun setEmptyRoomVisible(visible: Boolean)

        fun setRoomFilterInformation(calendarText: CharSequence, roomFilterCount: Int)

        fun setPriceAverageTypeVisible(visible: Boolean)

        fun setPriceAverageType(isAverageType: Boolean)

        fun setRoomActionButtonVisible(visible: Boolean)

        fun setRoomActionButtonText(text: String,
                                    leftResourceId: Int = 0,
                                    rightResourceId: Int = 0,
                                    drawablePadding: Int = 0,
                                    textColorResourceId: Int = R.color.default_text_ceb2135,
                                    backgroundResourceId: Int = R.drawable.shape_fillrect_leb2135_bffffff_r3)

        fun setRoomList(roomList: List<Room>?)

        fun setDailyCommentVisible(visible: Boolean)

        fun setDailyComment(commentList: List<String>)

        fun setFacilities(roomCount: Int, facilities: List<FacilitiesPictogram>?)

        fun setAddressInformationVisible(visible: Boolean)

        fun setAddressInformation(addressInformation: StayDetail.AddressInformation)

        fun setCheckTimeInformationVisible(visible: Boolean)

        fun setCheckTimeInformation(checkTimeInformation: StayDetail.CheckTimeInformation)

        fun setDetailInformationVisible(visible: Boolean)

        fun setDetailInformation(detailInformation: StayDetail.DetailInformation?, breakfastInformation: StayDetail.BreakfastInformation?)

        fun setCancellationAndRefundPolicyVisible(visible: Boolean)

        fun setCancellationAndRefundPolicy(refundInformation: StayDetail.RefundInformation?, hasNRDRoom: Boolean = false)

        fun setCheckInformationVisible(visible: Boolean)

        fun setCheckInformation(checkTimeInformation: StayDetail.CheckInformation)

        fun setRewardVisible(visible: Boolean)

        fun setRewardMemberInformation(titleText: String, optionText: String?, nights: Int, descriptionText: String)

        fun setRewardNonMemberInformation(titleText: String, optionText: String?, campaignFreeNights: Int, descriptionText: String)

        fun startRewardStickerAnimation()

        fun stopRewardStickerAnimation()

        fun setConciergeInformation()


        fun scrollTop()

        fun showShareDialog()

        fun showConciergeDialog(listener: DialogInterface.OnDismissListener)

        fun showVRDialog(checkedChangeListener: CompoundButton.OnCheckedChangeListener,
                         positiveListener: View.OnClickListener,
                         onDismissListener: DialogInterface.OnDismissListener)

        fun showTrueAwardsDialog(trueAwards: TrueAwards?, onDismissListener: DialogInterface.OnDismissListener)


        fun setActionButtonText(text: String)

        fun setActionButtonEnabled(enabled: Boolean)

        fun scrollRoomInformation()

        fun scrollStayInformation()

        fun showMoreRooms(animated: Boolean): Completable

        fun hideMoreRooms()

        fun isShowMoreRooms(): Boolean

        fun setSelectedRoomFilter(selectedBedType: LinkedHashSet<String>, selectedFacilities: LinkedHashSet<String>)

        fun setSelectedRoomFilterCount(selectedRoomFilterCount: Int)

        fun showRoomFilter(): Completable

        fun hideRoomFilter(): Completable
    }

    interface OnEventListener : OnBaseEventListener {
        fun onShareClick()

        fun onWishClick()

        fun onShareKakaoClick()

        fun onCopyLinkClick()

        fun onMoreShareClick()

        fun onImageClick(position: Int)

        fun onCalendarClick()

        fun onRoomFilterClick()

        fun onMapClick()

        fun onClipAddressClick()

        fun onNavigatorClick()

        fun onConciergeClick()

        fun onMoreRoomClick(expanded: Boolean)

        fun onPriceTypeClick(priceType: StayDetailPresenter.PriceType)

        fun onConciergeFaqClick()

        fun onConciergeHappyTalkClick()

        fun onConciergeCallClick()

        fun onRoomClick(stayRoom: StayRoom)

        fun onTrueReviewClick()

        fun onTrueVRClick()

        fun onDownloadCouponClick()

        fun onHideWishTooltipClick()

        fun onLoginClick()

        fun onRewardClick()

        fun onRewardGuideClick()

        fun onTrueAwardsClick()

        fun onShowRoomClick()

        fun onRoomInformationClick()

        fun onStayInformationClick()

        fun onSelectedBedTypeFilter(selected: Boolean, bedType: String)

        fun onSelectedFacilitiesFilter(selected: Boolean, facilities: String)

        fun onResetRoomFilterClick()

        fun onConfirmRoomFilterClick()

        fun onCloseRoomFilterClick()

        fun onScrolledBaseInformation()

        fun onScrolledRoomInformation()

        fun onScrolledStayInformation()
    }

    interface AnalyticsInterface : BaseAnalyticsInterface {
        fun setAnalyticsParam(analyticsParam: StayDetailAnalyticsParam)

        fun getStayPaymentAnalyticsParam(stayDetail: StayDetail, stayRoom: StayRoom): StayPaymentAnalyticsParam

        fun onScreen(activity: Activity, stayBookDateTime: StayBookDateTime, stayDetail: StayDetail?, priceFromList: Int,
                     bedTypeFilter: LinkedHashSet<String>, facilitiesFilter: LinkedHashSet<String>)

        fun onScreen(activity: Activity)

        fun onScreenSoldOut(activity: Activity)

        fun onScreenRoomInformation(activity: Activity)

        fun onScreenStayInformation(activity: Activity)

        fun onEventShareKakaoClick(activity: Activity, login: Boolean, userType: String, benefitAlarm: Boolean//
                                   , stayIndex: Int, stayName: String?)

        fun onEventLinkCopyClick(activity: Activity)

        fun onEventMoreShareClick(activity: Activity)

        fun onEventDownloadCoupon(activity: Activity, stayName: String?)

        fun onEventDownloadCouponByLogin(activity: Activity, login: Boolean)

        fun onEventShare(activity: Activity)

        fun onEventChangedPrice(activity: Activity, deepLink: Boolean, stayName: String?, soldOut: Boolean)

        fun onEventCalendarClick(activity: Activity)

        fun onEventTrueReviewClick(activity: Activity)

        fun onEventTrueVRClick(activity: Activity, stayIndex: Int)

        fun onEventImageClick(activity: Activity, stayName: String?)

        fun onEventConciergeClick(activity: Activity)

        fun onEventMapClick(activity: Activity, stayName: String?)

        fun onEventClipAddressClick(activity: Activity, stayName: String?)

        fun onEventWishClick(activity: Activity, stayBookDateTime: StayBookDateTime, stayDetail: StayDetail?, priceFromList: Int, myWish: Boolean)

        fun onEventCallClick(activity: Activity)

        fun onEventFaqClick(activity: Activity)

        fun onEventHappyTalkClick(activity: Activity)

        fun onEventShowTrueReview(activity: Activity, stayIndex: Int)

        fun onEventShowCoupon(activity: Activity, stayIndex: Int)

        fun onEventTrueAwards(activity: Activity, stayIndex: Int)

        fun onEventTrueAwardsClick(activity: Activity, stayIndex: Int)

        fun onEventRoomFilterClick(activity: Activity)

        fun onEventConfirmRoomFilterClick(activity: Activity, bedTypeFilter: LinkedHashSet<String>, facilitiesFilter: LinkedHashSet<String>)

        fun onEventResetFilterAndShowAllRoom(activity: Activity)

        fun onEventFoldRoom(activity: Activity, filtered: Boolean)

        fun onEventUnfoldRoom(activity: Activity, filtered: Boolean)
    }
}
