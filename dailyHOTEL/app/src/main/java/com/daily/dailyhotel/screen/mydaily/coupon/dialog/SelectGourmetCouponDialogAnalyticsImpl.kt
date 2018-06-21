package com.daily.dailyhotel.screen.mydaily.coupon.dialog

import android.app.Activity
import com.crashlytics.android.Crashlytics
import com.daily.base.util.ExLog
import com.daily.dailyhotel.entity.Coupon
import com.twoheart.dailyhotel.util.DailyCalendar
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager
import java.text.ParseException
import java.util.*

class SelectGourmetCouponDialogAnalyticsImpl : SelectGourmetCouponDialogInterface.AnalyticsInterface {
    override fun onScreen(activity: Activity, emptyList: Boolean) {
        val screen: String = if (emptyList) AnalyticsManager.Screen.DAILY_GOURMET_UNAVAILABLE_COUPON_LIST else AnalyticsManager.Screen.DAILY_GOURMET_AVAILABLE_COUPON_LIST
        AnalyticsManager.getInstance(activity).recordScreen(activity, screen, null)
    }

    override fun onCancelByPayment(activity: Activity, couponCount: Int) {
        try {
            when (couponCount) {
                0 -> {
                    // empty list
                }

                else -> {
                    AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS
                            , AnalyticsManager.Action.GOURMET_USING_COUPON_CANCEL_CLICKED, AnalyticsManager.Label.GOURMET_USING_COUPON_CANCEL, null)

                }
            }
        } catch (e: Exception) {
            Crashlytics.logException(e)
        }
    }

    override fun onSelectedCouponResult(activity: Activity, title: String) {
        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS
                , AnalyticsManager.Action.GOURMET_COUPON_SELECTED, title, null)
    }

    override fun onDownloadCoupon(activity: Activity, callByScreen: String, coupon: Coupon) {
        try {

            val paramsMap = HashMap<String, String>().apply {
                put(AnalyticsManager.KeyType.COUPON_NAME, coupon.title)
                put(AnalyticsManager.KeyType.COUPON_AVAILABLE_ITEM, coupon.availableItem)
                put(AnalyticsManager.KeyType.PRICE_OFF, coupon.amount.toString())
                put(AnalyticsManager.KeyType.DOWNLOAD_DATE, DailyCalendar.format(Date(), "yyyyMMddHHmm"))
                put(AnalyticsManager.KeyType.EXPIRATION_DATE, DailyCalendar.convertDateFormatString(coupon.validTo, DailyCalendar.ISO_8601_FORMAT, "yyyyMMddHHmm"))
                put(AnalyticsManager.KeyType.COUPON_CODE, coupon.couponCode)

                // TODO : emily 가 상태에 대한 처리 하면 KIND_OF_COUPON 값 넣는거 수정 필요...
                if (coupon.availableInGourmet && coupon.availableInStay) {
                    put(AnalyticsManager.KeyType.KIND_OF_COUPON, AnalyticsManager.ValueType.ALL)
                } else if (coupon.availableInStay) {
                    put(AnalyticsManager.KeyType.KIND_OF_COUPON, AnalyticsManager.ValueType.STAY)
                } else if (coupon.availableInGourmet) {
                    put(AnalyticsManager.KeyType.KIND_OF_COUPON, AnalyticsManager.ValueType.GOURMET)
                }

                val downloadFrom = when (callByScreen) {
                    AnalyticsManager.Screen.DAILYGOURMET_BOOKINGINITIALISE -> "booking"

                    AnalyticsManager.Screen.DAILYGOURMET_DETAIL -> "detail"

                    else -> ""
                }

                put(AnalyticsManager.KeyType.DOWNLOAD_FROM, downloadFrom)

                AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.COUPON_BOX//
                        , AnalyticsManager.Action.COUPON_DOWNLOAD_CLICKED, "$downloadFrom-${coupon.title}", this)
            }

            if (AnalyticsManager.Screen.DAILYGOURMET_BOOKINGINITIALISE.equals(callByScreen, true)) {
                AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
                        , AnalyticsManager.Action.GOURMET_COUPON_DOWNLOADED, coupon.title, null)
            }
        } catch (e: ParseException) {
            Crashlytics.log("Select Coupon::coupon.validTo: ${if (coupon != null) coupon.validTo else ""}")
            Crashlytics.logException(e)
            ExLog.d(e.toString())
        } catch (e: Exception) {
            ExLog.d(e.toString())
        }
    }
}