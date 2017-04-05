package com.twoheart.dailyhotel.network.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class PlaceReview
{
    @JsonField(name = "userId")
    public String email;

    @JsonField(name = "comment")
    public String comment;

    @JsonField(name = "createdAt")
    public String createdAt; // ISO-8601

    public PlaceReview()
    {
    }
}
