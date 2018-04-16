package com.twoheart.dailyhotel.firebase.model

import android.content.Context
import com.bluelinelabs.logansquare.LoganSquare
import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject


class HomeDefaultEventDelegate(jsonString: String) {

    private val homeDefaultEvent: HomeDefaultEvent?

    init {
        homeDefaultEvent = LoganSquare.parse(jsonString, HomeDefaultEvent::class.java)
    }

    val updateTime: String?
        get() {
            return homeDefaultEvent?.updateTime
        }

    val index: Int
        get() {
            return homeDefaultEvent?.index ?: 0
        }

    val title: String?
        get() {
            return homeDefaultEvent?.title
        }

    val eventUrl: String?
        get() {
            return homeDefaultEvent?.eventUrl
        }

    fun getImageUrl(context: Context): String? {
        val densityDpi = context.resources.displayMetrics.densityDpi;

        return if (densityDpi < 480) homeDefaultEvent?.lowResolution else homeDefaultEvent?.highResolution
    }

    @JsonObject
    internal class HomeDefaultEvent {
        @JsonField(name = arrayOf("updateTime"))
        var updateTime: String? = null

        @JsonField(name = arrayOf("index"))
        var index: Int? = null

        @JsonField(name = arrayOf("title"))
        var title: String? = null

        @JsonField(name = arrayOf("eventUrl"))
        var eventUrl: String? = null

        @JsonField(name = arrayOf("lowResolution"))
        var lowResolution: String? = null

        @JsonField(name = arrayOf("highResolution"))
        var highResolution: String? = null
    }
}
