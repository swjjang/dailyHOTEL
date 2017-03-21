package com.twoheart.dailyhotel.model;

import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;

import java.util.Calendar;
import java.util.Date;

public abstract class PlaceBookingTime
{
    protected String mBookingTime; // ISO-8601
    protected Calendar mBookingCalendar = DailyCalendar.getInstance();

    public PlaceBookingTime()
    {
    }

    void setBookingTime(String bookingTime) throws Exception
    {
        if (Util.isTextEmpty(bookingTime) == true)
        {
            return;
        }

        Date date = DailyCalendar.convertDate(bookingTime, DailyCalendar.ISO_8601_FORMAT);

        mBookingTime = bookingTime;
        mBookingCalendar.setTime(date);
    }
}