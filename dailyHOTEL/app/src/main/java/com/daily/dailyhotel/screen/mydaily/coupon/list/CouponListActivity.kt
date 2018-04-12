package com.daily.dailyhotel.screen.mydaily.coupon.list

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.daily.base.BaseActivity
import com.daily.base.util.DailyTextUtils
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.util.Constants

class CouponListActivity : BaseActivity<CouponListPresenter>() {

    companion object {
        @JvmStatic
        fun newInstance(context: Context, sortType: SortType, deepLink: String?): Intent {
            val intent = Intent(context, CouponListActivity::class.java)

            intent.putExtra(INTENT_EXTRA_DATA_SORT_TYPE, sortType.name)
            (!DailyTextUtils.isTextEmpty(deepLink)).let { intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_DEEPLINK, deepLink) }

            return intent
        }

        const val INTENT_EXTRA_DATA_SORT_TYPE = "sortType"
    }

    enum class SortType {
        ALL,
        STAY,
        GOURMET
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold)

        super.onCreate(savedInstanceState)
    }

    override fun createInstancePresenter(): CouponListPresenter {
        return CouponListPresenter(this)
    }

    override fun finish() {
        super.finish()

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right)
    }
}