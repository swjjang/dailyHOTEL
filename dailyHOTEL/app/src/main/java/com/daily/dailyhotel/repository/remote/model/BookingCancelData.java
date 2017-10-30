package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.BookingCancel;

@JsonObject
public class BookingCancelData
{
    @JsonField(name = "aggregationId")
    public String aggregationId;

    @JsonField(name = "orderSeq")
    public long orderSeq;

    @JsonField(name = "reservationIdx")
    public int reservationIdx;

    @JsonField(name = "checkinDate")
    public String checkinDate;

    @JsonField(name = "checkoutDate")
    public String checkoutDate;

    @JsonField(name = "checkinTime")
    public String checkinTime;

    @JsonField(name = "checkoutTime")
    public String checkoutTime;

    @JsonField(name = "cancelDateTime")
    public String cancelDateTime;

    @JsonField(name = "imagePath")
    public String imageUrl;

    @JsonField(name = "type")
    public String type;

    @JsonField(name = "name")
    public String name;

    @JsonField(name = "itemIdx")
    public int itemIdx;

    public BookingCancelData()
    {

    }

    public BookingCancel getBookingCancel()
    {
        BookingCancel bookingCancel = new BookingCancel();
        bookingCancel.aggregationId = aggregationId;
        bookingCancel.orderSeq = orderSeq;
        bookingCancel.reservationIdx = reservationIdx;
//        bookingCancel.checkinDate = checkinDate;
//        bookingCancel.checkoutDate = checkoutDate;
//        bookingCancel.checkinTime = checkinTime;
//        bookingCancel.checkoutTime = checkoutTime;
        bookingCancel.cancelDateTime = cancelDateTime;
        bookingCancel.imageUrl = imageUrl;
//        bookingCancel.type = type;
        bookingCancel.name = name;
        bookingCancel.itemIdx = itemIdx;

        switch (type)
        {
            case "hotel":
            case "HOTEL":
                bookingCancel.placeType = BookingCancel.PlaceType.STAY;
                break;

            case "fnb":
            case "FNB":
                bookingCancel.placeType = BookingCancel.PlaceType.GOURMET;
                break;

            case "outbound":
            case "OUTBOUND":
                bookingCancel.placeType = BookingCancel.PlaceType.STAY_OUTBOUND;
                break;
        }

        bookingCancel.checkInDateTime = checkinDate + "T" + checkinTime + "+09:00";
        bookingCancel.checkOutDateTime = checkoutDate + "T" + checkoutTime + "+09:00";

        return bookingCancel;
    }

}
