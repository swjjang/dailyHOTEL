package com.twoheart.dailyhotel.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.Constants;

public class SaleTime implements Constants, Parcelable
{
	public static final long SECONDS_IN_A_DAY = 3600 * 24;

	private Date mOpenTime;
	private Date mCloseTime;
	private Date mCurrentTime;
	private Date mDailyTime;

	private int mDayOfDays; // 데이즈 날짜. curentTime으로 부터 몇일.

	private static final SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss", Locale.KOREA);

	public SaleTime()
	{
		super();

		format.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	public SaleTime(Parcel in)
	{
		readFromParcel(in);

	}

	public static String attachCurrentDate(String currentYear, String currentMonth, String currentDay, String time)
	{

		return new StringBuilder(currentYear).append("-").append(currentMonth).append("-").append(currentDay).append(" ").append(time).toString();

	}

	public static Date stringToDate(String string) throws ParseException
	{
		return format.parse(string);
	}

	public static String dateToString(Date date) throws ParseException
	{
		return format.format(date);
	}

	public void setOpenTime(long openTime)
	{
		mOpenTime = new Date(openTime);
	}

	public void setCloseTime(long closeTime)
	{
		mCloseTime = new Date(closeTime);
	}

	public void setCurrentTime(long currentTime)
	{
		mCurrentTime = new Date(currentTime);
	}

	public void setDailyTime(long dailyTime)
	{
		mDailyTime = new Date(dailyTime);
	}

	public String getDailyDayOftheWeek()
	{
		return getTimezonedDateFormat("EEE").format(getDayOfDaysHotelDate());
	}

	public String getDailyDay()
	{
		return getTimezonedDateFormat("d").format(getDayOfDaysHotelDate());
	}

	public String getDailyDateFormat(String format)
	{
		return getTimezonedDateFormat(format).format(getDayOfDaysHotelDate());
	}

	public Date getDayOfDaysHotelDate()
	{
		return new Date(mDailyTime.getTime() + SECONDS_IN_A_DAY * mDayOfDays * 1000);
	}

	public String getDayOfDaysHotelDateFormat(String format)
	{
		return getTimezonedDateFormat(format).format(getDayOfDaysHotelDate());
	}

	public SaleTime getClone(int nextDay)
	{
		SaleTime nextSaleTime = new SaleTime();

		nextSaleTime.mDayOfDays = nextDay;
		nextSaleTime.mOpenTime = new Date(mOpenTime.getTime());
		nextSaleTime.mCloseTime = new Date(mCloseTime.getTime());
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

	public Long getCurrentTime()
	{
		return mCurrentTime.getTime();
	}

	public Long getOpenTime()
	{
		return mOpenTime.getTime();
	}

	public Long getCloseTime()
	{
		return mCloseTime.getTime();
	}
	
	public Long getDailyTime()
	{
		return mDailyTime.getTime();
	}
	
	public int getOffsetDailyDay()
	{
		return mDayOfDays;
	}


	public boolean isSaleTime()
	{
		if (ALWAYS_OPEN == true)
		{
			return true;
		}

		if ((mCurrentTime != null) && (mOpenTime != null) && (mCloseTime != null))
		{
			if ((mCurrentTime.compareTo(mOpenTime) >= 0) && (mCurrentTime.compareTo(mCloseTime) < 0))
			{
				return true;
			} else
			{
				return false;
			}
		} else
		{
			throw new IllegalStateException("Current time, open time and close time must be set.");
		}
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeValue(mOpenTime);
		dest.writeValue(mCloseTime);
		dest.writeValue(mCurrentTime);
		dest.writeValue(mDailyTime);
		dest.writeInt(mDayOfDays);
	}

	private void readFromParcel(Parcel in)
	{
		mOpenTime = (Date) in.readValue(Date.class.getClassLoader());
		mCloseTime = (Date) in.readValue(Date.class.getClassLoader());
		mCurrentTime = (Date) in.readValue(Date.class.getClassLoader());
		mDailyTime = (Date) in.readValue(Date.class.getClassLoader());
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