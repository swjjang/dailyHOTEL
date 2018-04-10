package com.daily.dailyhotel.screen.copy.kotlin

import android.app.Activity
import com.daily.base.BaseAnalyticsInterface
import com.daily.base.BaseDialogViewInterface
import com.daily.base.OnBaseEventListener
import com.twoheart.dailyhotel.network.model.Event

interface EventListInterface {
    interface ViewInterface : BaseDialogViewInterface {
        fun onEventList(eventList: MutableList<Event>)
    }

    interface OnEventListener : OnBaseEventListener {
        fun onItemClick(event: Event)
        fun onHomeButtonClick()
    }

    interface AnalyticsInterface : BaseAnalyticsInterface {
        fun onScreen(activity: Activity)
    }
}
