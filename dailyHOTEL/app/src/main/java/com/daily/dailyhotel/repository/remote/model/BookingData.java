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

    @JsonField(name = "aggregationId")
    public String aggregationId;

    @JsonField(name = "tid")
    public String tid;

    @JsonField(name = "name")
    public String name;

    @JsonField(name = "checkinDate")
    public String checkinDate;

    @JsonField(name = "checkoutDate")
    public String checkoutDate;

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

    @JsonField(name = "reviewStatusType")
    public String reviewStatusType;

    @JsonField(name = "itemIdx")
    public int placeIndex;

    @JsonField(name = "waitingForBooking")
    public boolean waitingForBooking;

    @JsonField(name = "images")
    public List<LinkedHashMap<String, String>> images;

    public BookingData()
    {

    }

    public Booking getBooking()
    {
        final String PAYMENT_COMPLETED = "10";
        final String PAYMENT_WAITING = "20";
        final String REVIEW_MODIFIABLE = "MODIFIABLE";
        final String REVIEW_ADDABLE = "ADDABLE";

        Booking booking = new Booking();
        booking.placeName = name;
        booking.reservationIndex = reservationIdx;
        booking.aggregationId = aggregationId;

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

        booking.checkInDateTime = checkinDate + "T" + checkinTime + "+09:00";
        booking.checkOutDateTime = checkoutDate + "T" + checkoutTime + "+09:00";
        booking.readyForRefund = readyForRefund;
        booking.comment = comment;
        booking.tid = tid;
        booking.placeIndex = placeIndex;
        booking.availableReview = REVIEW_MODIFIABLE.equalsIgnoreCase(reviewStatusType) == true //
            || REVIEW_ADDABLE.equalsIgnoreCase(reviewStatusType) == true;
        booking.waitingForBooking = waitingForBooking;
        booking.reviewStatusType = reviewStatusType;

        return booking;
    }
}
