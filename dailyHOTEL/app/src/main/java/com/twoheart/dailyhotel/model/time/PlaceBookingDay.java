package com.twoheart.dailyhotel.model.time;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;

import java.util.Calendar;

public abstract class PlaceBookingDay implements Parcelable
{
    // 기본적으로 GMT9, KOREA 이다.
    private Calendar mCalendar = DailyCalendar.getInstance();

    public PlaceBookingDay()
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
        if (calendar == null || Util.isTextEmpty(format) == true)
        {
            return null;
        }

        return DailyCalendar.format(calendar.getTime(), format);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeLong(mCalendar.getTimeInMillis());
    }

    protected void readFromParcel(Parcel in)
    {
        mCalendar.setTimeInMillis(in.readLong());
    }
}