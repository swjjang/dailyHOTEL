package com.twoheart.dailyhotel.firebase.model

import com.bluelinelabs.logansquare.LoganSquare
import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject
import com.twoheart.dailyhotel.Setting


class VersionDelegate(jsonString: String) {

    private val updateVersion: UpdateVersion?

    init {
        updateVersion = LoganSquare.parse(jsonString, UpdateVersion::class.java)
    }

    val current: String?
        get() {
            when (Setting.getStore()) {
                Setting.Stores.PLAY_STORE -> return updateVersion?.versionCode?.play?.current

                Setting.Stores.T_STORE -> return updateVersion?.versionCode?.one?.current

                else -> return null
            }
        }

    val force: String?
        get() {
            when (Setting.getStore()) {
                Setting.Stores.PLAY_STORE -> return updateVersion?.versionCode?.play?.force

                Setting.Stores.T_STORE -> return updateVersion?.versionCode?.one?.force

                else -> return null
            }
        }

    @JsonObject
    internal class UpdateVersion {
        @JsonField(name = arrayOf("versionCode"))
        var versionCode: VersionCode? = null
    }

    @JsonObject
    internal class VersionCode {
        @JsonField(name = arrayOf("play"))
        var play: Store? = null

        @JsonField(name = arrayOf("one"))
        var one: Store? = null
    }

    @JsonObject
    internal class Store {
        @JsonField(name = arrayOf("current"))
        var current: String? = null

        @JsonField(name = arrayOf("force"))
        var force: String? = null
    }
}
