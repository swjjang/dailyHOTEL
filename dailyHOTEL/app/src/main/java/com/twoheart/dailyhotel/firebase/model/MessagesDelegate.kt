package com.twoheart.dailyhotel.firebase.model

import com.bluelinelabs.logansquare.LoganSquare
import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject


class MessagesDelegate(jsonString: String) {

    private val messages: Messages?

    init {
        messages = LoganSquare.parse(jsonString, Messages::class.java)
    }

    val loginText01: String?
        get() {
            return messages?.login?.text01
        }

    val signupText01: String?
        get() {
            return messages?.signup?.text01
        }

    val signupText02: String?
        get() {
            return messages?.signup?.text02
        }


    val homeMessageAreaLoginEnabled: Boolean
        get() {
            return messages?.home?.messageArea?.login?.enabled ?: true
        }

    val homeMessageAreaLogoutEnabled: Boolean
        get() {
            return messages?.home?.messageArea?.logout?.enabled ?: true
        }

    val homeMessageAreaLogoutTitle: String?
        get() {
            return messages?.home?.messageArea?.logout?.title
        }

    val homeMessageAreaLogoutCallToAction: String?
        get() {
            return messages?.home?.messageArea?.logout?.callToAction
        }

    val homeCategoryAreaEnabled: Boolean
        get() {
            return messages?.home?.categoryArea?.enabled ?: true
        }

    @JsonObject
    internal class Messages {
        @JsonField(name = arrayOf("login"))
        var login: Login? = null

        @JsonField(name = arrayOf("signup"))
        var signup: Signup? = null

        @JsonField(name = arrayOf("home"))
        var home: Home? = null
    }

    @JsonObject
    internal class Login {
        @JsonField(name = arrayOf("text01"))
        var text01: String? = null
    }

    @JsonObject
    internal class Signup {
        @JsonField(name = arrayOf("text01"))
        var text01: String? = null

        @JsonField(name = arrayOf("text02"))
        var text02: String? = null
    }

    @JsonObject
    internal class Home {
        @JsonField(name = arrayOf("messageArea"))
        var messageArea: MessageArea? = null

        @JsonField(name = arrayOf("categoryArea"))
        var categoryArea: CategoryArea? = null
    }

    @JsonObject
    internal class MessageArea {
        @JsonField(name = arrayOf("login"))
        var login: Login? = null

        @JsonField(name = arrayOf("logout"))
        var logout: Logout? = null

        @JsonObject
        internal class Login {
            @JsonField(name = arrayOf("enabled"))
            var enabled: Boolean? = null
        }

        @JsonObject
        internal class Logout {
            @JsonField(name = arrayOf("enabled"))
            var enabled: Boolean? = null

            @JsonField(name = arrayOf("title"))
            var title: String? = null

            @JsonField(name = arrayOf("callToAction"))
            var callToAction: String? = null
        }
    }

    @JsonObject
    internal class CategoryArea {
        @JsonField(name = arrayOf("enabled"))
        var enabled: Boolean? = null
    }
}
