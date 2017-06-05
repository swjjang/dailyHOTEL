package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.Reservation;

import java.util.List;

@JsonObject
public class ReservationData
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


    public ReservationData()
    {

    }

    public Reservation getReservation()
    {
        Reservation reservation = new Reservation();

        return reservation;
    }
}
