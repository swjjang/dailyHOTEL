package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import java.util.Calendar;
import java.util.Date;

public class GourmetReservationTime extends PlaceReservationTime
{
    public GourmetReservationTime()
    {
    }

    public void setReservationTime(String reservationTime) throws Exception
    {
        if (Util.isTextEmpty(reservationTime) == true)
        {
            return;
        }

        Date date = DailyCalendar.convertDate(mReservationTime, DailyCalendar.ISO_8601_FORMAT);

        mReservationTime = reservationTime;

        mReservationCalendar.setTime(date);
    }

    public GourmetReservationTime(Parcel in)
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
    }

    private void readFromParcel(Parcel in)
    {
        String reservationTime = in.readString();

        try
        {
            setReservationTime(reservationTime);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    public static final Creator CREATOR = new Creator()
    {
        public GourmetReservationTime createFromParcel(Parcel in)
        {
            return new GourmetReservationTime(in);
        }

        @Override
        public GourmetReservationTime[] newArray(int size)
        {
            return new GourmetReservationTime[size];
        }
    };
}