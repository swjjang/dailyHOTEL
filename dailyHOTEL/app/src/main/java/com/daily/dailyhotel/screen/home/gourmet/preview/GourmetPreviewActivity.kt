package com.daily.dailyhotel.screen.home.gourmet.preview

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.daily.base.BaseActivity
import com.twoheart.dailyhotel.R

class GourmetPreviewActivity : BaseActivity<GourmetPreviewPresenter>() {

    companion object {
        const val REQUEST_CODE_WISH_DIALOG = 10000

        const val INTENT_EXTRA_DATA_WISH = "wish"
        const val INTENT_EXTRA_DATA_VISIT_DATE_TIME = "visitDateTime"
        const val INTENT_EXTRA_DATA_GOURMET_INDEX = "gourmetIndex"
        const val INTENT_EXTRA_DATA_GOURMET_NAME = "gourmetName"
        const val INTENT_EXTRA_DATA_GOURMET_CATEGORY = "gourmetCategory"
        const val INTENT_EXTRA_DATA_GOURMET_VIEW_PRICE = "viewPrice"

        const val SKIP_CHECK_PRICE_VALUE = Int.MIN_VALUE

        @JvmStatic
        fun newInstance(context: Context, visitDateTime: String
                        , index: Int, name: String, category: String
                        , viewPrice: Int = SKIP_CHECK_PRICE_VALUE): Intent {
            val intent = Intent(context, GourmetPreviewActivity::class.java)

            intent.putExtra(INTENT_EXTRA_DATA_VISIT_DATE_TIME, visitDateTime)
            intent.putExtra(INTENT_EXTRA_DATA_GOURMET_INDEX, index)
            intent.putExtra(INTENT_EXTRA_DATA_GOURMET_NAME, name)
            intent.putExtra(INTENT_EXTRA_DATA_GOURMET_CATEGORY, category)
            intent.putExtra(INTENT_EXTRA_DATA_GOURMET_VIEW_PRICE, viewPrice)

            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.hold, R.anim.hold)

        super.onCreate(savedInstanceState)
    }

    override fun createInstancePresenter(): GourmetPreviewPresenter {
        return GourmetPreviewPresenter(this)
    }

    override fun finish() {
        super.finish()

        overridePendingTransition(R.anim.hold, R.anim.hold)
    }
}