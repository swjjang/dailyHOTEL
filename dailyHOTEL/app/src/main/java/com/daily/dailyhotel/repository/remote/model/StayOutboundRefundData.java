package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.BookingHide;
import com.daily.dailyhotel.entity.StayOutboundRefund;

@JsonObject
public class StayOutboundRefundData
{
    @JsonField(name = "reservationIdx")
    public int reservationIdx;

    public StayOutboundRefundData()
    {

    }

    public StayOutboundRefund getStayOutboundRefund()
    {
        StayOutboundRefund stayOutboundRefund = new StayOutboundRefund();

        return stayOutboundRefund;
    }
}
