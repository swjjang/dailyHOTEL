package com.daily.dailyhotel.entity;

import com.twoheart.dailyhotel.util.DailyCalendar;

import java.util.Calendar;

public class GourmetBookDateTime extends PlaceBookDateTime
{
    public GourmetBookDateTime()
    {
    }

    /**
     * @param dateTime ISO-8601
     */
    public void setVisitDateTime(String dateTime) throws Exception
    {
        setTimeInString(dateTime);
    }

    /**
     * @param dateTime ISO-8601
     */
    public void setVisitDateTime(String dateTime, int afterDay) throws Exception
    {
        setTimeInString(dateTime, afterDay);
    }

    public String getVisitDateTime(String format)
    {
        return getString(format);
    }
}
