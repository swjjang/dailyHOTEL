package com.twoheart.dailyhotel.network.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class StampHistory
{
    @JsonField(name = "idx")
    public String idx;

    @JsonField(name = "reservationName")
    public String reservationName;

    @JsonField(name = "publishedAt")
    public String publishedAt; // yyyy-MM-dd

    @JsonField(name = "reservationIdx")
    public int reservationIdx;

    public StampHistory()
    {
    }
}
