package com.twoheart.dailyhotel.network.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class PlaceReviewProgress
{
    @JsonField
    public String name;

    @JsonField
    public int value;

    public PlaceReviewProgress()
    {
    }

    public PlaceReviewProgress(String name, int value)
    {
        this.name = name;
        this.value = value;
    }
}
