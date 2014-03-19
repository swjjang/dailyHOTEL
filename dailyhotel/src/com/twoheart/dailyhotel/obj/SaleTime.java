package com.twoheart.dailyhotel.obj;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.Constants;

public class SaleTime implements Constants, Parcelable {

	private Date mOpenTime;
	private Date mCloseTime;
	private Date mCurrentTime;

	public static final Locale locale = Locale.KOREA;
	private final Calendar calendar = Calendar.getInstance();
	private final SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss", locale);
	
	public SaleTime() {
		super();
	}
	
	
	public SaleTime(Date mOpenTime, Date mCloseTime,
			Date mCurrentTime) {
		super();
		this.mOpenTime = mOpenTime;
		this.mCloseTime = mCloseTime;
		this.mCurrentTime = mCurrentTime;
	}

	public SaleTime(Parcel in) {
		readFromParcel(in);
		
	}

	private String attachCurrentDate(String time) {
		String currentYear = getCurrentYear();
		String currentMonth = getCurrentMonth();
		String currentDay = getCurrentDay();
		
		return new StringBuilder(currentYear)
		.append("-").append(currentMonth)
		.append("-").append(currentDay)
		.append(" ").append(time).toString();
		
	}

	public void setOpenTime(String openTime) {
		try {
			String currentTime = attachCurrentDate(openTime);
			this.mOpenTime = format.parse(currentTime);
		} catch (ParseException e) {
			if (DEBUG)
				e.printStackTrace();
		}
	}

	public void setCloseTime(String closeTime) {
		try {
			this.mCloseTime = format.parse(attachCurrentDate(closeTime));
		} catch (ParseException e) {
			if (DEBUG)
				e.printStackTrace();
		}
	}
	
	public void setCurrentTime(String currentTime) {
		try {
			this.mCurrentTime = format.parse(format.format(new Date(Long.parseLong(currentTime))));
			calendar.setTime(mCurrentTime);
		} catch (ParseException e) {
			if (DEBUG)
				e.printStackTrace();
		}
	}
	
	public String getCurrentMonth() {
		return new SimpleDateFormat("MM", locale).format(mCurrentTime);
	}
	
	public String getCurrentDay() {
		return Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
	}
	
	public String getCurrentYear() {
		return new SimpleDateFormat("yy", locale).format(mCurrentTime);
	}
	
	public Long getCurrentTime() {
		return calendar.getTimeInMillis();
	}
	
	public Long getOpenTime() {
		return mOpenTime.getTime();
	}
	
	public Long getCloseTime() {
		return mCloseTime.getTime();
	}

	public boolean isSaleTime() {
		if ((mCurrentTime != null) && (mOpenTime != null) && (mCloseTime != null)) {
			if ((mCurrentTime.compareTo(mOpenTime) >= 0) && (mCurrentTime.compareTo(mCloseTime) < 0))
				return true;
		} else
			throw new IllegalStateException(
					"Current time, open time and close time must be set.");
		
		return false;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeValue(mOpenTime);
		dest.writeValue(mCloseTime);
		dest.writeValue(mCurrentTime);
		
	}
	
	private void readFromParcel(Parcel in) {
		mOpenTime = (Date) in.readValue(Date.class.getClassLoader());
		mCloseTime = (Date) in.readValue(Date.class.getClassLoader());
		mCurrentTime = (Date) in.readValue(Date.class.getClassLoader());
	}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public SaleTime createFromParcel(Parcel in) {
			return new SaleTime(in);
		}

		@Override
		public SaleTime[] newArray(int size) {
			return new SaleTime[size];
		}

	};

}