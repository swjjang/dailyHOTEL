package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.StayOutboundBookingDetail;

import java.util.List;

@JsonObject
public class StayOutboundBookingDetailData
{
    @JsonField(name = "reservationIdx")
    public int reservationIdx;

    @JsonField(name = "hotelId")
    public int hotelId;

    @JsonField(name = "hotelName")
    public String hotelName;

    @JsonField(name = "nameEng")
    public String nameEng;

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

    @JsonField(name = "fee")
    public double fee;

    @JsonField(name = "numberOfAdults")
    public int numberOfAdults;

    @JsonField(name = "numberOfChildren")
    public List<Integer> numberOfChildren;

    @JsonField(name = "readyForRefund")
    public boolean readyForRefund;

    public StayOutboundBookingDetailData()
    {

    }

    public StayOutboundBookingDetail getStayOutboundBookingDetail()
    {
        StayOutboundBookingDetail stayOutboundBookingDetail = new StayOutboundBookingDetail();

        return stayOutboundBookingDetail;
    }
}
