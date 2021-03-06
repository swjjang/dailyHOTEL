package com.daily.dailyhotel.entity;

import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.util.DailyCalendar;

import java.util.Calendar;

public abstract class PlaceBookDateTime
{
    // 기본적으로 GMT9, KOREA 이다.
    private Calendar mCalendar = DailyCalendar.getInstance();

    public PlaceBookDateTime()
    {
    }

    void setTimeInMillis(long millis)
    {
        mCalendar.setTimeInMillis(millis);
    }

    long getTime()
    {
        return mCalendar.getTimeInMillis();
    }

    String getString(String format)
    {
        return getCalendarDateString(mCalendar, format);
    }

    /**
     * @param dateString ISO8601타입이 아니면 안받아줌.
     */
    void setTimeInString(String dateString) throws Exception
    {
        DailyCalendar.setCalendarDateString(mCalendar, dateString);
    }

    void setTimeInString(String dateString, int afterDay) throws Exception
    {
        DailyCalendar.setCalendarDateString(mCalendar, dateString, afterDay);
    }

    String getCalendarDateString(Calendar calendar, String format)
    {
        if (calendar == null || DailyTextUtils.isTextEmpty(format) == true)
        {
            return null;
        }

        return DailyCalendar.format(calendar.getTime(), format);
    }
}
