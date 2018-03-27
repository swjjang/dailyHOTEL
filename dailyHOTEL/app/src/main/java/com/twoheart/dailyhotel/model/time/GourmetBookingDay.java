package com.twoheart.dailyhotel.model.time;

import android.os.Parcel;
import android.os.Parcelable;

public class GourmetBookingDay extends PlaceBookingDay
{
    public GourmetBookingDay()
    {
    }

    public GourmetBookingDay(String visitDateTime) throws Exception
    {
        setVisitDay(visitDateTime);
    }

    public GourmetBookingDay(Parcel in)
    {
        readFromParcel(in);
    }

    /**
     * @param dateTime ISO-8601
     */
    public void setVisitDay(String dateTime) throws Exception
    {
        setTimeInString(dateTime);
    }

    /**
     * @param dateTime ISO-8601
     */
    public void setVisitDay(String dateTime, int afterDay) throws Exception
    {
        setTimeInString(dateTime, afterDay);
    }

    public String getVisitDay(String format)
    {
        return getString(format);
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
    }

    protected void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);
    }

    public static final Parcelable.Creator CREATOR = new Creator()
    {
        public GourmetBookingDay createFromParcel(Parcel in)
        {
            return new GourmetBookingDay(in);
        }

        @Override
        public GourmetBookingDay[] newArray(int size)
        {
            return new GourmetBookingDay[size];
        }
    };
}