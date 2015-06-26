package com.twoheart.dailyhotel.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.twoheart.dailyhotel.HotelDaysListFragment;

/**
 */
public class ConfigurationPreferences
{
	private SharedPreferences mPreferences;
	private Editor mEditor;

	private static ConfigurationPreferences mInstance;
	
	private static String PREFERENCES_DATE_DAYS = "province_";	// 메인 화면에 보일 날짜 탭의 날짜 개수.

	public static ConfigurationPreferences getInstance(Context context)
	{
		if (mInstance == null)
		{
			synchronized (ConfigurationPreferences.class)
			{
				if (mInstance == null)
				{
					mInstance = new ConfigurationPreferences(context);
				}
			}
		}

		return mInstance;
	}

	private ConfigurationPreferences(Context context)
	{
		mPreferences = context.getSharedPreferences("configuration_dailyHOTEL", Context.MODE_PRIVATE);
		mEditor = mPreferences.edit();
	}

	/////////////////////////////////////////////////////////////////////////////////////////
	// Preference
	/////////////////////////////////////////////////////////////////////////////////////////

	public int getDaysCountOfProvince(int provinceIndex)
	{
		int state = 0;

		if (mPreferences != null)
		{
			state = mPreferences.getInt(PREFERENCES_DATE_DAYS + provinceIndex, HotelDaysListFragment.DEFAULT_DAY_OF_COUNT);
		}

		return state;
	}

	public void setDaysCountOfProvince(int provinceIndex, int days)
	{
		if (mEditor != null)
		{
			mEditor.putInt(PREFERENCES_DATE_DAYS + provinceIndex, days);
			mEditor.apply();
		}
	}
}
