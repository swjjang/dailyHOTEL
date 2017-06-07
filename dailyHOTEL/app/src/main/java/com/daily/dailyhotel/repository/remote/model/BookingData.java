package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.Booking;

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

    @JsonField(name = "checkinTime")
    public String checkinTime;

    @JsonField(name = "checkoutTime")
    public String checkoutTime;

    @JsonField(name = "comment")
    public String comment;

    @JsonField(name = "paymentType")
    public String paymentType;

    @JsonField(name = "type")
    public String type;

    @JsonField(name = "readyForRefund")
    public boolean readyForRefund;

    @JsonField(name = "images")
    public List<String> images;

    public BookingData()
    {

    }

    public Booking getBooking()
    {
        Booking booking = new Booking();
        booking.imageUrl = images.get(0);

        switch(paymentType)
        {
            case "card":
                booking.paymentType = Booking.PaymentType.CARD;
                break;

            case "phone":
                booking.paymentType = Booking.PaymentType.PHONE;
                break;
        }

        switch (type)
        {
            case "stay":
                booking.placeType = Booking.PlaceType.STAY;
                break;

            case "gourmet":
                booking.placeType = Booking.PlaceType.GOURMET;
                break;

            case "outbound":
                booking.placeType = Booking.PlaceType.STAY_OUTBOUND;
                break;
        }

        booking.checkInDateTime = checkinTime;
        booking.checkOutDateTime = checkoutTime;
        booking.readyForRefund = readyForRefund;
        booking.comment = comment;
        booking.tid = tid;

        return booking;
    }
}
