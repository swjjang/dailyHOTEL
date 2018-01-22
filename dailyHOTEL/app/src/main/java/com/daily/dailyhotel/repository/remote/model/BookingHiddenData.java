package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.BookingHidden;

/**
 * Created by android_sam on 2017. 10. 25..
 */
@JsonObject
public class BookingHiddenData
{
    @JsonField(name = "isSuccess")
    public int isSuccessValue;

    @JsonField(name = "is_success")
    public boolean isSuccess;

    public BookingHiddenData()
    {

    }

    public BookingHidden getBookingHidden()
    {
        BookingHidden bookingHidden = new BookingHidden();

        bookingHidden.isSuccess = isSuccessValue == 1 || isSuccess;

        return bookingHidden;
    }
}
