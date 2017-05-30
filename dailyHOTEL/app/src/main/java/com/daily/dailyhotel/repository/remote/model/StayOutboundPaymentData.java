package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.StayOutboundPayment;

@JsonObject
public class StayOutboundPaymentData
{
    @JsonField(name = "hotelId")
    public int hotelId;

    @JsonField(name = "availableRooms")
    public int availableRooms;

    @JsonField(name = "checkinDate")
    public String checkinDate;

    @JsonField(name = "checkinTime")
    public String checkinTime;

    @JsonField(name = "checkoutDate")
    public String checkoutDate;

    @JsonField(name = "checkoutTime")
    public String checkoutTime;

    @JsonField(name = "nonRefundable")
    public boolean nonRefundable;

    @JsonField(name = "nonRefundableDescription")
    public String nonRefundableDescription;

    @JsonField(name = "total")
    public int total;

    public StayOutboundPaymentData()
    {

    }

    public StayOutboundPayment getStayOutboundPayment()
    {
        StayOutboundPayment stayOutboundPayment = new StayOutboundPayment();


        return stayOutboundPayment;
    }
}
