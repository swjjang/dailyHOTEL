package com.daily.dailyhotel.screen.event.list.kotlin

import android.app.Activity
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager

class EventListAnalyticsImpl : EventListInterface.AnalyticsInterface {
    override fun onScreen(activity: Activity) {
        activity?.let {
            AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.EVENT_LIST, null)
        }
    }

}