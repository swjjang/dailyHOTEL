package com.twoheart.dailyhotel.model.time;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.network.model.TodayDateTime;

public class DayDateTime implements Parcelable
{
    private TodayDateTime mTodayDateTime;
    private int mAfterDay;

    public DayDateTime()
    {
    }

    public DayDateTime(Parcel in)
    {
        readFromParcel(in);
    }

    public void setToday(String openDateTime, String closeDateTime, String currentDateTime, String dailyDateTime)
    {
        if (mTodayDateTime == null)
        {
            mTodayDateTime = new TodayDateTime(openDateTime, closeDateTime, currentDateTime, dailyDateTime);
        }

        mTodayDateTime.setToday(openDateTime, closeDateTime, currentDateTime, dailyDateTime);
    }

    public DayDateTime getClone(int afterDay)
    {
        DayDateTime dayDateTime = new DayDateTime();
        dayDateTime.mTodayDateTime = mTodayDateTime.getClone();
        dayDateTime.mAfterDay = afterDay;

        return dayDateTime;
    }

    public void setAfterDay(int afterDay)
    {
        mAfterDay = afterDay;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeParcelable(mTodayDateTime, flags);
        dest.writeInt(mAfterDay);
    }

    private void readFromParcel(Parcel in)
    {
        mTodayDateTime = in.readParcelable(TodayDateTime.class.getClassLoader());
        mAfterDay = in.readInt();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public DayDateTime createFromParcel(Parcel in)
        {
            return new DayDateTime(in);
        }

        @Override
        public DayDateTime[] newArray(int size)
        {
            return new DayDateTime[size];
        }
    };
}