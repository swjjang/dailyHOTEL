package com.twoheart.dailyhotel.firebase.model

import android.content.Context
import com.bluelinelabs.logansquare.LoganSquare
import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject


class SplashDelegate(jsonString: String) {

    private val splash: Splash?

    init {
        splash = LoganSquare.parse(jsonString, Splash::class.java)
    }

    val updateTime: String?
        get() {
            return splash?.imageUpdate?.updateTime
        }

    fun getUrl(context: Context): String? {
        val densityDpi = context.resources.displayMetrics.densityDpi;

        when {
            densityDpi < 240 -> return splash?.imageUpdate?.url?.hdpi
            densityDpi <= 480 -> return splash?.imageUpdate?.url?.xhdpi
            else -> return splash?.imageUpdate?.url?.xxxhdpi
        }
    }

    @JsonObject
    internal class Splash {
        @JsonField(name = arrayOf("imageUpdate"))
        var imageUpdate: ImageUpdate? = null
    }

    @JsonObject
    internal class ImageUpdate {
        @JsonField(name = arrayOf("updateTime"))
        var updateTime: String? = null

        @JsonField(name = arrayOf("url"))
        var url: ImgaeUrl? = null
    }

    @JsonObject
    internal class ImgaeUrl {
        @JsonField(name = arrayOf("hdpi"))
        var hdpi: String? = null

        @JsonField(name = arrayOf("xhdpi"))
        var xhdpi: String? = null

        @JsonField(name = arrayOf("xxxhdpi"))
        var xxxhdpi: String? = null
    }
}
