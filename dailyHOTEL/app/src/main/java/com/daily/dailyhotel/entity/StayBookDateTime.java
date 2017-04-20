package com.daily.dailyhotel.entity;

import android.support.annotation.NonNull;

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

    public void verifyCommonDateTime(@NonNull CommonDateTime commonDateTime) throws Exception
    {
        if (commonDateTime == null)
        {
            return;
        }

        // 예외 처리로 보고 있는 체크인/체크아웃 날짜가 지나 간경우 다음 날로 변경해준다.
        // 체크인 날짜 체크

        // 날짜로 비교해야 한다.
        Calendar todayCalendar = DailyCalendar.getInstance(commonDateTime.dailyDateTime, true);
        Calendar checkInCalendar = DailyCalendar.getInstance(getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), true);
        Calendar checkOutCalendar = DailyCalendar.getInstance(getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT), true);

        // 하루가 지나서 체크인 날짜가 전날짜 인 경우
        if (todayCalendar.getTimeInMillis() > checkInCalendar.getTimeInMillis())
        {
            setCheckInDateTime(commonDateTime.dailyDateTime);

            checkInCalendar = DailyCalendar.getInstance(getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), true);
        }

        // 체크인 날짜가 체크 아웃 날짜와 같거나 큰경우.
        if (checkInCalendar.getTimeInMillis() >= checkOutCalendar.getTimeInMillis())
        {
            setCheckOutDateTime(getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), 1);
        }
    }
}
