package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

public class StayBookingTime extends PlaceBookingTime implements Parcelable
{
    private int mNights;

    public StayBookingTime()
    {
    }

    public StayBookingTime(Parcel in)
    {
        readFromParcel(in);
    }

    public void setBookingTime(String bookingTime, int nights) throws Exception
    {
        if (Util.isTextEmpty(bookingTime) == true || nights <= 0)
        {
            return;
        }

        setBookingTime(bookingTime);
        mNights = nights;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(mBookingTime);
        dest.writeInt(mNights);
    }

    private void readFromParcel(Parcel in)
    {
        String bookingTime = in.readString();
        int nights = in.readInt();

        try
        {
            setBookingTime(bookingTime, nights);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    public static final Parcelable.Creator CREATOR = new Creator()
    {
        public StayBookingTime createFromParcel(Parcel in)
        {
            return new StayBookingTime(in);
        }

        @Override
        public StayBookingTime[] newArray(int size)
        {
            return new StayBookingTime[size];
        }
    };
}