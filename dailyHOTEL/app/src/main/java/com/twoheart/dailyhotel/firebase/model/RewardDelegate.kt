package com.twoheart.dailyhotel.firebase.model

import com.bluelinelabs.logansquare.LoganSquare
import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject
import org.json.JSONArray
import org.json.JSONObject


class RewardDelegate(jsonString: String) {

    private val reward: Reward?

    init {
        reward = LoganSquare.parse(jsonString, Reward::class.java)
    }

    val cardTitleMessage: String?
        get() {
            return reward?.cardTitleMessage
        }

    val rewardTitleMessage: String?
        get() {
            return reward?.rewardTitleMessage
        }

    val campaignEnabled: Boolean
        get() {
            return reward?.campaignEnabled ?: false
        }

    val nonMemberMessageDefault: String?
        get() {
            return reward?.nonMember?.message?.defaultMessage
        }

    val nonMemberMessageCampaign: String?
        get() {
            return reward?.nonMember?.message?.campaign
        }

    val nonMemberCampaignFreeNights: Int
        get() {
            return reward?.nonMember?.campaignFreeNights ?: 0
        }

    val memberMessagesNights: Array<String?>
        get() {
            return arrayOf(reward?.member?.messages?.nights0,
                    reward?.member?.messages?.nights1,
                    reward?.member?.messages?.nights2,
                    reward?.member?.messages?.nights3,
                    reward?.member?.messages?.nights4,
                    reward?.member?.messages?.nights5,
                    reward?.member?.messages?.nights6,
                    reward?.member?.messages?.nights7,
                    reward?.member?.messages?.nights8,
                    reward?.member?.messages?.nights9)
        }

    val guides: String?
        get() {
            val jsonArray = JSONArray()

            reward?.guides?.forEach {
                val jsonObject = JSONObject()
                jsonObject.put("titleMessage", it.titleMessage)
                jsonObject.put("descriptionMessage", it.descriptionMessage)
                jsonArray.put(jsonObject)
            }

            return jsonArray.toString()
        }


    @JsonObject
    internal class Reward {
        @JsonField(name = arrayOf("cardTitleMessage"))
        var cardTitleMessage: String? = null

        @JsonField(name = arrayOf("rewardTitleMessage"))
        var rewardTitleMessage: String? = null

        @JsonField(name = arrayOf("campaignEnabled"))
        var campaignEnabled: Boolean? = null

        @JsonField(name = arrayOf("nonMember"))
        var nonMember: NonMember? = null

        @JsonField(name = arrayOf("member"))
        var member: Member? = null

        @JsonField(name = arrayOf("guides"))
        var guides: List<Guide>? = null
    }

    @JsonObject
    internal class NonMember {
        @JsonField(name = arrayOf("message"))
        var message: Message? = null

        @JsonField(name = arrayOf("campaignFreeNights"))
        var campaignFreeNights: Int? = null

        @JsonObject
        internal class Message {
            @JsonField(name = arrayOf("campaign"))
            var campaign: String? = null

            @JsonField(name = arrayOf("default"))
            var defaultMessage: String? = null // default 라는 변수명을 사용할수가 없다.
        }
    }

    @JsonObject
    internal class Member {
        @JsonField(name = arrayOf("messages"))
        var messages: Messages? = null

        @JsonObject
        internal class Messages {
            @JsonField(name = arrayOf("0"))
            var nights0: String? = null

            @JsonField(name = arrayOf("1"))
            var nights1: String? = null

            @JsonField(name = arrayOf("2"))
            var nights2: String? = null

            @JsonField(name = arrayOf("3"))
            var nights3: String? = null

            @JsonField(name = arrayOf("4"))
            var nights4: String? = null

            @JsonField(name = arrayOf("5"))
            var nights5: String? = null

            @JsonField(name = arrayOf("6"))
            var nights6: String? = null

            @JsonField(name = arrayOf("7"))
            var nights7: String? = null

            @JsonField(name = arrayOf("8"))
            var nights8: String? = null

            @JsonField(name = arrayOf("9"))
            var nights9: String? = null
        }
    }

    @JsonObject
    internal class Guide {
        @JsonField(name = arrayOf("titleMessage"))
        var titleMessage: String? = null

        @JsonField(name = arrayOf("descriptionMessage"))
        var descriptionMessage: String? = null
    }
}
