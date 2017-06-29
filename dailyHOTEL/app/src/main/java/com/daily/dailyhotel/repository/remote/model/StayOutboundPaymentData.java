package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.StayOutboundPayment;

import java.util.List;

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

    @JsonField(name = "cancelPolicyDescriptions")
    public List<String> cancelPolicyDescriptions;

    @JsonField(name = "total")
    public int total;

    @JsonField(name = "totalUsd")
    public double totalUsd;

    @JsonField(name = "feeTotalAmountUsd")
    public double feeTotalAmountUsd;

    @JsonField(name = "rateKey")
    public String rateKey;

    @JsonField(name = "roomTypeCode")
    public String roomTypeCode;

    @JsonField(name = "rateCode")
    public String rateCode;

    @JsonField(name = "roomBedTypeId")
    public int roomBedTypeId;

    public StayOutboundPaymentData()
    {

    }

    public StayOutboundPayment getStayOutboundPayment()
    {
        StayOutboundPayment stayOutboundPayment = new StayOutboundPayment();

        stayOutboundPayment.stayIndex = hotelId;
        stayOutboundPayment.availableRooms = availableRooms;
        stayOutboundPayment.checkInDate = checkinDate;
        stayOutboundPayment.checkInTime = checkinTime;
        stayOutboundPayment.checkOutDate = checkoutDate;
        stayOutboundPayment.checkOutTime = checkoutTime;
        stayOutboundPayment.nonRefundable = nonRefundable;
        stayOutboundPayment.setRefundPolicyList(cancelPolicyDescriptions);
        stayOutboundPayment.totalPrice = total;
        stayOutboundPayment.feeTotalAmountUsd = feeTotalAmountUsd;
        stayOutboundPayment.rateKey = rateKey;
        stayOutboundPayment.roomTypeCode = roomTypeCode;
        stayOutboundPayment.rateCode = rateCode;
        stayOutboundPayment.roomBedTypeId = roomBedTypeId;

        return stayOutboundPayment;
    }
}
