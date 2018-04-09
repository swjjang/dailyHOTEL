package com.daily.dailyhotel.screen.home.gourmet.preview

import android.app.Activity
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager

class GourmetPreviewAnalyticsImpl : GourmetPreviewInterface.AnalyticsInterface {

    override fun onScreen(activity: Activity, category: String?) {
        val params = hashMapOf(
                AnalyticsManager.KeyType.PLACE_TYPE to AnalyticsManager.ValueType.GOURMET
                , AnalyticsManager.KeyType.CATEGORY to category
        )

        AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.PEEK_POP, null, params)
    }

    override fun onEventBackClick(activity: Activity) {
        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.PEEK_POP_CLOSE, AnalyticsManager.Label.BACKKEY, null)
    }

    override fun onEventCloseClick(activity: Activity) {
        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.PEEK_POP_CLOSE, AnalyticsManager.Label.CLOSE, null)
    }

    override fun onEventWishClick(activity: Activity, wish: Boolean) {
        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , if (wish) AnalyticsManager.Action.PEEK_POP_ADD_WISHLIST else AnalyticsManager.Action.PEEK_POP_DELETE_WISHLIST, null, null)
    }

    override fun onEventKakaoClick(activity: Activity) {
        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.PEEK_POP_SHARE_KAKAO, null, null)
    }

    override fun onEventMapClick(activity: Activity) {
        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.PEEK_POP_NAVER_MAP, null, null)
    }

    override fun onEventDetailClick(activity: Activity) {
        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.PEEK_POP_RESERVATION, null, null)
    }
}