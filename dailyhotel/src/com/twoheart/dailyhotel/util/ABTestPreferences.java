package com.twoheart.dailyhotel.util;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 */
public class ABTestPreferences
{
	private SharedPreferences mPreferences;
	private Editor mEditor;

	private static ABTestPreferences mInstance;

	// A/B Test 기능
	private static final String KAKAOTALK_CONSULT = "1";

	public static ABTestPreferences getInstance(Context context)
	{
		if (mInstance == null)
		{
			synchronized (ABTestPreferences.class)
			{
				if (mInstance == null)
				{
					mInstance = new ABTestPreferences(context);
				}
			}
		}

		return mInstance;
	}

	private ABTestPreferences(Context context)
	{
		mPreferences = context.getSharedPreferences("abTest", Context.MODE_PRIVATE);
		mEditor = mPreferences.edit();
	}

	public void configurationABTest(Context context, JSONObject jsonObject)
	{
		try
		{

		} catch (Exception e)
		{
			setKakaotalkConsult(0);
		}
	}

	public int getKakaotalkConsult()
	{
		int state = 0;

		if (mPreferences != null)
		{
			state = mPreferences.getInt(KAKAOTALK_CONSULT, 0);
		}

		return state;
	}

	public void setKakaotalkConsult(int state)
	{
		if (mEditor != null)
		{
			mEditor.putInt(KAKAOTALK_CONSULT, state);
			mEditor.apply();
		}
	}
}
