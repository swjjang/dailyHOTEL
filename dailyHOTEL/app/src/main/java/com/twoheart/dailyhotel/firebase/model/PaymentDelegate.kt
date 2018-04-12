package com.twoheart.dailyhotel.firebase.model

import com.bluelinelabs.logansquare.LoganSquare
import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject


class PaymentDelegate(jsonString: String) {

    private val payment: Payment?

    init {
        payment = LoganSquare.parse(jsonString, Payment::class.java)
    }

    val stayEasyCard: Boolean
        get() {
            return payment?.paymentTypeEnabled?.stay?.easyCard ?: true
        }

    val stayCard: Boolean
        get() {
            return payment?.paymentTypeEnabled?.stay?.card ?: true
        }

    val stayPhone: Boolean
        get() {
            return payment?.paymentTypeEnabled?.stay?.phoneBill ?: true
        }

    val stayVirtual: Boolean
        get() {
            return payment?.paymentTypeEnabled?.stay?.virtualAccount ?: true
        }

    val stayOutboundEasyCard: Boolean
        get() {
            return payment?.paymentTypeEnabled?.stayOutbound?.easyCard ?: true
        }

    val stayOutboundCard: Boolean
        get() {
            return payment?.paymentTypeEnabled?.stayOutbound?.card ?: true
        }

    val stayOutboundPhone: Boolean
        get() {
            return payment?.paymentTypeEnabled?.stayOutbound?.phoneBill ?: true
        }

    val stayOutboundVirtual: Boolean
        get() {
            return payment?.paymentTypeEnabled?.stayOutbound?.virtualAccount ?: true
        }

    val gourmetEasyCard: Boolean
        get() {
            return payment?.paymentTypeEnabled?.gourmet?.easyCard ?: true
        }

    val gourmetCard: Boolean
        get() {
            return payment?.paymentTypeEnabled?.gourmet?.card ?: true
        }

    val gourmetPhone: Boolean
        get() {
            return payment?.paymentTypeEnabled?.gourmet?.phoneBill ?: true
        }

    val gourmetVirtual: Boolean
        get() {
            return payment?.paymentTypeEnabled?.gourmet?.virtualAccount ?: true
        }

    @JsonObject
    internal class Payment {
        @JsonField(name = arrayOf("paymentTypeEnabled"))
        var paymentTypeEnabled: PaymentTypeEnabled? = null
    }

    @JsonObject
    internal class PaymentTypeEnabled {
        @JsonField(name = arrayOf("stay"))
        var stay: PaymentType? = null

        @JsonField(name = arrayOf("gourmet"))
        var gourmet: PaymentType? = null

        @JsonField(name = arrayOf("stayOutbound"))
        var stayOutbound: PaymentType? = null
    }

    @JsonObject
    internal class PaymentType {
        @JsonField(name = arrayOf("easyCard"))
        var easyCard: Boolean? = null

        @JsonField(name = arrayOf("card"))
        var card: Boolean? = null

        @JsonField(name = arrayOf("phoneBill"))
        var phoneBill: Boolean? = null

        @JsonField(name = arrayOf("virtualAccount"))
        var virtualAccount: Boolean? = null
    }
}
