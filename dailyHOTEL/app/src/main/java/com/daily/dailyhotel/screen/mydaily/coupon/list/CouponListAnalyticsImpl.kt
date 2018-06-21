package com.daily.dailyhotel.screen.mydaily.coupon.list

import android.app.Activity
import com.crashlytics.android.Crashlytics
import com.daily.base.util.ExLog
import com.daily.dailyhotel.entity.Coupon
import com.twoheart.dailyhotel.util.DailyCalendar
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager
import java.text.ParseException
import java.util.*

class CouponListAnalyticsImpl : CouponListInterface.AnalyticsInterface {
    override fun onScreen(activity: Activity) {
        AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.MENU_COUPON_BOX, null)
    }

    override fun onDownloadCoupon(activity: Activity, coupon: Coupon) {
        try {
            val paramMap = HashMap<String, String>().apply {
                put(AnalyticsManager.KeyType.COUPON_NAME, coupon.title)
                put(AnalyticsManager.KeyType.COUPON_AVAILABLE_ITEM, coupon.availableItem)
                put(AnalyticsManager.KeyType.PRICE_OFF, Integer.toString(coupon.amount))
                put(AnalyticsManager.KeyType.DOWNLOAD_DATE, DailyCalendar.format(Date(), "yyyyMMddHHmm"))
                put(AnalyticsManager.KeyType.EXPIRATION_DATE, DailyCalendar.convertDateFormatString(coupon.validTo, DailyCalendar.ISO_8601_FORMAT, "yyyyMMddHHmm"))
                put(AnalyticsManager.KeyType.DOWNLOAD_FROM, "couponbox")
                put(AnalyticsManager.KeyType.COUPON_CODE, coupon.couponCode)

                if (coupon.availableInGourmet && coupon.availableInStay && coupon.availableInOutboundHotel) {
                    put(AnalyticsManager.KeyType.KIND_OF_COUPON, AnalyticsManager.ValueType.ALL)
                } else if (coupon.availableInStay || coupon.availableInOutboundHotel) {
                    put(AnalyticsManager.KeyType.KIND_OF_COUPON, AnalyticsManager.ValueType.STAY)
                } else if (coupon.availableInGourmet) {
                    put(AnalyticsManager.KeyType.KIND_OF_COUPON, AnalyticsManager.ValueType.GOURMET)
                }
            }

            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.COUPON_BOX
                    , AnalyticsManager.Action.COUPON_DOWNLOAD_CLICKED, "couponbox-" + coupon.title, paramMap)
        } catch (e: ParseException) {
            Crashlytics.log("Coupon List::coupon.validTo: " + if (coupon != null) coupon.validTo else "")
            Crashlytics.logException(e)
            ExLog.d(e.toString())
        } catch (e: Exception) {
            ExLog.d(e.toString())
        }
    }
}