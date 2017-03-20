package com.twoheart.dailyhotel.model;

import android.os.Parcel;

import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

public class StayBookTime extends PlaceBookTime
{
    private int mNights;

    public StayBookTime()
    {
    }

    public void setBookTime(String bookTime, int nights) throws Exception
    {
        if (Util.isTextEmpty(bookTime) == true || nights <= 0)
        {
            return;
        }

        setBookTime(bookTime);
        mNights = nights;
    }

    public StayBookTime(Parcel in)
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
        dest.writeInt(mNights);
    }

    private void readFromParcel(Parcel in)
    {
        String bookTime = in.readString();
        int nights = in.readInt();

        try
        {
            setBookTime(bookTime, nights);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    public static final Creator CREATOR = new Creator()
    {
        public StayBookTime createFromParcel(Parcel in)
        {
            return new StayBookTime(in);
        }

        @Override
        public StayBookTime[] newArray(int size)
        {
            return new StayBookTime[size];
        }
    };
}