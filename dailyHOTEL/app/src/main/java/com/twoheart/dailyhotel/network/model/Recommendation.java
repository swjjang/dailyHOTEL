package com.twoheart.dailyhotel.network.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by android_sam on 2017. 1. 31..
 */
@JsonObject
public class Recommendation
{
    @JsonField
    public int idx;

    @JsonField
    public String serviceType;

    @JsonField
    public String title;

    @JsonField
    public String subtitle;

    @JsonField
    public String defaultImageUrl;

    @JsonField
    public String lowResolutionImageUrl;

    @JsonField
    public String linkUrl;

    @JsonField
    public String startedAt; // ISO-8601

    @JsonField
    public String endedAt; // ISO-8601

    @JsonField
    public int countOfItems;
}
