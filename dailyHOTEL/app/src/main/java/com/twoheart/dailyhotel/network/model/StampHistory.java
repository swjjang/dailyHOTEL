package com.twoheart.dailyhotel.network.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class StampHistory
{
    @JsonField
    public String idx;

    @JsonField
    public String reservationName;

    @JsonField
    public String publishedAt; // yyyy-MM-dd

    @JsonField
    public int reservationIdx;

    public StampHistory()
    {
    }
}
