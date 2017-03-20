package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.ExLog;

public class GourmetBookingTime extends PlaceBookingTime implements Parcelable
{
    public GourmetBookingTime()
    {
    }

    public GourmetBookingTime(Parcel in)
    {
        readFromParcel(in);
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
    }

    private void readFromParcel(Parcel in)
    {
        String bookingTime = in.readString();

        try
        {
            setBookingTime(bookingTime);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    public static final Parcelable.Creator CREATOR = new Creator()
    {
        public GourmetBookingTime createFromParcel(Parcel in)
        {
            return new GourmetBookingTime(in);
        }

        @Override
        public GourmetBookingTime[] newArray(int size)
        {
            return new GourmetBookingTime[size];
        }
    };
}