package com.twoheart.dailyhotel.model;

import android.os.Parcel;

import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

public class StayReservationTime extends PlaceReservationTime
{
    private int mNights;

    public StayReservationTime()
    {
    }

    public void setReservationTime(String reservationTime, int nights) throws Exception
    {
        if (Util.isTextEmpty(reservationTime) == true || nights <= 0)
        {
            return;
        }

        setReservationTime(reservationTime);
        mNights = nights;
    }

    public StayReservationTime(Parcel in)
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
        dest.writeString(mReservationTime);
        dest.writeInt(mNights);
    }

    private void readFromParcel(Parcel in)
    {
        String reservationTime = in.readString();
        int nights = in.readInt();

        try
        {
            setReservationTime(reservationTime, nights);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    public static final Creator CREATOR = new Creator()
    {
        public StayReservationTime createFromParcel(Parcel in)
        {
            return new StayReservationTime(in);
        }

        @Override
        public StayReservationTime[] newArray(int size)
        {
            return new StayReservationTime[size];
        }
    };
}