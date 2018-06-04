package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.StayRefundPolicy;

@JsonObject
public class StayRefundPolicyData
{
    @JsonField(name = "comment")
    public String comment;

    @JsonField(name = "refundPolicy")
    public String refundPolicy;


    public StayRefundPolicyData()
    {

    }

    public StayRefundPolicy getStayRefundPolicy()
    {
        StayRefundPolicy stayRefundPolicy = new StayRefundPolicy();

        stayRefundPolicy.comment = comment;
        stayRefundPolicy.refundPolicy = refundPolicy;

        return stayRefundPolicy;
    }
}
