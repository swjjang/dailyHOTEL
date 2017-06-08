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

    @JsonField(name = "checkInDate")
    public String checkInDate;

    @JsonField(name = "checkInTime")
    public String checkInTime;

    @JsonField(name = "checkOutDate")
    public String checkOutDate;

    @JsonField(name = "checkOutTime")
    public String checkOutTime;

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

    @JsonField(name = "readyForRefund")
    public boolean readyForRefund;

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

        //        stayOutboundBookingDetail.readyForRefund;
        stayOutboundBookingDetail.refundComment = cancelPolicyDescription;
        //        stayOutboundBookingDetail.refundPolicy;

        stayOutboundBookingDetail.checkInDate = checkInDate;
        stayOutboundBookingDetail.checkInTime = checkInTime;
        stayOutboundBookingDetail.checkOutDate = checkOutDate;
        stayOutboundBookingDetail.checkOutTime = checkOutTime;
        stayOutboundBookingDetail.aggregationId = aggregationId;
        stayOutboundBookingDetail.paymentType = StayOutboundBookingDetail.PaymentType.valueOf(paymentType);
        stayOutboundBookingDetail.paymentDate = paymentDate;

        return stayOutboundBookingDetail;
    }
}
