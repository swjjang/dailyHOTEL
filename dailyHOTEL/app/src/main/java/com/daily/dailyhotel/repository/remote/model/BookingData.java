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

    @JsonField(name = "reviewStatusType")
    public String reviewStatusType;

    @JsonField(name = "itemIdex")
    public int placeIndex;

    @JsonField(name = "images")
    public List<LinkedHashMap<String, String>> images;

    public BookingData()
    {

    }

    public Booking getBooking()
    {
        final String PAYMENT_COMPLETED = "10";
        final String PAYMENT_WAITING = "20";
        final String REVIEW_ABLE = "ADDABLE";

        Booking booking = new Booking();
        booking.placeName = name;
        booking.reservationIndex = reservationIdx;

        if (images != null && images.size() != 0)
        {
            booking.imageUrl = images.get(0).get("path");
        }

        if (PAYMENT_WAITING.equalsIgnoreCase(payType) == true)
        {
            booking.statePayment = Booking.PAYMENT_WAITING;
        } else
        {
            booking.statePayment = Booking.PAYMENT_COMPLETED;
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
        booking.placeIndex = placeIndex;
        booking.hasReview = REVIEW_ABLE.equalsIgnoreCase(reviewStatusType) == false;

        return booking;
    }
}
