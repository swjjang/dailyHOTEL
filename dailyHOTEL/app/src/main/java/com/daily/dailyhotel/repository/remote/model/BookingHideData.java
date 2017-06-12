package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.BookingHide;

@JsonObject
public class BookingHideData
{
    @JsonField(name = "reservationIdx")
    public int reservationIdx;

    public BookingHideData()
    {

    }

    public BookingHide getBookingHide()
    {
        BookingHide bookingHide = new BookingHide();

        bookingHide.index = reservationIdx;

        return bookingHide;
    }
}
