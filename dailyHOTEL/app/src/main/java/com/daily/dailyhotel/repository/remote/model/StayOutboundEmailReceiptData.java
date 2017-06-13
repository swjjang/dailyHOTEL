package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.StayOutboundRefund;

@JsonObject
public class StayOutboundEmailReceiptData
{
    @JsonField(name = "reservationIdx")
    public int reservationIdx;

    public StayOutboundEmailReceiptData()
    {

    }

    public StayOutboundRefund getStayOutboundRefund()
    {
        StayOutboundRefund stayOutboundRefund = new StayOutboundRefund();

        return stayOutboundRefund;
    }
}
