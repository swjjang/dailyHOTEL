package com.twoheart.dailyhotel.firebase.model

import com.bluelinelabs.logansquare.LoganSquare
import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject


class CompanyDelegate(jsonString: String) {

    private val company: Company?

    init {
        company = LoganSquare.parse(jsonString, Company::class.java)
    }

    val name: String?
        get() {
            return company?.name
        }

    val ceo: String?
        get() {
            return company?.ceo
        }

    val bizRegNumber: String?
        get() {
            return company?.bizRegNumber
        }

    val itcRegNumber: String?
        get() {
            return company?.itcRegNumber
        }

    val address1: String?
        get() {
            return company?.address1
        }

    val phoneNumber1: String?
        get() {
            return company?.phoneNumber1
        }

    val fax1: String?
        get() {
            return company?.fax1
        }

    val privacyManager: String?
        get() {
            return company?.privacyManager
        }

    @JsonObject
    internal class Company {
        @JsonField(name = ["name"])
        var name: String? = null

        @JsonField(name = ["ceo"])
        var ceo: String? = null

        @JsonField(name = ["bizRegNumber"])
        var bizRegNumber: String? = null

        @JsonField(name = ["itcRegNumber"])
        var itcRegNumber: String? = null

        @JsonField(name = ["address1"])
        var address1: String? = null

        @JsonField(name = ["phoneNumber1"])
        var phoneNumber1: String? = null

        @JsonField(name = ["fax1"])
        var fax1: String? = null

        @JsonField(name = ["privacyManager"])
        var privacyManager: String? = null
    }
}
