package com.twoheart.dailyhotel.firebase.model

import com.bluelinelabs.logansquare.LoganSquare
import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject


class ConfigDelegate(jsonString: String) {

    private val config: Config?

    init {
        config = LoganSquare.parse(jsonString, Config::class.java)
    }

    val boutiqueBusinessModelEnabled: Boolean
        get() {
            return config?.boutiqueBusinessModelEnabled ?: false
        }

    val operationLunchStartTime: String?
        get() {
            return config?.operationLunchTime?.startTime
        }

    val operationLunchEndTime: String?
        get() {
            return config?.operationLunchTime?.endTime
        }

    val stayDetailTrueReviewProductNameVisible: Boolean
        get() {
            return config?.detailTrueReviewProductNameVisible?.stay ?: true
        }

    val stayOutboundDetailTrueReviewProductNameVisible: Boolean
        get() {
            return config?.detailTrueReviewProductNameVisible?.stay ?: true
        }

    val gourmetDetailTrueReviewProductNameVisible: Boolean
        get() {
            return config?.detailTrueReviewProductNameVisible?.stay ?: true
        }

    @JsonObject
    internal class Config {
        @JsonField(name = arrayOf("boutiqueBusinessModelEnabled"))
        var boutiqueBusinessModelEnabled: Boolean? = null

        @JsonField(name = arrayOf("operationLunchTime"))
        var operationLunchTime: OperationLunchTime? = null

        @JsonField(name = arrayOf("detailTrueReviewProductNameVisible"))
        var detailTrueReviewProductNameVisible: DetailTrueReviewProductNameVisible? = null
    }

    @JsonObject
    internal class OperationLunchTime {
        @JsonField(name = arrayOf("startTime"))
        var startTime: String? = null

        @JsonField(name = arrayOf("endTime"))
        var endTime: String? = null
    }

    @JsonObject
    internal class DetailTrueReviewProductNameVisible {
        @JsonField(name = arrayOf("stay"))
        var stay: Boolean? = null

        @JsonField(name = arrayOf("stayOutbound"))
        var stayOutbound: Boolean? = null

        @JsonField(name = arrayOf("gourmet"))
        var gourmet: Boolean? = null
    }
}
