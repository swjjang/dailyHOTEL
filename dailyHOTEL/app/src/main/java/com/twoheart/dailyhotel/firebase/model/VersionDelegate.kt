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
            return when (Setting.getStore()) {
                Setting.Stores.PLAY_STORE -> version?.stores?.play?.versionCode?.optional

                Setting.Stores.T_STORE -> version?.stores?.one?.versionCode?.optional

                else -> null
            }
        }

    val force: String?
        get() {
            return when (Setting.getStore()) {
                Setting.Stores.PLAY_STORE -> version?.stores?.play?.versionCode?.force

                Setting.Stores.T_STORE -> version?.stores?.one?.versionCode?.force

                else -> null
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
        @JsonField(name = ["stores"])
        var stores: Stores? = null

        @JsonField(name = ["messages"])
        var messages: Messages? = null
    }


    @JsonObject
    internal class Stores {
        @JsonField(name = ["play"])
        var play: Store? = null

        @JsonField(name = ["one"])
        var one: Store? = null
    }

    @JsonObject
    internal class Store {
        @JsonField(name = ["versionCode"])
        var versionCode: VersionCode? = null
    }

    @JsonObject
    internal class VersionCode {
        @JsonField(name = ["optional"])
        var optional: String? = null

        @JsonField(name = ["force"])
        var force: String? = null
    }


    @JsonObject
    internal class Messages {
        @JsonField(name = ["optional"])
        var optional: Message? = null

        @JsonField(name = ["force"])
        var force: Message? = null
    }

    @JsonObject
    internal class Message {
        @JsonField(name = ["title"])
        var title: String? = null

        @JsonField(name = ["message"])
        var message: String? = null
    }
}
