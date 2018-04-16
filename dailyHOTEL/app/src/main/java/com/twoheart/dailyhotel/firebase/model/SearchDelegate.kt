package com.twoheart.dailyhotel.firebase.model

import com.bluelinelabs.logansquare.LoganSquare
import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject
import org.json.JSONArray


class SearchDelegate(jsonString: String) {

    private val search: Search?

    init {
        search = LoganSquare.parse(jsonString, Search::class.java)
    }

    val suggestHintStay: String?
        get() {
            return search?.suggestHint?.stay
        }

    val suggestHintStayOutbound: String?
        get() {
            return search?.suggestHint?.stayOutbound
        }

    val suggestHintGourmet: String?
        get() {
            return search?.suggestHint?.gourmet
        }

    val gourmetRelatedKeywords: String?
        get() {

            val jsonArray = JSONArray()

            search?.gourmetRelatedKeywords?.forEach { jsonArray.put(it) }

            return jsonArray.toString()
        }

    val stayOutboundRelatedKeywords: String?
        get() {

            val jsonArray = JSONArray()

            search?.stayOutboundRelatedKeywords?.forEach { jsonArray.put(it) }

            return jsonArray.toString()
        }


    @JsonObject
    internal class Search {
        @JsonField(name = arrayOf("suggestHint"))
        var suggestHint: SuggestHint? = null

        @JsonField(name = arrayOf("gourmetRelatedKeywords"))
        var gourmetRelatedKeywords: List<String>? = null

        @JsonField(name = arrayOf("stayOutboundRelatedKeywords"))
        var stayOutboundRelatedKeywords: List<String>? = null
    }

    @JsonObject
    internal class SuggestHint {
        @JsonField(name = arrayOf("stay"))
        var stay: String? = null

        @JsonField(name = arrayOf("stayOutbound"))
        var stayOutbound: String? = null

        @JsonField(name = arrayOf("gourmet"))
        var gourmet: String? = null
    }
}
