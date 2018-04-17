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
        @JsonField(name = ["suggestHint"])
        var suggestHint: SuggestHint? = null

        @JsonField(name = ["gourmetRelatedKeywords"])
        var gourmetRelatedKeywords: List<String>? = null

        @JsonField(name = ["stayOutboundRelatedKeywords"])
        var stayOutboundRelatedKeywords: List<String>? = null
    }

    @JsonObject
    internal class SuggestHint {
        @JsonField(name = ["stay"])
        var stay: String? = null

        @JsonField(name = ["stayOutbound"])
        var stayOutbound: String? = null

        @JsonField(name = ["gourmet"])
        var gourmet: String? = null
    }
}
