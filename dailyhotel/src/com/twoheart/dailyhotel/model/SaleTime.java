package com.twoheart.dailyhotel.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;

public class SaleTime implements Constants, Parcelable
{
	private Date mOpenTime;
	private Date mCloseTime;
	private Date mCurrentTime;

	private static final Calendar calendar = Calendar.getInstance();
	private static final SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss");

	public SaleTime()
	{
		super();
		//		TimeZone.get
		format.setTimeZone(TimeZone.getTimeZone("GMT+09:00"));
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

	public void setOpenTime(String openTime)
	{
		try
		{
			String currentTime = attachCurrentDate(getCurrentYear(), getCurrentMonth(), getCurrentDay(), openTime);
			this.mOpenTime = stringToDate(currentTime);
		} catch (ParseException e)
		{
			ExLog.e(e.toString());
		}
	}

	public void setCloseTime(String closeTime)
	{
		try
		{
			this.mCloseTime = stringToDate(attachCurrentDate(getCurrentYear(), getCurrentMonth(), getCurrentDay(), closeTime));
		} catch (ParseException e)
		{
			ExLog.e(e.toString());
		}
	}

	public void setCurrentTime(String currentTime)
	{
		try
		{
			mCurrentTime = new Date(Long.parseLong(currentTime));
			calendar.setTime(mCurrentTime);

		} catch (NumberFormatException e)
		{
			ExLog.e(e.toString());
		}
	}

	public SimpleDateFormat getTimezonedDateFormat(String datePattern)
	{
		SimpleDateFormat sFormat = new SimpleDateFormat(datePattern);
		sFormat.setTimeZone(TimeZone.getTimeZone("GMT+09:00"));
		return sFormat;
	}

	public String getCurrentMonth()
	{
		return getTimezonedDateFormat("MM").format(mCurrentTime);
		//		return new SimpleDateFormat("MM").format(mCurrentTime);
	}

	public String getCurrentDay()
	{
		return getTimezonedDateFormat("dd").format(mCurrentTime);
		//		return new SimpleDateFormat("dd").format(mCurrentTime);
	}

	public String getCurrentYear()
	{
		return getTimezonedDateFormat("yy").format(mCurrentTime);
		//		return new SimpleDateFormat("yy").format(mCurrentTime);
	}

	public String getCurrentHour()
	{
		return getTimezonedDateFormat("hh").format(mCurrentTime);
	}

	public String getCurrentMin()
	{
		return getTimezonedDateFormat("mm").format(mCurrentTime);
	}

	public String getCurrentSec()
	{
		return getTimezonedDateFormat("ss").format(mCurrentTime);
	}

	public Long getCurrentTime()
	{
		return calendar.getTimeInMillis();
	}

	public Long getOpenTime()
	{
		return mOpenTime.getTime();
	}

	public Long getCloseTime()
	{
		return mCloseTime.getTime();
	}

	// TimeControl: 시간 조정
	public boolean isSaleTime()
	{
		if (ALWAYS_OPEN == true)
			return true;

		if ((mCurrentTime != null) && (mOpenTime != null) && (mCloseTime != null))
		{
			if (mCloseTime.compareTo(mOpenTime) > 0)
			{
				if ((mCurrentTime.compareTo(mOpenTime) >= 0) && (mCurrentTime.compareTo(mCloseTime) < 0))
					return true;
				else
					return false;

			} else
			{
				if ((mCurrentTime.compareTo(mCloseTime) >= 0) && (mCurrentTime.compareTo(mOpenTime) < 0))
					return false;
				else
					return true;
			}

		} else
			throw new IllegalStateException("Current time, open time and close time must be set.");
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

	}

	private void readFromParcel(Parcel in)
	{
		mOpenTime = (Date) in.readValue(Date.class.getClassLoader());
		mCloseTime = (Date) in.readValue(Date.class.getClassLoader());
		mCurrentTime = (Date) in.readValue(Date.class.getClassLoader());
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