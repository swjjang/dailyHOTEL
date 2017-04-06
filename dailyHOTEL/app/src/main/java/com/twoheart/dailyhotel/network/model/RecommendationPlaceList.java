package com.twoheart.dailyhotel.network.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

@JsonObject
public class RecommendationPlaceList<E>
{
    @JsonField(name = "recommendation")
    public Recommendation recommendation;

    @JsonField(name = "items")
    public List<E> items;

    @JsonField(name = "imgUrl")
    public String imageBaseUrl;
}
