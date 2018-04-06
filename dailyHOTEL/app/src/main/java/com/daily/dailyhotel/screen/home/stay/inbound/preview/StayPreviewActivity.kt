package com.daily.dailyhotel.screen.home.stay.inbound.preview

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.daily.base.BaseActivity
import com.twoheart.dailyhotel.R

class StayPreviewActivity : BaseActivity<StayPreviewPresenter>() {
    companion object {
        const val REQUEST_CODE_WISH_DIALOG = 10000

        const val INTENT_EXTRA_DATA_WISH = "wish"
        const val INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME = "checkInDateTime"
        const val INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME = "checkOutDateTime"
        const val INTENT_EXTRA_DATA_STAY_INDEX = "stayIndex"
        const val INTENT_EXTRA_DATA_STAY_NAME = "stayName"
        const val INTENT_EXTRA_DATA_STAY_GRADE = "grade"
        const val INTENT_EXTRA_DATA_STAY_VIEW_PRICE = "viewPrice"

        const val SKIP_CHECK_PRICE_VALUE = Int.MIN_VALUE

        @JvmStatic
        fun newInstance(context: Context, checkDateTime: String, checkOutDateTime: String
                        , index: Int, name: String, grade: String
                        , viewPrice: Int = SKIP_CHECK_PRICE_VALUE): Intent {
            val intent = Intent(context, StayPreviewActivity::class.java)

            intent.putExtra(INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME, checkDateTime)
            intent.putExtra(INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME, checkOutDateTime)
            intent.putExtra(INTENT_EXTRA_DATA_STAY_INDEX, index)
            intent.putExtra(INTENT_EXTRA_DATA_STAY_NAME, name)
            intent.putExtra(INTENT_EXTRA_DATA_STAY_GRADE, grade)
            intent.putExtra(INTENT_EXTRA_DATA_STAY_VIEW_PRICE, viewPrice)

            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.hold, R.anim.hold)

        super.onCreate(savedInstanceState)
    }

    override fun createInstancePresenter(): StayPreviewPresenter {
        return StayPreviewPresenter(this)
    }

    override fun finish() {
        super.finish()

        overridePendingTransition(R.anim.hold, R.anim.hold)
    }
}