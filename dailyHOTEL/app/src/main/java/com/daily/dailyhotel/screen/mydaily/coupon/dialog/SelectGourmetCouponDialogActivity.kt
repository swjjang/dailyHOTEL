package com.daily.dailyhotel.screen.mydaily.coupon.dialog

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Window
import com.daily.base.BaseActivity
import com.twoheart.dailyhotel.R

class SelectGourmetCouponDialogActivity : BaseActivity<SelectGourmetCouponDialogPresenter>() {

    companion object {
        @JvmStatic
        fun newInstance(context: Context): Intent {
            val intent = Intent(context, SelectGourmetCouponDialogActivity::class.java)

            return intent
        }
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