package com.twoheart.dailyhotel.network.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class StampHistory
{
    @JsonField
    public String placeName;

    @JsonField
    public String date; // ISO-8601

    @JsonField
    public int nights;

    @JsonField
    public int reservationIndex;

    public StampHistory()
    {
    }


    public StampHistory(String placeName, String date, int nights)
    {
        this.placeName = placeName;
        this.date = date;
        this.nights = nights;
    }
}
