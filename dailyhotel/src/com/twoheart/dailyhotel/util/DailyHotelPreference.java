package com.twoheart.dailyhotel.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 */
public class DailyHotelPreference
{
	private SharedPreferences mPreferences;
	private Editor mEditor;

	private static DailyHotelPreference mInstance;

	private static final String KEY_ALARM_HOTEL = "1"; // 알람 호텔
	private static final String KEY_ALARM_FNB = "2"; // 알람 FNB

	private static final String KEY_NEW_EVENT_TODAY_FNB = "3"; // 앱 처음 실행시 FNB에  New 아이콘 넣기

	public static synchronized DailyHotelPreference getInstance(Context context)
	{
		if (mInstance == null)
		{
			mInstance = new DailyHotelPreference(context);
		}

		return mInstance;
	}

	private DailyHotelPreference(Context context)
	{
		mPreferences = context.getSharedPreferences("dailyHOTEL_v1", Context.MODE_PRIVATE);
		mEditor = mPreferences.edit();
	}

	/////////////////////////////////////////////////////////////////////////////////////////
	// Preference
	/////////////////////////////////////////////////////////////////////////////////////////

	public boolean getEnabledHotelAlarm()
	{
		boolean result = false;

		if (mPreferences != null)
		{
			result = mPreferences.getBoolean(KEY_ALARM_HOTEL, false);
		}

		return result;
	}

	public void setEnabledHotelAlarm(boolean enable)
	{
		if (mEditor != null)
		{
			mEditor.putBoolean(KEY_ALARM_HOTEL, enable);
			mEditor.commit();
		}
	}

	public boolean getEnabledFnBAlarm()
	{
		boolean result = false;

		if (mPreferences != null)
		{
			result = mPreferences.getBoolean(KEY_ALARM_FNB, false);
		}

		return result;
	}

	public void setEnabledFnBAlarm(boolean enable)
	{
		if (mEditor != null)
		{
			mEditor.putBoolean(KEY_ALARM_FNB, enable);
			mEditor.commit();
		}
	}

	public boolean isNewTodayFnB()
	{
		boolean result = false;

		if (mPreferences != null)
		{
			result = mPreferences.getBoolean(KEY_NEW_EVENT_TODAY_FNB, true);
		}

		return result;
	}

	public void setNewTodayFnB(boolean isNew)
	{
		if (mEditor != null)
		{
			mEditor.putBoolean(KEY_NEW_EVENT_TODAY_FNB, isNew);
			mEditor.commit();
		}
	}
}
