package com.daily.dailyhotel.screen.mydaily.coupon.list

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.daily.base.BaseActivity
import com.twoheart.dailyhotel.R

class CouponListActivity : BaseActivity<CouponListPresenter>() {

    companion object {
        @JvmStatic
        fun newInstance(context: Context): Intent {
            val intent = Intent(context, CouponListActivity::class.java)

            return intent
        }
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