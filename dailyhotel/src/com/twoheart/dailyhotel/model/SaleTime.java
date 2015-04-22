package com.twoheart.dailyhotel.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;

public class SaleTime implements Constants, Parcelable
{
	public static final long SECONDS_IN_A_DAY = 3600 * 24;

	private Date mOpenTime;
	private Date mCloseTime;
	private Date mCurrentTime;
	private int mDayOfDays; // 데이즈 날짜. curentTime으로 부터 몇일.

	private static final SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss", Locale.KOREA);

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

	public void setCurrentTime(long currentTime)
	{
		mCurrentTime = new Date(currentTime);
	}

	public Date getLogicaltime()
	{
		long logicalTime = getCurrentTime() + SECONDS_IN_A_DAY * 1000 * mDayOfDays;

		ExLog.d("Current Time : " + mCurrentTime.toString() + ", mOpenTime : " + mOpenTime.toString());

		// 현재 시간이 오픈 시간 보다 작아지면.
		if (mCurrentTime.compareTo(mOpenTime) < 0)
		{
			try
			{
				// 다음 날이 되면 오늘 정시에서 클로즈 시간을 뺀다.
				String todayString = SaleTime.attachCurrentDate(getCurrentYear(), getCurrentMonth(), getCurrentDay(), "00:00:00");
				Date onTimeDate = SaleTime.stringToDate(todayString);

				ExLog.d("On Time : " + onTimeDate.toString() + ", Close Time :" + mCloseTime.toString());

				logicalTime += (onTimeDate.getTime() - getCloseTime());
			} catch (Exception e)
			{
				ExLog.e(e.toString());
			}
		}

		return new Date(logicalTime);
	}

	public String getLogicalDayOftheWeek()
	{
		return getTimezonedDateFormat("EEE").format(getLogicaltime());
	}

	public String getLogicalDay()
	{
		return getTimezonedDateFormat("d").format(getLogicaltime());
	}

	public String getLogicalDateFormat(String format)
	{
		return getTimezonedDateFormat(format).format(getLogicaltime());
	}

	public Date getRequestHotelDate()
	{
		return new Date(mCurrentTime.getTime() + SECONDS_IN_A_DAY * mDayOfDays * 1000);
	}

	public String getRequestHotelDateFormat(String format)
	{
		return getTimezonedDateFormat(format).format(getRequestHotelDate());
	}

	public SaleTime getClone(int nextDay)
	{
		SaleTime nextSaleTime = new SaleTime();

		nextSaleTime.mDayOfDays = nextDay;
		nextSaleTime.mOpenTime = new Date(mOpenTime.getTime());
		nextSaleTime.mCloseTime = new Date(mCloseTime.getTime());
		nextSaleTime.mCurrentTime = new Date(mCurrentTime.getTime());

		return nextSaleTime;
	}

	public SimpleDateFormat getTimezonedDateFormat(String datePattern)
	{
		SimpleDateFormat sFormat = new SimpleDateFormat(datePattern, Locale.KOREA);
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

	public String getCurrentDayEx()
	{
		return getTimezonedDateFormat("d").format(mCurrentTime);
	}

	public String getCurrentYear()
	{
		return getTimezonedDateFormat("yy").format(mCurrentTime);
		//		return new SimpleDateFormat("yy").format(mCurrentTime);
	}

	public String getCurrentDayOftheWeek()
	{
		return getTimezonedDateFormat("EEE").format(mCurrentTime);
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
		return mCurrentTime.getTime();
	}

	public String getCurrentDate()
	{
		return getTimezonedDateFormat("M월 dd일 EEEE").format(mCurrentTime);
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
		//		dest.writeValue(mLogicalTime);
		dest.writeInt(mDayOfDays);
	}

	private void readFromParcel(Parcel in)
	{
		mOpenTime = (Date) in.readValue(Date.class.getClassLoader());
		mCloseTime = (Date) in.readValue(Date.class.getClassLoader());
		mCurrentTime = (Date) in.readValue(Date.class.getClassLoader());
		//		mLogicalTime = (Date) in.readValue(Date.class.getClassLoader());
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