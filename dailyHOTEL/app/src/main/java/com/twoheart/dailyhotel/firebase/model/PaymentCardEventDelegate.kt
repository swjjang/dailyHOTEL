package com.twoheart.dailyhotel.firebase.model

import com.bluelinelabs.logansquare.LoganSquare
import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject
import com.twoheart.dailyhotel.Setting


class PaymentCardEventDelegate(jsonString: String) {

    private val version: Version?

    init {
        version = LoganSquare.parse(jsonString, Version::class.java)
    }

    val optional: String?
        get() {
            when (Setting.getStore()) {
                Setting.Stores.PLAY_STORE -> return version?.stores?.play?.versionCode?.optional

                Setting.Stores.T_STORE -> return version?.stores?.one?.versionCode?.optional

                else -> return null
            }
        }

    val force: String?
        get() {
            when (Setting.getStore()) {
                Setting.Stores.PLAY_STORE -> return version?.stores?.play?.versionCode?.force

                Setting.Stores.T_STORE -> return version?.stores?.one?.versionCode?.force

                else -> return null
            }
        }

    val optionalMessage: Pair<String?, String?>
        get() {
            return Pair(version?.messages?.optional?.title, version?.messages?.optional?.message)
        }

    val forceMessage: Pair<String?, String?>
        get() {
            return Pair(version?.messages?.force?.title, version?.messages?.force?.message)
        }

    @JsonObject
    internal class Version {
        @JsonField(name = arrayOf("enabled"))
        var enabled: Boolean? = null

        @JsonField(name = arrayOf("events"))
        var events: List<Event>? = null
    }


    @JsonObject
    internal class Event {
        @JsonField(name = arrayOf("enabled"))
        var enabled: Boolean? = null

        @JsonField(name = arrayOf("startDateTime"))
        var startDateTime: String? = null

        @JsonField(name = arrayOf("endDateTime"))
        var endDateTime: String? = null

        @JsonField(name = arrayOf("title"))
        var title: String? = null

        @JsonField(name = arrayOf("messages"))
        var messages: List<String>? = null
    }
}
