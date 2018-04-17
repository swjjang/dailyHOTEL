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
        @JsonField(name = ["login"])
        var login: Login? = null

        @JsonField(name = ["signup"])
        var signup: Signup? = null

        @JsonField(name = ["home"])
        var home: Home? = null
    }

    @JsonObject
    internal class Login {
        @JsonField(name = ["text01"])
        var text01: String? = null
    }

    @JsonObject
    internal class Signup {
        @JsonField(name = ["text01"])
        var text01: String? = null

        @JsonField(name = ["text02"])
        var text02: String? = null
    }

    @JsonObject
    internal class Home {
        @JsonField(name = ["messageArea"])
        var messageArea: MessageArea? = null

        @JsonField(name = ["categoryArea"])
        var categoryArea: CategoryArea? = null
    }

    @JsonObject
    internal class MessageArea {
        @JsonField(name = ["login"])
        var login: Login? = null

        @JsonField(name = ["logout"])
        var logout: Logout? = null

        @JsonObject
        internal class Login {
            @JsonField(name = ["enabled"])
            var enabled: Boolean? = null
        }

        @JsonObject
        internal class Logout {
            @JsonField(name = ["enabled"])
            var enabled: Boolean? = null

            @JsonField(name = ["title"])
            var title: String? = null

            @JsonField(name = ["callToAction"])
            var callToAction: String? = null
        }
    }

    @JsonObject
    internal class CategoryArea {
        @JsonField(name = ["enabled"])
        var enabled: Boolean? = null
    }
}
