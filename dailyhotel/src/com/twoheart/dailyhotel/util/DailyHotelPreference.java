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

	// A/B Test 기능
	private static final String KEY_ALARM_HOTEL = "1";
	private static final String KEY_ALARM_FNB = "2";

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
}
