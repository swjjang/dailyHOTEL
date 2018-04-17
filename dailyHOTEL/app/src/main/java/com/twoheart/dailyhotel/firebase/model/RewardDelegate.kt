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
        @JsonField(name = ["cardTitleMessage"])
        var cardTitleMessage: String? = null

        @JsonField(name = ["rewardTitleMessage"])
        var rewardTitleMessage: String? = null

        @JsonField(name = ["campaignEnabled"])
        var campaignEnabled: Boolean? = null

        @JsonField(name = ["nonMember"])
        var nonMember: NonMember? = null

        @JsonField(name = ["member"])
        var member: Member? = null

        @JsonField(name = ["guides"])
        var guides: List<Guide>? = null
    }

    @JsonObject
    internal class NonMember {
        @JsonField(name = ["message"])
        var message: Message? = null

        @JsonField(name = ["campaignFreeNights"])
        var campaignFreeNights: Int? = null

        @JsonObject
        internal class Message {
            @JsonField(name = ["campaign"])
            var campaign: String? = null

            @JsonField(name = ["default"])
            var defaultMessage: String? = null // default 라는 변수명을 사용할수가 없다.
        }
    }

    @JsonObject
    internal class Member {
        @JsonField(name = ["messages"])
        var messages: Messages? = null

        @JsonObject
        internal class Messages {
            @JsonField(name = ["0"])
            var nights0: String? = null

            @JsonField(name = ["1"])
            var nights1: String? = null

            @JsonField(name = ["2"])
            var nights2: String? = null

            @JsonField(name = ["3"])
            var nights3: String? = null

            @JsonField(name = ["4"])
            var nights4: String? = null

            @JsonField(name = ["5"])
            var nights5: String? = null

            @JsonField(name = ["6"])
            var nights6: String? = null

            @JsonField(name = ["7"])
            var nights7: String? = null

            @JsonField(name = ["8"])
            var nights8: String? = null

            @JsonField(name = ["9"])
            var nights9: String? = null
        }
    }

    @JsonObject
    internal class Guide {
        @JsonField(name = ["titleMessage"])
        var titleMessage: String? = null

        @JsonField(name = ["descriptionMessage"])
        var descriptionMessage: String? = null
    }
}
