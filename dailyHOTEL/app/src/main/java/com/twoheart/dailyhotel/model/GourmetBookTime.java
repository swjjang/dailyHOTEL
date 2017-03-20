package com.twoheart.dailyhotel.model;

import android.os.Parcel;

import com.twoheart.dailyhotel.util.ExLog;

public class GourmetBookTime extends PlaceBookTime
{
    public GourmetBookTime()
    {
    }

    public GourmetBookTime(Parcel in)
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
        dest.writeString(mBookTime);
    }

    private void readFromParcel(Parcel in)
    {
        String bookTime = in.readString();

        try
        {
            setBookTime(bookTime);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    public static final Creator CREATOR = new Creator()
    {
        public GourmetBookTime createFromParcel(Parcel in)
        {
            return new GourmetBookTime(in);
        }

        @Override
        public GourmetBookTime[] newArray(int size)
        {
            return new GourmetBookTime[size];
        }
    };
}