package com.daily.dailyhotel.screen.home.stay.inbound.detail

import android.app.Activity
import com.daily.base.util.DailyTextUtils
import com.daily.base.util.ExLog
import com.daily.dailyhotel.entity.StayBookDateTime
import com.daily.dailyhotel.entity.StayDetail
import com.daily.dailyhotel.entity.StayRoom
import com.daily.dailyhotel.parcel.analytics.StayDetailAnalyticsParam
import com.daily.dailyhotel.parcel.analytics.StayPaymentAnalyticsParam
import com.daily.dailyhotel.util.isNotNullAndNotEmpty
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.util.Constants
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager
import java.util.*

class StayDetailAnalyticsImpl : StayDetailInterface.AnalyticsInterface {
    private var analyticsParam: StayDetailAnalyticsParam? = null

    override fun setAnalyticsParam(analyticsParam: StayDetailAnalyticsParam) {
        this.analyticsParam = analyticsParam
    }

    override fun getStayPaymentAnalyticsParam(stayDetail: StayDetail, stayRoom: StayRoom): StayPaymentAnalyticsParam {

        return StayPaymentAnalyticsParam()
    }

    override fun onScreen(activity: Activity, stayBookDateTime: StayBookDateTime, stayDetail: StayDetail?, priceFromList: Int,
                          bedTypeFilter: LinkedHashSet<String>, facilitiesFilter: LinkedHashSet<String>) {
        if (stayDetail == null) return

        try {
            val params = HashMap<String, String?>()
            params[AnalyticsManager.KeyType.PLACE_INDEX] = stayDetail.index.toString()
            params[AnalyticsManager.KeyType.NAME] = stayDetail.baseInformation?.name
            params[AnalyticsManager.KeyType.GRADE] = stayDetail.baseInformation?.grade?.getName(activity)
            params[AnalyticsManager.KeyType.DBENEFIT] = if (DailyTextUtils.isTextEmpty(stayDetail?.benefitInformation?.title)) "no" else "yes" // 3

            params[AnalyticsManager.KeyType.PRICE] = if (stayDetail.roomInformation?.roomList == null || !stayDetail.roomInformation?.roomList.isNotNullAndNotEmpty())
                "0"
            else
                stayDetail.roomInformation?.roomList?.get(0)?.amountInformation?.discountAverage.toString()

            val nights = stayBookDateTime.nights

            params[AnalyticsManager.KeyType.QUANTITY] = nights.toString()
            params[AnalyticsManager.KeyType.PLACE_INDEX] = stayDetail.index.toString()
            params[AnalyticsManager.KeyType.CHECK_IN] = stayBookDateTime.getCheckInDateTime("yyyy-MM-dd") // 1
            params[AnalyticsManager.KeyType.CHECK_OUT] = stayBookDateTime.getCheckOutDateTime("yyyy-MM-dd") // 2
            params[AnalyticsManager.KeyType.ADDRESS] = stayDetail.addressInformation?.address

            params[AnalyticsManager.KeyType.CATEGORY] = if (DailyTextUtils.isTextEmpty(stayDetail.baseInformation?.category))
                AnalyticsManager.ValueType.EMPTY
            else
                stayDetail.baseInformation?.category

            analyticsParam?.let {
                params[AnalyticsManager.KeyType.PROVINCE] = it.areaGroupName
                params[AnalyticsManager.KeyType.DISTRICT] = it.areaName
                params[AnalyticsManager.KeyType.AREA] = it.addressAreaName

                params[AnalyticsManager.KeyType.LIST_INDEX] = if (it.entryPosition == -1)
                    AnalyticsManager.ValueType.EMPTY
                else
                    Integer.toString(it.entryPosition)

                params[AnalyticsManager.KeyType.PLACE_COUNT] = if (it.totalListCount == -1)
                    AnalyticsManager.ValueType.EMPTY
                else
                    Integer.toString(it.totalListCount)

                params[AnalyticsManager.KeyType.IS_SHOW_ORIGINAL_PRICE] = it.getShowOriginalPriceYn()
                params[AnalyticsManager.KeyType.DAILYCHOICE] = if (it.isDailyChoice) "y" else "n"
            }

            params[AnalyticsManager.KeyType.UNIT_PRICE] = priceFromList.toString()
            params[AnalyticsManager.KeyType.CHECK_IN_DATE] = stayBookDateTime.getCheckInDateTime("yyyyMMdd")
            params[AnalyticsManager.KeyType.RATING] = stayDetail.trueReviewInformation?.ratingPercent.toString()
            params[AnalyticsManager.KeyType.LENGTH_OF_STAY] = nights.toString()
            params[AnalyticsManager.KeyType.COUNTRY] = AnalyticsManager.ValueType.DOMESTIC

            // 베드타입
            params[AnalyticsManager.KeyType.BEDTYPE_DOUBLE] = bedTypeFilter.contains("DOUBLE").toString()
            params[AnalyticsManager.KeyType.BEDTYPE_TWIN] = bedTypeFilter.contains("TWIN").toString()
            params[AnalyticsManager.KeyType.BEDTYPE_IN_FLOOR_HEATING] = bedTypeFilter.contains("IN_FLOOR_HEATING").toString()
            params[AnalyticsManager.KeyType.BEDTYPE_SINGLE] = bedTypeFilter.contains("SINGLE").toString()

            // 시설
            analyticsParam?.let {
                params[AnalyticsManager.KeyType.FACILITY_KIDS_PLAY_ROOM] = it.amenitiesFilter.contains("KidsPlayroom").toString()
                params[AnalyticsManager.KeyType.FACILITY_POOL] = it.amenitiesFilter.contains("Pool").toString()
                params[AnalyticsManager.KeyType.FACILITY_PET] = it.amenitiesFilter.contains("Pet").toString()
            }

            params[AnalyticsManager.KeyType.FACILITY_BREAKFAST] = facilitiesFilter.contains("Breakfast").toString()
            params[AnalyticsManager.KeyType.FACILITY_PART_ROOM] = facilitiesFilter.contains("PartyRoom").toString()
            params[AnalyticsManager.KeyType.FACILITY_WHIRLPOOL] = facilitiesFilter.contains("SpaWallpool").toString()

            AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.DAILYHOTEL_DETAIL, null, params)
        } catch (e: Exception) {
            ExLog.e(e.toString())
        }
    }

    override fun onScreen(activity: Activity) {
        AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.DAILYHOTEL_DETAIL, null)
    }

    override fun onScreenSoldOut(activity: Activity) {
        AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.DAILYHOTEL_HOTELDETAILVIEW_EMPTY, null)
    }

    override fun onScreenRoomInformation(activity: Activity) {
        AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.INFORMATION_ROOM, null)
    }

    override fun onScreenStayInformation(activity: Activity) {
        AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.INFORMATION_STAY, null)
    }

    override fun onEventShareKakaoClick(activity: Activity, login: Boolean, userType: String, benefitAlarm: Boolean, stayIndex: Int, stayName: String?) {
        try {
            val params = HashMap<String, String?>()
            params[AnalyticsManager.KeyType.SERVICE] = AnalyticsManager.ValueType.STAY
            params[AnalyticsManager.KeyType.COUNTRY] = AnalyticsManager.ValueType.DOMESTIC

            params[AnalyticsManager.KeyType.PROVINCE] = analyticsParam?.getAreaGroupName()

            if (login) {
                params[AnalyticsManager.KeyType.USER_TYPE] = AnalyticsManager.ValueType.MEMBER

                when (userType) {
                    Constants.DAILY_USER -> params[AnalyticsManager.KeyType.MEMBER_TYPE] = AnalyticsManager.UserType.EMAIL

                    Constants.KAKAO_USER -> params[AnalyticsManager.KeyType.MEMBER_TYPE] = AnalyticsManager.UserType.KAKAO

                    Constants.FACEBOOK_USER -> params[AnalyticsManager.KeyType.MEMBER_TYPE] = AnalyticsManager.UserType.FACEBOOK

                    else -> params[AnalyticsManager.KeyType.MEMBER_TYPE] = AnalyticsManager.ValueType.EMPTY
                }
            } else {
                params[AnalyticsManager.KeyType.USER_TYPE] = AnalyticsManager.ValueType.GUEST
                params[AnalyticsManager.KeyType.MEMBER_TYPE] = AnalyticsManager.ValueType.EMPTY
            }

            params[AnalyticsManager.KeyType.PUSH_NOTIFICATION] = if (benefitAlarm) "on" else "off"
            params[AnalyticsManager.KeyType.SHARE_METHOD] = AnalyticsManager.ValueType.KAKAO
            params[AnalyticsManager.KeyType.VENDOR_ID] = stayIndex.toString()
            params[AnalyticsManager.KeyType.VENDOR_NAME] = stayName

            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SHARE
                    , AnalyticsManager.Action.STAY_ITEM_SHARE, AnalyticsManager.ValueType.KAKAO, params)
        } catch (e: Exception) {
            ExLog.d(e.toString())
        }
    }

    override fun onEventLinkCopyClick(activity: Activity) {
        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SHARE,
                AnalyticsManager.Action.STAY_ITEM_SHARE, AnalyticsManager.ValueType.LINK_COPY, null)
    }

    override fun onEventMoreShareClick(activity: Activity) {
        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SHARE,
                AnalyticsManager.Action.STAY_ITEM_SHARE, AnalyticsManager.ValueType.ETC, null)
    }

    override fun onEventDownloadCoupon(activity: Activity, stayName: String?) {
        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS, AnalyticsManager.Action.HOTEL_COUPON_DOWNLOAD, stayName, null)
    }

    override fun onEventDownloadCouponByLogin(activity: Activity, login: Boolean) {
        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.POPUP_BOXES, AnalyticsManager.Action.COUPON_LOGIN,
                if (login) AnalyticsManager.Label.LOGIN_ else AnalyticsManager.Label.CLOSED, null)
    }

    override fun onEventShare(activity: Activity) {
        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SHARE,
                AnalyticsManager.Action.ITEM_SHARE, AnalyticsManager.Label.STAY, null)
    }

    override fun onEventChangedPrice(activity: Activity, deepLink: Boolean, stayName: String?, soldOut: Boolean) {
        if (soldOut) {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.POPUP_BOXES,
                    if (deepLink) AnalyticsManager.Action.SOLDOUT_DEEPLINK else AnalyticsManager.Action.SOLDOUT, stayName, null)
        } else {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.POPUP_BOXES,
                    AnalyticsManager.Action.SOLDOUT_CHANGEPRICE, stayName, null)
        }
    }

    override fun onEventCalendarClick(activity: Activity) {
        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.DETAILVIEW_STAY, "filter_date", AnalyticsManager.ValueType.EMPTY, null)
    }

    override fun onEventTrueReviewClick(activity: Activity) {
        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.TRUE_REVIEW_CLICK, AnalyticsManager.Label.STAY, null)
    }

    override fun onEventTrueVRClick(activity: Activity, stayIndex: Int) {
        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION,
                AnalyticsManager.Action.TRUE_VR_CLICK, Integer.toString(stayIndex), null)
    }

    override fun onEventImageClick(activity: Activity, stayName: String?) {
        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS,
                AnalyticsManager.Action.HOTEL_IMAGE_CLICKED, stayName, null)
    }

    override fun onEventConciergeClick(activity: Activity) {
        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION,
                AnalyticsManager.Action.CONTACT_DAILY_CONCIERGE, AnalyticsManager.Label.STAY_DETAIL, null)
    }

    override fun onEventMapClick(activity: Activity, stayName: String?) {
        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS,
                AnalyticsManager.Action.HOTEL_DETAIL_MAP_CLICKED, stayName, null)
    }

    override fun onEventClipAddressClick(activity: Activity, stayName: String?) {
        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS,
                AnalyticsManager.Action.HOTEL_DETAIL_ADDRESS_COPY_CLICKED, stayName, null)
    }

    override fun onEventWishClick(activity: Activity, stayBookDateTime: StayBookDateTime, stayDetail: StayDetail?, priceFromList: Int, myWish: Boolean) {
        if (stayDetail == null) {
            return
        }

        try {
            val params = HashMap<String, String?>()
            params[AnalyticsManager.KeyType.PLACE_TYPE] = AnalyticsManager.ValueType.STAY
            params[AnalyticsManager.KeyType.NAME] = stayDetail.baseInformation?.name
            params[AnalyticsManager.KeyType.VALUE] = priceFromList.toString()
            params[AnalyticsManager.KeyType.COUNTRY] = AnalyticsManager.ValueType.DOMESTIC
            params[AnalyticsManager.KeyType.CATEGORY] = stayDetail.baseInformation?.category

            params[AnalyticsManager.KeyType.PROVINCE] = analyticsParam?.getAreaGroupName()
            params[AnalyticsManager.KeyType.DISTRICT] = analyticsParam?.getAreaName()
            params[AnalyticsManager.KeyType.AREA] = analyticsParam?.getAddressAreaName()

            params[AnalyticsManager.KeyType.GRADE] = stayDetail.baseInformation?.grade?.getName(activity)
            params[AnalyticsManager.KeyType.PLACE_INDEX] = Integer.toString(stayDetail.index)
            params[AnalyticsManager.KeyType.RATING] = stayDetail.trueReviewInformation?.ratingPercent.toString()

            val listIndex = if (analyticsParam?.entryPosition == -1)
                AnalyticsManager.ValueType.EMPTY
            else
                analyticsParam?.entryPosition.toString()

            params[AnalyticsManager.KeyType.LIST_INDEX] = listIndex
            params[AnalyticsManager.KeyType.DAILYCHOICE] = if (analyticsParam?.isDailyChoice == true) "y" else "n"
            params[AnalyticsManager.KeyType.DBENEFIT] = if (DailyTextUtils.isTextEmpty(stayDetail.benefitInformation?.title)) "no" else "yes"

            val nights = stayBookDateTime.nights

            params[AnalyticsManager.KeyType.CHECK_IN] = stayBookDateTime.getCheckInDateTime("yyyy-MM-dd")
            params[AnalyticsManager.KeyType.CHECK_OUT] = stayBookDateTime.getCheckOutDateTime("yyyy-MM-dd")
            params[AnalyticsManager.KeyType.LENGTH_OF_STAY] = nights.toString()
            params[AnalyticsManager.KeyType.IS_SHOW_ORIGINAL_PRICE] = analyticsParam?.getShowOriginalPriceYn()

            AnalyticsManager.getInstance(activity).recordEvent(//
                    AnalyticsManager.Category.NAVIGATION_, //
                    if (myWish) AnalyticsManager.Action.WISHLIST_ON else AnalyticsManager.Action.WISHLIST_OFF, stayDetail.baseInformation?.name, params)
        } catch (e: Exception) {
            ExLog.d(e.toString())
        }
    }

    override fun onEventCallClick(activity: Activity) {
        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE,
                AnalyticsManager.Action.CALL_CLICK, AnalyticsManager.Label.STAY_DETAIL, null)
    }

    override fun onEventFaqClick(activity: Activity) {
        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE,
                AnalyticsManager.Action.FNQ_CLICK, AnalyticsManager.Label.STAY_DETAIL, null)
    }

    override fun onEventHappyTalkClick(activity: Activity) {
        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE,
                AnalyticsManager.Action.HAPPYTALK_CLICK, AnalyticsManager.Label.STAY_DETAIL, null)
    }

    override fun onEventShowTrueReview(activity: Activity, stayIndex: Int) {
        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.DETAIL_PAGE_TRUE_REVIEW,
                AnalyticsManager.Label.STAY, Integer.toString(stayIndex), null)
    }

    override fun onEventShowCoupon(activity: Activity, stayIndex: Int) {
        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.DETAIL_PAGE_COUPON,
                AnalyticsManager.Label.STAY, Integer.toString(stayIndex), null)
    }

    override fun onEventTrueAwards(activity: Activity, stayIndex: Int) {
        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.TRUE_AWARDS,
                AnalyticsManager.Action.DETAIL_PAGE, Integer.toString(stayIndex), null)
    }

    override fun onEventTrueAwardsClick(activity: Activity, stayIndex: Int) {
        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.TRUE_AWARDS,
                AnalyticsManager.Action.QUESTION_MARK, Integer.toString(stayIndex), null)
    }

    override fun onEventRoomFilterClick(activity: Activity) {
        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.DETAILVIEW_STAY, "filter_roomtype", AnalyticsManager.ValueType.EMPTY, null)
    }

    override fun onEventConfirmRoomFilterClick(activity: Activity, bedTypeFilter: LinkedHashSet<String>, facilitiesFilter: LinkedHashSet<String>) {
        try {
            bedTypeFilter.forEach {
                getBedTypeResourceName(it).takeIf { it > 0 }?.let {
                    AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.DETAILVIEW_STAY,
                            "filter_bedtype", activity.getString(it), null)
                }
            }

            facilitiesFilter.forEach {
                getFacilitiesResourceName(it).takeIf { it > 0 }?.let {
                    AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.DETAILVIEW_STAY,
                            "filter_room_amenities", activity.getString(it), null)
                }
            }
        } catch (e: Exception) {
            ExLog.e(e.toString())
        }
    }

    private fun getBedTypeResourceName(bedType: String): Int {
        return when (bedType) {
            "DOUBLE" -> R.string.label_double
            "TWIN" -> R.string.label_twin
            "IN_FLOOR_HEATING" -> R.string.label_in_floor_heating
            "SINGLE" -> R.string.label_single
            else -> 0
        }
    }

    private fun getFacilitiesResourceName(bedType: String): Int {
        return when (bedType) {
            "SPAWALLPOOL" -> R.string.label_whirlpool
            "BATH" -> R.string.label_bathtub
            "AMENITY" -> R.string.label_bath_amenity
            "SHOWERGOWN" -> R.string.label_shower_gown
            "TOOTHBRUSHSET" -> R.string.label_toothbrush_set
            "PRIVATEBBQ" -> R.string.label_private_bbq
            "PRIVATEPOOL" -> R.string.label_private_pool
            "PARTYROOM" -> R.string.label_party_room
            "KARAOKE" -> R.string.label_karaoke
            "BREAKFAST" -> R.string.label_breakfast
            "PC" -> R.string.label_computer
            "TV" -> R.string.label_television
            "COOKING" -> R.string.label_cooking
            "SMOKEABLE" -> R.string.label_smokeable
            "DISABLEDFACILITIES" -> R.string.label_disabled_facilities
            else -> 0
        }
    }

    override fun onEventResetFilterAndShowAllRoom(activity: Activity) {
        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.DETAILVIEW_STAY,
                "filter_see_all", AnalyticsManager.ValueType.EMPTY, null)
    }

    override fun onEventFoldRoom(activity: Activity, filtered: Boolean) {
        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.DETAILVIEW_STAY,
                "filter_fold_" + if (filtered) "filtered" else "nonfiltered", AnalyticsManager.ValueType.EMPTY, null)
    }

    override fun onEventUnfoldRoom(activity: Activity, filtered: Boolean) {
        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.DETAILVIEW_STAY,
                "filter_unfold_" + if (filtered) "filtered" else "nonfiltered", AnalyticsManager.ValueType.EMPTY, null)
    }
}