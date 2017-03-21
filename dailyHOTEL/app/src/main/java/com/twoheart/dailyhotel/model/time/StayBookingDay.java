package com.twoheart.dailyhotel.model.time;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.DailyCalendar;

import java.util.Calendar;

public class StayBookingDay extends PlaceBookingDay
{
    private Calendar mCheckOutCalendar = DailyCalendar.getInstance();

    public StayBookingDay()
    {
    }

    public StayBookingDay(Parcel in)
    {
        readFromParcel(in);
    }

    /**
     * @param millis GMT-0
     */
    public void setCheckInTime(long millis)
    {
        setTimeInMillis(millis);
    }

    /**
     * @param dateTime ISO-8601
     */
    public void setCheckInDay(String dateTime) throws Exception
    {
        setTimeInString(dateTime);
    }

    public String getCheckInDay(String format)
    {
        return getString(format);
    }

    public void setCheckOutDay(long millis)
    {
        mCheckOutCalendar.setTimeInMillis(millis);
    }

    /**
     * @param dateTime ISO-8601
     */
    public void setCheckOutDay(String dateTime) throws Exception
    {
        setCalendarDateString(mCheckOutCalendar, dateTime);
    }

    public void setCheckOutDay(String dateTime, int afterDay) throws Exception
    {
        setCalendarDateString(mCheckOutCalendar, dateTime);
    }

    public String getCheckOutDay(String format)
    {
        return getCalendarDateString(mCheckOutCalendar, format);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);

        dest.writeLong(mCheckOutCalendar.getTimeInMillis());
    }

    protected void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

        mCheckOutCalendar.setTimeInMillis(in.readLong());
    }

    public static final Parcelable.Creator CREATOR = new Creator()
    {
        public StayBookingDay createFromParcel(Parcel in)
        {
            return new StayBookingDay(in);
        }

        @Override
        public StayBookingDay[] newArray(int size)
        {
            return new StayBookingDay[size];
        }
    };
}