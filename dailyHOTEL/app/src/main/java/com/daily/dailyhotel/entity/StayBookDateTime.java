package com.daily.dailyhotel.entity;

import com.twoheart.dailyhotel.util.DailyCalendar;

import java.util.Calendar;

public class StayBookDateTime extends PlaceBookDateTime
{
    private Calendar mCheckOutCalendar = DailyCalendar.getInstance();

    public StayBookDateTime()
    {
    }

    /**
     * @param dateTime ISO-8601
     */
    public void setCheckInDateTime(String dateTime) throws Exception
    {
        setTimeInString(dateTime);
    }

    /**
     * @param dateTime ISO-8601
     */
    public void setCheckInDateTime(String dateTime, int afterDay) throws Exception
    {
        setTimeInString(dateTime, afterDay);
    }

    public String getCheckInDateTime(String format)
    {
        return getString(format);
    }

    /**
     * @param dateTime ISO-8601
     */
    public void setCheckOutDateTime(String dateTime) throws Exception
    {
        DailyCalendar.setCalendarDateString(mCheckOutCalendar, dateTime);
    }

    public void setCheckOutDateTime(String dateTime, int afterDay) throws Exception
    {
        DailyCalendar.setCalendarDateString(mCheckOutCalendar, dateTime, afterDay);
    }

    public String getCheckOutDateTime(String format)
    {
        return getCalendarDateString(mCheckOutCalendar, format);
    }

    public int getNights() throws Exception
    {
        Calendar checkInCalendar = DailyCalendar.getInstance(getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), true);
        Calendar checkOutCalendar = DailyCalendar.getInstance(getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT), true);

        return (int) ((checkOutCalendar.getTimeInMillis() - checkInCalendar.getTimeInMillis()) / DailyCalendar.DAY_MILLISECOND);
    }
}
