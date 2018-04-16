package com.daily.dailyhotel.screen.mydaily.coupon.register

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.daily.base.BaseActivity
import com.twoheart.dailyhotel.R

class RegisterCouponActivity : BaseActivity<RegisterCouponPresenter>() {

    companion object {
        @JvmStatic
        fun newInstance(context: Context, callByScreen: String?): Intent {
            val intent = Intent(context, RegisterCouponActivity::class.java)
            callByScreen?.let {
                intent.putExtra(EXTRA_DATA_CALL_BY_SCREEN, callByScreen)
            }

            return intent
        }

        const val EXTRA_DATA_CALL_BY_SCREEN = "callByScreen"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold)

        super.onCreate(savedInstanceState)
    }

    override fun createInstancePresenter(): RegisterCouponPresenter {
        return RegisterCouponPresenter(this)
    }

    override fun finish() {
        super.finish()

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right)
    }
}