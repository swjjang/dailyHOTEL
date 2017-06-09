package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayOutboundBookingDetail;

import java.util.ArrayList;

@JsonObject
public class StayOutboundBookingDetailData
{
    @JsonField(name = "reservationIdx")
    public int reservationIdx;

    @JsonField(name = "hotelId")
    public int hotelId;

    @JsonField(name = "hotelName")
    public String hotelName;

    @JsonField(name = "hotelAddress")
    public String hotelAddress;

    @JsonField(name = "checkinDate")
    public String checkinDate;

    @JsonField(name = "checkinTime")
    public String checkinTime;

    @JsonField(name = "checkoutDate")
    public String checkoutDate;

    @JsonField(name = "checkoutTime")
    public String checkoutTime;

    @JsonField(name = "latitude")
    public double latitude;

    @JsonField(name = "longitude")
    public double longitude;

    @JsonField(name = "guestEmail")
    public String guestEmail;

    @JsonField(name = "guestFirstName")
    public String guestFirstName;

    @JsonField(name = "guestLastName")
    public String guestLastName;

    @JsonField(name = "guestPhone")
    public String guestPhone;

    @JsonField(name = "roomName")
    public String roomName;

    @JsonField(name = "numberOfAdults")
    public int numberOfAdults;

    @JsonField(name = "numberOfChildren")
    public int numberOfChildren;

    @JsonField(name = "childrenAges")
    public ArrayList<Integer> childrenAges;

    @JsonField(name = "refundStatus")
    public String refundStatus;

    @JsonField(name = "aggregationId")
    public String aggregationId;

    @JsonField(name = "cancelPolicyDescription")
    public String cancelPolicyDescription;

    @JsonField(name = "paymentType")
    public String paymentType;

    @JsonField(name = "paymentDate")
    public String paymentDate;

    @JsonField(name = "total")
    public int total;

    @JsonField(name = "paymentAmount")
    public int paymentAmount;

    @JsonField(name = "bonus")
    public int bonus;

    @JsonField(name = "fee")
    public double fee;

    public StayOutboundBookingDetailData()
    {

    }

    public StayOutboundBookingDetail getStayOutboundBookingDetail()
    {
        StayOutboundBookingDetail stayOutboundBookingDetail = new StayOutboundBookingDetail();

        stayOutboundBookingDetail.stayIndex = hotelId;
        stayOutboundBookingDetail.bookingIndex = reservationIdx;
        stayOutboundBookingDetail.name = hotelName;
        stayOutboundBookingDetail.roomName = roomName;
        stayOutboundBookingDetail.address = hotelAddress;
        stayOutboundBookingDetail.guestFirstName = guestFirstName;
        stayOutboundBookingDetail.guestLastName = guestLastName;
        stayOutboundBookingDetail.guestEmail = guestEmail;
        stayOutboundBookingDetail.guestPhone = guestPhone;
        stayOutboundBookingDetail.latitude = latitude;
        stayOutboundBookingDetail.longitude = longitude;
        stayOutboundBookingDetail.paymentPrice = paymentAmount;
        stayOutboundBookingDetail.bonus = bonus;
        stayOutboundBookingDetail.totalPrice = total;
        stayOutboundBookingDetail.fee = fee;
        stayOutboundBookingDetail.setPeople(new People(numberOfAdults, childrenAges));
        stayOutboundBookingDetail.refundStatus = StayOutboundBookingDetail.RefundType.valueOf(refundStatus);
        stayOutboundBookingDetail.refundPolicy = cancelPolicyDescription;
        stayOutboundBookingDetail.checkInDate = checkinDate;
        stayOutboundBookingDetail.checkInTime = checkinTime;
        stayOutboundBookingDetail.checkOutDate = checkoutDate;
        stayOutboundBookingDetail.checkOutTime = checkoutTime;
        stayOutboundBookingDetail.aggregationId = aggregationId;
        stayOutboundBookingDetail.paymentType = StayOutboundBookingDetail.PaymentType.valueOf(paymentType);
        stayOutboundBookingDetail.paymentDate = paymentDate;

        return stayOutboundBookingDetail;
    }
}
