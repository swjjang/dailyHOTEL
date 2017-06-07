package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.Booking;

import java.util.List;

@JsonObject
public class BookingData
{
    @JsonField(name = "reservationRecIdx")
    public int reservationRecIdx;

    @JsonField(name = "tid")
    public String tid;

    @JsonField(name = "hotelName")
    public String hotelName;

    @JsonField(name = "checkinTime")
    public String checkinTime;

    @JsonField(name = "checkoutTime")
    public String checkoutTime;

    @JsonField(name = "comment")
    public String comment;

    @JsonField(name = "payType")
    public String payType;

    @JsonField(name = "type")
    public String type;

    @JsonField(name = "readyForRefund")
    public boolean readyForRefund;

    @JsonField(name = "img")
    public List<String> img;

    @JsonField(name = "imgDir")
    public String imgDir;


    public BookingData()
    {

    }

    public Booking getBooking()
    {
        Booking booking = new Booking();

        return booking;
    }
}
