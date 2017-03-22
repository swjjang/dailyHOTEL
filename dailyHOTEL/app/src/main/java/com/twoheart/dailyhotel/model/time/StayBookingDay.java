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
     * @param dateTime ISO-8601
     */
    public void setCheckInDay(String dateTime) throws Exception
    {
        setTimeInString(dateTime);
    }

    /**
     * @param dateTime ISO-8601
     */
    public void setCheckInDay(String dateTime, int afterDay) throws Exception
    {
        setTimeInString(dateTime, afterDay);
    }

    public String getCheckInDay(String format)
    {
        return getString(format);
    }

    /**
     * @param dateTime ISO-8601
     */
    public void setCheckOutDay(String dateTime) throws Exception
    {
        DailyCalendar.setCalendarDateString(mCheckOutCalendar, dateTime);
    }

    public void setCheckOutDay(String dateTime, int afterDay) throws Exception
    {
        DailyCalendar.setCalendarDateString(mCheckOutCalendar, dateTime, afterDay);
    }

    public String getCheckOutDay(String format)
    {
        return getCalendarDateString(mCheckOutCalendar, format);
    }

    public int getNights() throws Exception
    {
        Calendar checkInCalendar = DailyCalendar.getInstance(getCheckInDay(DailyCalendar.ISO_8601_FORMAT), true);
        Calendar checkOutCalendar = DailyCalendar.getInstance(getCheckOutDay(DailyCalendar.ISO_8601_FORMAT), true);

        return (int) ((checkOutCalendar.getTimeInMillis() - checkInCalendar.getTimeInMillis()) / DailyCalendar.DAY_MILLISECOND);
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