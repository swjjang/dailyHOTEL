package com.twoheart.dailyhotel.model;

import android.os.Parcelable;

import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;

import java.util.Calendar;
import java.util.Date;

public abstract class PlaceReservationTime implements Parcelable
{
    protected String mReservationTime;
    protected Calendar mReservationCalendar = DailyCalendar.getInstance();

    public PlaceReservationTime()
    {
    }

    protected void setReservationTime(String reservationTime) throws Exception
    {
        if (Util.isTextEmpty(reservationTime) == true)
        {
            return;
        }

        Date date = DailyCalendar.convertDate(mReservationTime, DailyCalendar.ISO_8601_FORMAT);

        mReservationTime = reservationTime;
        mReservationCalendar.setTime(date);
    }
}