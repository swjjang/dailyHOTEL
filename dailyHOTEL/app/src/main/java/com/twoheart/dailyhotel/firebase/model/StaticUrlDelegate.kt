package com.twoheart.dailyhotel.firebase.model

import com.bluelinelabs.logansquare.LoganSquare
import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject


class StaticUrlDelegate(jsonString: String) {

    private val staticUrl: StaticUrl?

    init {
        staticUrl = LoganSquare.parse(jsonString, StaticUrl::class.java)
    }

    val privacy: String?
        get() {
            return staticUrl?.privacy
        }

    val collectPersonalInformation: String?
        get() {
            return staticUrl?.collectPersonalInformation
        }

    val terms: String?
        get() {
            return staticUrl?.terms
        }

    val about: String?
        get() {
            return staticUrl?.about
        }

    val location: String?
        get() {
            return staticUrl?.location
        }

    val childProtect: String?
        get() {
            return staticUrl?.childProtect
        }

    val bonus: String?
        get() {
            return staticUrl?.bonus
        }

    val coupon: String?
        get() {
            return staticUrl?.coupon
        }

    val prodCouponNote: String?
        get() {
            return staticUrl?.prodCouponNote
        }

    val devCouponNote: String?
        get() {
            return staticUrl?.devCouponNote
        }

    val faq: String?
        get() {
            return staticUrl?.faq
        }

    val license: String?
        get() {
            return staticUrl?.license
        }

    val review: String?
        get() {
            return staticUrl?.review
        }


    val lifeStyleProject: String?
        get() {
            return staticUrl?.lifeStyleProject
        }

    val dailyReward: String?
        get() {
            return staticUrl?.dailyReward
        }

    val dailyRewardTerms: String?
        get() {
            return staticUrl?.dailyRewardTerms
        }

    val dailyRewardCouponTerms: String?
        get() {
            return staticUrl?.dailyRewardCouponTerms
        }

    val dailyTrueAwards: String?
        get() {
            return staticUrl?.dailyTrueAwards
        }

    @JsonObject
    internal class StaticUrl {
        @JsonField(name = arrayOf("privacy"))
        var privacy: String? = null

        @JsonField(name = arrayOf("collectPersonalInformation"))
        var collectPersonalInformation: String? = null

        @JsonField(name = arrayOf("terms"))
        var terms: String? = null

        @JsonField(name = arrayOf("about"))
        var about: String? = null

        @JsonField(name = arrayOf("location"))
        var location: String? = null

        @JsonField(name = arrayOf("childProtect"))
        var childProtect: String? = null

        @JsonField(name = arrayOf("bonus"))
        var bonus: String? = null

        @JsonField(name = arrayOf("coupon"))
        var coupon: String? = null

        @JsonField(name = arrayOf("prodCouponNote"))
        var prodCouponNote: String? = null

        @JsonField(name = arrayOf("devCouponNote"))
        var devCouponNote: String? = null

        @JsonField(name = arrayOf("faq"))
        var faq: String? = null

        @JsonField(name = arrayOf("license"))
        var license: String? = null

        @JsonField(name = arrayOf("review"))
        var review: String? = null

        @JsonField(name = arrayOf("lifeStyleProject"))
        var lifeStyleProject: String? = null

        @JsonField(name = arrayOf("dailyReward"))
        var dailyReward: String? = null

        @JsonField(name = arrayOf("dailyRewardTerms"))
        var dailyRewardTerms: String? = null

        @JsonField(name = arrayOf("dailyRewardCouponTerms"))
        var dailyRewardCouponTerms: String? = null

        @JsonField(name = arrayOf("dailyTrueAwards"))
        var dailyTrueAwards: String? = null
    }
}
