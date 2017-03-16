package com.twoheart.dailyhotel.network.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class PlaceReview
{
    @JsonField
    public String email;

    @JsonField
    public String date; // ISO-8601

    @JsonField
    public String message;

    public PlaceReview()
    {
    }
}
