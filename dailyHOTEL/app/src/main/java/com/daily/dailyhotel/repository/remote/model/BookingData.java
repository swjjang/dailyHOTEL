package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.Booking;

import java.util.LinkedHashMap;
import java.util.List;

@JsonObject
public class BookingData
{
    @JsonField(name = "reservationIdx")
    public int reservationIdx;

    @JsonField(name = "tid")
    public String tid;

    @JsonField(name = "name")
    public String name;

    @JsonField(name = "checkinDate")
    public String checkinDate;

    @JsonField(name = "checkoutDate")
    public String checkoutDate;

    @JsonField(name = "comment")
    public String comment;

    @JsonField(name = "payType")
    public String payType;

    @JsonField(name = "type")
    public String type;

    @JsonField(name = "readyForRefund")
    public boolean readyForRefund;

    @JsonField(name = "images")
    public List<LinkedHashMap<String, String>> images;

    public BookingData()
    {

    }

    public Booking getBooking()
    {
        final String COMPLETED_PAYMENT = "10";
        final String WAIT_PAYMENT = "20";

        Booking booking = new Booking();
        booking.placeName = name;
        booking.index = reservationIdx;

        if (images != null && images.size() != 0)
        {
            booking.imageUrl = images.get(0).get("path");
        }

        if (WAIT_PAYMENT.equalsIgnoreCase(payType) == true)
        {
            booking.statusPayment = Booking.WAIT_PAYMENT;
        } else
        {
            booking.statusPayment = Booking.COMPLETED_PAYMENT;
        }

        switch (type)
        {
            case "hotel":
                booking.placeType = Booking.PlaceType.STAY;
                break;

            case "fnb":
                booking.placeType = Booking.PlaceType.GOURMET;
                break;

            case "outbound":
                booking.placeType = Booking.PlaceType.STAY_OUTBOUND;
                break;
        }

        booking.checkInDateTime = checkinDate;
        booking.checkOutDateTime = checkoutDate;
        booking.readyForRefund = readyForRefund;
        booking.comment = comment;
        booking.tid = tid;

        return booking;
    }
}
