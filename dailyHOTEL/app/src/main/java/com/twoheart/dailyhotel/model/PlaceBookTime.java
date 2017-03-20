package com.twoheart.dailyhotel.model;

import android.os.Parcelable;

import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;

import java.util.Calendar;
import java.util.Date;

public abstract class PlaceBookTime implements Parcelable
{
    protected String mBookTime;
    protected Calendar mBookCalendar = DailyCalendar.getInstance();

    public PlaceBookTime()
    {
    }

    protected void setBookTime(String bookTime) throws Exception
    {
        if (Util.isTextEmpty(bookTime) == true)
        {
            return;
        }

        Date date = DailyCalendar.convertDate(bookTime, DailyCalendar.ISO_8601_FORMAT);

        mBookTime = bookTime;
        mBookCalendar.setTime(date);
    }
}