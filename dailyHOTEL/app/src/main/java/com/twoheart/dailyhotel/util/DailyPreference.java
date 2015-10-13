package com.twoheart.dailyhotel.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 */
public class DailyPreference
{
    private static final String KEY_OPENING_ALARM = "1"; // 알람
    private static final String KEY_NEW_EVENT_TODAY_FNB = "2"; // 앱 처음 실행시 FNB에  New 아이콘 넣기
    private static DailyPreference mInstance;
    private SharedPreferences mPreferences;
    private Editor mEditor;

    private DailyPreference(Context context)
    {
        mPreferences = context.getSharedPreferences("dailyHOTEL_v1", Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }

    public static synchronized DailyPreference getInstance(Context context)
    {
        if (mInstance == null)
        {
            mInstance = new DailyPreference(context);
        }

        return mInstance;
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    // Preference
    /////////////////////////////////////////////////////////////////////////////////////////

    public boolean getEnabledOpeningAlarm()
    {
        boolean result = false;

        if (mPreferences != null)
        {
            result = mPreferences.getBoolean(KEY_OPENING_ALARM, false);
        }

        return result;
    }

    public void setEnabledOpeningAlarm(boolean enable)
    {
        if (mEditor != null)
        {
            mEditor.putBoolean(KEY_OPENING_ALARM, enable);
            mEditor.apply();
        }
    }

    public boolean isNewTodayFnB()
    {
        boolean result = false;

        if (mPreferences != null)
        {
            result = mPreferences.getBoolean(KEY_NEW_EVENT_TODAY_FNB, false);
        }

        return result;
    }

    public void setNewTodayFnB(boolean isNew)
    {
        if (mEditor != null)
        {
            mEditor.putBoolean(KEY_NEW_EVENT_TODAY_FNB, isNew);
            mEditor.apply();
        }
    }

    public void clear()
    {
        if (mEditor != null)
        {
            mEditor.clear();
            mEditor.apply();
        }
    }
}
