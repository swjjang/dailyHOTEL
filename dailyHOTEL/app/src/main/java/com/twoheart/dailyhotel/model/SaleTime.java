package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.ExLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SaleTime implements Parcelable
{
    public static final long MILLISECOND_IN_A_DAY = 3600 * 24 * 1000;

    private Date mCurrentDateTime;
    private Date mDailyDateTime;
    private int mDayOfDays; // 데이즈 날짜. curentTime으로 부터 몇일.

    public SaleTime()
    {
        super();
    }

    public SaleTime(Parcel in)
    {
        readFromParcel(in);
    }

    public Date getDayOfDaysDate()
    {
        return new Date(mDailyDateTime.getTime() + MILLISECOND_IN_A_DAY * mDayOfDays);
    }

    public String getDayOfDaysDateFormat(String format)
    {
        //        return getTimezonedDateFormat(format).format(getDayOfDaysDate());
        return DailyCalendar.format(getDayOfDaysDate(), format);
    }

    public SaleTime getClone(int nextDay)
    {
        SaleTime nextSaleTime = new SaleTime();

        nextSaleTime.mDayOfDays = nextDay;
        nextSaleTime.mCurrentDateTime = (Date) mCurrentDateTime.clone();
        nextSaleTime.mDailyDateTime = (Date) mDailyDateTime.clone();

        return nextSaleTime;
    }

    public boolean isDayOfDaysDateEquals(SaleTime saleTime)
    {
        return mDailyDateTime.getTime() == saleTime.mDailyDateTime.getTime() && mDayOfDays == saleTime.mDayOfDays;
    }

    public void setCurrentTime(String currentDateTime)
    {
        try
        {
            mCurrentDateTime = DailyCalendar.convertDate(currentDateTime, DailyCalendar.ISO_8601_FORMAT);
        } catch (ParseException e)
        {
            ExLog.d(e.toString());
        }
    }

    public Date getDailyDateTime()
    {
        return mDailyDateTime;
    }

    public void setDailyTime(String dailyDateTime)
    {
        try
        {
            mDailyDateTime = DailyCalendar.convertDate(dailyDateTime, DailyCalendar.ISO_8601_FORMAT);
        } catch (ParseException e)
        {
            ExLog.d(e.toString());
        }
    }

    public int getOffsetDailyDay()
    {
        return mDayOfDays;
    }

    public void setOffsetDailyDay(int dayOfDays)
    {
        mDayOfDays = dayOfDays;
    }

    /**
     * yyyyMMdd의 날짜를 기존의 SaleTime에 적용한다.
     *
     * @param saleTime
     * @param date
     */
    public static SaleTime changeDateSaleTime(SaleTime saleTime, String date)
    {
        SaleTime changedSaleTime = null;

        try
        {
            SimpleDateFormat format = new java.text.SimpleDateFormat("yyyyMMdd", Locale.KOREA);
            Date schemeDate = format.parse(date);
            Date dailyDate = format.parse(saleTime.getDayOfDaysDateFormat("yyyyMMdd"));

            int dailyDayOfDays = (int) ((schemeDate.getTime() - dailyDate.getTime()) / SaleTime.MILLISECOND_IN_A_DAY);

            if (dailyDayOfDays >= 0)
            {
                changedSaleTime = saleTime.getClone(dailyDayOfDays);
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        return changedSaleTime;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        if (mCurrentDateTime == null)
        {
            mCurrentDateTime = new Date();
        }

        if (mDailyDateTime == null)
        {
            mDailyDateTime = new Date();
        }

        dest.writeLong(mCurrentDateTime.getTime());
        dest.writeLong(mDailyDateTime.getTime());
        dest.writeInt(mDayOfDays);
    }

    private void readFromParcel(Parcel in)
    {
        mCurrentDateTime = new Date(in.readLong());
        mDailyDateTime = new Date(in.readLong());
        mDayOfDays = in.readInt();
    }

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
}