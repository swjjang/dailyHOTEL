package com.daily.dailyhotel.screen.copy.kotlin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.daily.base.BaseActivity
import com.twoheart.dailyhotel.R

class CopyActivity : BaseActivity<CopyPresenter>() {

    companion object {
        @JvmStatic
        fun newInstance(context: Context): Intent {
            val intent = Intent(context, CopyActivity::class.java)

            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold)

        super.onCreate(savedInstanceState)
    }

    override fun createInstancePresenter(): CopyPresenter {
        return CopyPresenter(this)
    }

    override fun finish() {
        super.finish()

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right)
    }
}