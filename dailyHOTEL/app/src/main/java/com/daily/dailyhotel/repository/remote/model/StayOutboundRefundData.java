package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class StayOutboundRefundData
{
    @JsonField(name = "reservationIdx")
    public int reservationIdx;

    @JsonField(name = "message")
    public String message;

    public StayOutboundRefundData()
    {

    }
}
