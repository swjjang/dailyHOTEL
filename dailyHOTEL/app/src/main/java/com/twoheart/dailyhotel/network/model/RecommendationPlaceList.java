package com.twoheart.dailyhotel.network.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.repository.remote.model.ConfigurationsData;

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

    @JsonField(name = "stickers")
    public List<Sticker> stickers;

    @JsonField(name = "configurations")
    public ConfigurationsData configurations;
}
