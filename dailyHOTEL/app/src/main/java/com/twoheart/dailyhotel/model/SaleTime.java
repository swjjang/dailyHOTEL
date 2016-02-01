package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class SaleTime implements Constants, Parcelable
{
    public static final long MILLISECOND_IN_A_DAY = 3600 * 24 * 1000;
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public SaleTime createFromParcel(Parcel in)
        {
            return new SaleTime(in);
        }

        @Override
        public SaleTime[] newArray(int size)
        {
            return new SaleTime[size];
        }

    };

    private Date mCurrentTime;
    private Date mDailyTime;
    private int mDayOfDays; // 데이즈 날짜. curentTime으로 부터 몇일.

    public SaleTime()
    {
        super();
    }

    public SaleTime(Parcel in)
    {
        readFromParcel(in);
    }

    public String toString()
    {
        SimpleDateFormat sFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss", Locale.KOREA);
        sFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("currentTime : " + sFormat.format(mCurrentTime));
        stringBuilder.append("\ndailyTime : " + sFormat.format(mDailyTime));

        return stringBuilder.toString();
    }

    public String getDailyDayOftheWeek()
    {
        return getTimezonedDateFormat("EEE").format(getDayOfDaysDate());
    }

    public String getDailyDay()
    {
        return getTimezonedDateFormat("d").format(getDayOfDaysDate());
    }

    public String getDailyDateFormat(String format)
    {
        return getTimezonedDateFormat(format).format(getDayOfDaysDate());
    }

    public Date getDayOfDaysDate()
    {
        return new Date(mDailyTime.getTime() + MILLISECOND_IN_A_DAY * mDayOfDays);
    }

    public String getDayOfDaysDateFormat(String format)
    {
        return getTimezonedDateFormat(format).format(getDayOfDaysDate());
    }

    public SaleTime getClone(int nextDay)
    {
        SaleTime nextSaleTime = new SaleTime();

        nextSaleTime.mDayOfDays = nextDay;
        nextSaleTime.mCurrentTime = new Date(mCurrentTime.getTime());
        nextSaleTime.mDailyTime = new Date(mDailyTime.getTime());

        return nextSaleTime;
    }

    public SimpleDateFormat getTimezonedDateFormat(String datePattern)
    {
        SimpleDateFormat sFormat = new SimpleDateFormat(datePattern, Locale.KOREA);
        sFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sFormat;
    }

    public long getCurrentTime()
    {
        return mCurrentTime.getTime();
    }

    public void setCurrentTime(long currentTime)
    {
        mCurrentTime = new Date(currentTime);
    }

    public long getDailyTime()
    {
        return mDailyTime.getTime();
    }

    public void setDailyTime(long dailyTime)
    {
        mDailyTime = new Date(dailyTime);
    }

    public int getOffsetDailyDay()
    {
        return mDayOfDays;
    }

    public void setOffsetDailyDay(int dayOfDays)
    {
        mDayOfDays = dayOfDays;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeValue(mCurrentTime);
        dest.writeValue(mDailyTime);
        dest.writeInt(mDayOfDays);
    }

    private void readFromParcel(Parcel in)
    {
        mCurrentTime = (Date) in.readValue(Date.class.getClassLoader());
        mDailyTime = (Date) in.readValue(Date.class.getClassLoader());
        mDayOfDays = in.readInt();
    }
}