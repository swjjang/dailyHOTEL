package com.daily.dailyhotel.screen.event.list

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.daily.base.BaseActivity
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.util.Constants

class EventListActivity : BaseActivity<EventListPresenter>() {

    companion object {
        @JvmStatic
        fun newInstance(context: Context, deepLink: String?): Intent {
            val intent = Intent(context, EventListActivity::class.java)

            deepLink?.let {
                intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_DEEPLINK, it)
            }

            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold)

        super.onCreate(savedInstanceState)
    }

    override fun createInstancePresenter(): EventListPresenter {
        return EventListPresenter(this)
    }

    override fun finish() {
        super.finish()

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right)
    }
}