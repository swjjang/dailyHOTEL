package com.twoheart.dailyhotel.firebase.model

import com.bluelinelabs.logansquare.LoganSquare
import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject
import com.twoheart.dailyhotel.Setting


class VersionDelegate(jsonString: String) {

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
        @JsonField(name = arrayOf("stores"))
        var stores: Stores? = null

        @JsonField(name = arrayOf("messages"))
        var messages: Messages? = null
    }


    @JsonObject
    internal class Stores {
        @JsonField(name = arrayOf("play"))
        var play: Store? = null

        @JsonField(name = arrayOf("one"))
        var one: Store? = null
    }

    @JsonObject
    internal class Store {
        @JsonField(name = arrayOf("versionCode"))
        var versionCode: VersionCode? = null
    }

    @JsonObject
    internal class VersionCode {
        @JsonField(name = arrayOf("optional"))
        var optional: String? = null

        @JsonField(name = arrayOf("force"))
        var force: String? = null
    }


    @JsonObject
    internal class Messages {
        @JsonField(name = arrayOf("optional"))
        var optional: Message? = null

        @JsonField(name = arrayOf("force"))
        var force: Message? = null
    }

    @JsonObject
    internal class Message {
        @JsonField(name = arrayOf("title"))
        var title: String? = null

        @JsonField(name = arrayOf("message"))
        var message: String? = null
    }
}
