package com.daily.dailyhotel.screen.mydaily.coupon.dialog

import android.app.Activity
import com.crashlytics.android.Crashlytics
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager

class SelectStayCouponDialogAnalyticsImpl : SelectStayCouponDialogInterface.AnalyticsInterface {
    override fun onCancelByPayment(activity: Activity, couponCount: Int, categoryCode: String, stayName: String, roomPrice: Int) {
        try {
            when (couponCount) {
                0 -> {
                    // empty list
                    val label = "$categoryCode-$stayName-$roomPrice"
                    AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS, AnalyticsManager.Action.HOTEL_COUPON_NOT_FOUND, label, null)
                }

                else -> {
                    AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS,
                            AnalyticsManager.Action.HOTEL_USING_COUPON_CANCEL_CLICKED, AnalyticsManager.Label.HOTEL_USING_COUPON_CANCEL, null)
                }
            }
        } catch (e: Exception) {
            Crashlytics.logException(e)
        }
    }
}