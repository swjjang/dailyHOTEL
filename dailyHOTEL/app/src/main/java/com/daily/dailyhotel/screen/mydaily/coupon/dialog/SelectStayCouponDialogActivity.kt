package com.daily.dailyhotel.screen.mydaily.coupon.dialog

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Window
import com.daily.base.BaseActivity
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.util.Constants
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager

class SelectStayCouponDialogActivity : BaseActivity<SelectStayCouponDialogPresenter>() {

    companion object {
        @JvmStatic
        fun newInstance(context: Context, stayIndex: Int, roomIndex: Int, checkInDate: String, checkOutDate: String
                        , categoryCode: String, stayName: String, roomPrice: Int): Intent {
            return Intent(context, SelectStayCouponDialogActivity::class.java).apply {
                putExtra(INTENT_EXTRA_STAY_IDX, stayIndex)
                putExtra(INTENT_EXTRA_ROOM_IDX, roomIndex)
                putExtra(INTENT_EXTRA_CHECK_IN_DATE, checkInDate)
                putExtra(INTENT_EXTRA_CHECK_OUT_DATE, checkOutDate)
                putExtra(INTENT_EXTRA_CATEGORY_CODE, categoryCode)
                putExtra(INTENT_EXTRA_STAY_NAME, stayName)
                putExtra(INTENT_EXTRA_ROOM_PRICE, roomPrice)
                putExtra(Constants.NAME_INTENT_EXTRA_DATA_CALL_BY_SCREEN, AnalyticsManager.Screen.DAILYHOTEL_BOOKINGINITIALISE)
            }
        }

        @JvmStatic
        fun newInstance(context: Context, stayIndex: Int, checkInDate: String, checkOutDate: String
                        , categoryCode: String, stayName: String): Intent {
            return Intent(context, SelectStayCouponDialogActivity::class.java).apply {
                putExtra(INTENT_EXTRA_STAY_IDX, stayIndex)
                putExtra(INTENT_EXTRA_CHECK_IN_DATE, checkInDate)
                putExtra(INTENT_EXTRA_CHECK_OUT_DATE, checkOutDate)
                putExtra(INTENT_EXTRA_CATEGORY_CODE, categoryCode)
                putExtra(INTENT_EXTRA_STAY_NAME, stayName)
                putExtra(Constants.NAME_INTENT_EXTRA_DATA_CALL_BY_SCREEN, AnalyticsManager.Screen.DAILYHOTEL_DETAIL)
            }
        }

        const val INTENT_EXTRA_STAY_IDX = "stayIdx"
        const val INTENT_EXTRA_ROOM_IDX = "roomIdx"
        const val INTENT_EXTRA_CATEGORY_CODE = "categoryCode"
        const val INTENT_EXTRA_STAY_NAME = "stayName"
        const val INTENT_EXTRA_ROOM_PRICE = "roomPrice"
        const val INTENT_EXTRA_CHECK_IN_DATE = "checkInDate"
        const val INTENT_EXTRA_CHECK_OUT_DATE = "checkOutDate"
        const val INTENT_EXTRA_SELECT_COUPON = "selectCoupon"
        const val INTENT_EXTRA_MAX_COUPON_AMOUNT = "maxCouponAmount"
        const val INTENT_EXTRA_HAS_DOWNLOADABLE_COUPON = "hasDownloadableCoupon"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.hold, R.anim.hold)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)

        super.onCreate(savedInstanceState)
    }

    override fun createInstancePresenter(): SelectStayCouponDialogPresenter {
        return SelectStayCouponDialogPresenter(this)
    }

    override fun finish() {
        super.finish()

        overridePendingTransition(R.anim.hold, R.anim.hold)
    }
}