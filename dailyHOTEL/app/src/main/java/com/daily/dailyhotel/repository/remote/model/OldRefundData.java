package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.OldRefund;

/**
 * Created by android_sam on 2018. 1. 12..
 */
@JsonObject
public class OldRefundData
{
    @JsonField(name = "failRefund")
    public boolean failRefund; // "failRefund": true,

    @JsonField(name = "messageFromPg")
    public String messageFromPg; // "messageFromPg": "string",

    @JsonField(name = "readyForRefund")
    public boolean readyForRefund; // "readyForRefund": true

    public OldRefund getOldRefund()
    {
        OldRefund oldRefund = new OldRefund();

        oldRefund.failRefund = this.failRefund;
        oldRefund.messageFromPg = this.messageFromPg;
        oldRefund.readyForRefund = this.readyForRefund;

        return oldRefund;
    }
}
