package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class StayFilterCountData
{
    @JsonField(name = "hotelSalesCount")
    public int count;
}
