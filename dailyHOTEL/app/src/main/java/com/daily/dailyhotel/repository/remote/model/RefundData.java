package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class RefundData
{
    @JsonField(name = "failRefund")
    public boolean failRefund;

    @JsonField(name = "messageFromPg")
    public String messageFromPg;

    @JsonField(name = "readyForRefund")
    public boolean readyForRefund;

    public RefundData()
    {

    }
}