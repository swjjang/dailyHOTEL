package com.twoheart.dailyhotel.network.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.Map;

@JsonObject
public abstract class RecommendationPlace
{
    @JsonField
    public String name;

    @JsonField
    public String addrSummary;

    @JsonField
    public String category;

    @JsonField
    public int discount;

    @JsonField
    public int price;

    @JsonField
    public Map<String, Object> imgPathMain;

    @JsonField
    public double latitude;

    @JsonField
    public double longitude;

    @JsonField
    public String districtName;

    @JsonField
    public int rating;

    @JsonField
    public String benefit;

    @JsonField
    public boolean isSoldOut;

    public String imageUrl;
}
