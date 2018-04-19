package com.daily.dailyhotel.screen.mydaily.coupon.dialog

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Window
import com.daily.base.BaseActivity
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.util.Constants
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager

class SelectGourmetCouponDialogActivity : BaseActivity<SelectGourmetCouponDialogPresenter>() {

    companion object {
        @JvmStatic
        fun newInstance(context: Context, visitDay: String, gourmetIndex: Int, gourmetName: String//
                        , ticketIndexes: IntArray, ticketCounts: IntArray): Intent {
            val intent = Intent(context, SelectGourmetCouponDialogActivity::class.java)
            intent.putExtra(INTENT_EXTRA_VISIT_DAY, visitDay)
            intent.putExtra(INTENT_EXTRA_GOURMET_INDEX, gourmetIndex)
            intent.putExtra(INTENT_EXTRA_GOURMET_NAME, gourmetName)
            intent.putExtra(INTENT_EXTRA_TICKET_INDEXES, ticketIndexes)
            intent.putExtra(INTENT_EXTRA_TICKET_COUNTS, ticketCounts)
            intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_CALL_BY_SCREEN, AnalyticsManager.Screen.DAILYGOURMET_BOOKINGINITIALISE)

            return intent
        }

        @JvmStatic
        fun newInstance(context: Context, visitDay: String, gourmetIndex: Int, gourmetName: String): Intent {
            val intent = Intent(context, SelectGourmetCouponDialogActivity::class.java)
            intent.putExtra(INTENT_EXTRA_VISIT_DAY, visitDay)
            intent.putExtra(INTENT_EXTRA_GOURMET_INDEX, gourmetIndex)
            intent.putExtra(INTENT_EXTRA_GOURMET_NAME, gourmetName)
            intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_CALL_BY_SCREEN, AnalyticsManager.Screen.DAILYGOURMET_DETAIL)

            return intent
        }

        const val INTENT_EXTRA_SELECT_COUPON = "selectCoupon"
        const val INTENT_EXTRA_GOURMET_INDEX = "gourmetIndex"
        const val INTENT_EXTRA_TICKET_INDEXES = "ticketIndexes"
        const val INTENT_EXTRA_VISIT_DAY = "visitDay"
        const val INTENT_EXTRA_GOURMET_NAME = "gourmetName"
        const val INTENT_EXTRA_TICKET_COUNTS = "ticketCounts"
        const val INTENT_EXTRA_MAX_COUPON_AMOUNT = "maxCouponAmount"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.hold, R.anim.hold)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)

        super.onCreate(savedInstanceState)
    }

    override fun createInstancePresenter(): SelectGourmetCouponDialogPresenter {
        return SelectGourmetCouponDialogPresenter(this)
    }

    override fun finish() {
        super.finish()

        overridePendingTransition(R.anim.hold, R.anim.hold)
    }
}