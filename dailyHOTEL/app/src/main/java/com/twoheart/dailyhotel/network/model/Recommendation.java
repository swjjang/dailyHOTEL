package com.twoheart.dailyhotel.network.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by android_sam on 2017. 1. 31..
 */
@JsonObject
public class Recommendation
{
    @JsonField(name = "idx")
    public int idx;

    @JsonField(name = "serviceType")
    public String serviceType;

    @JsonField(name = "title")
    public String title;

    @JsonField(name = "subtitle")
    public String subtitle;

    @JsonField(name = "defaultImageUrl")
    public String defaultImageUrl;

    @JsonField(name = "lowResolutionImageUrl")
    public String lowResolutionImageUrl;

    @JsonField(name = "linkUrl")
    public String linkUrl;

    @JsonField(name = "startedAt")
    public String startedAt; // ISO-8601

    @JsonField(name = "endedAt")
    public String endedAt; // ISO-8601

    @JsonField(name = "countOfItems")
    public int countOfItems;
}
