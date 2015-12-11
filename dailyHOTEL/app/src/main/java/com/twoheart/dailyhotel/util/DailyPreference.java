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
    private static final String KEY_LAST_MENU = "3"; // 마지막 메뉴 리스트가 무엇인지
    private static final String KEY_SHOW_GUIDE = "4"; // 가이드를 봤는지 여부
    private static final String KEY_ALLOW_PUSH = "5";

    private static final String KEY_COMPANY_NAME = "100";
    private static final String KEY_COMPANY_CEO = "101";
    private static final String KEY_COMPANY_BIZREGNUMBER = "102";
    private static final String KEY_COMPANY_ITCREGNUMBER = "103";
    private static final String KEY_COMPANY_ADDRESS = "104";
    private static final String KEY_COMPANY_PHONENUMBER = "105";


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

    public void clear()
    {
        if (mEditor != null)
        {
            mEditor.clear();
            mEditor.apply();
        }
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

    public String getLastMenu()
    {
        String result = null;

        if (mPreferences != null)
        {
            result = mPreferences.getString(KEY_LAST_MENU, null);
        }

        return result;
    }

    public void setLastMenu(String menu)
    {
        if (mEditor != null)
        {
            mEditor.putString(KEY_LAST_MENU, menu);
            mEditor.apply();
        }
    }

    public boolean isShowGuide()
    {
        boolean result = false;

        if (mPreferences != null)
        {
            result = mPreferences.getBoolean(KEY_SHOW_GUIDE, false);
        }

        return result;
    }

    public void setShowGuide(boolean isShow)
    {
        if (mEditor != null)
        {
            mEditor.putBoolean(KEY_SHOW_GUIDE, isShow);
            mEditor.apply();
        }
    }

    public boolean isAllowPush()
    {
        boolean result = false;

        if (mPreferences != null)
        {
            result = mPreferences.getBoolean(KEY_ALLOW_PUSH, true);
        }

        return result;
    }

    public void setAllowPush(boolean allow)
    {
        if (mEditor != null)
        {
            mEditor.putBoolean(KEY_ALLOW_PUSH, allow);
            mEditor.apply();
        }
    }

    public void setCompanyName(String text)
    {
        if (mEditor != null)
        {
            mEditor.putString(KEY_COMPANY_NAME, text);
            mEditor.apply();
        }
    }

    public void setCompanyCEO(String text)
    {
        if (mEditor != null)
        {
            mEditor.putString(KEY_COMPANY_CEO, text);
            mEditor.apply();
        }
    }

    public void setCompanyBizRegNumber(String text)
    {
        if (mEditor != null)
        {
            mEditor.putString(KEY_COMPANY_BIZREGNUMBER, text);
            mEditor.apply();
        }
    }

    public void setCompanyItcRegNumber(String text)
    {
        if (mEditor != null)
        {
            mEditor.putString(KEY_COMPANY_ITCREGNUMBER, text);
            mEditor.apply();
        }
    }

    public void setCompanyAddress(String text)
    {
        if (mEditor != null)
        {
            mEditor.putString(KEY_COMPANY_ADDRESS, text);
            mEditor.apply();
        }
    }

    public void setCompanyPhoneNumber(String text)
    {
        if (mEditor != null)
        {
            mEditor.putString(KEY_COMPANY_PHONENUMBER, text);
            mEditor.apply();
        }
    }

    public String getCompanyName()
    {
        String result = null;

        if (mPreferences != null)
        {
            result = mPreferences.getString(KEY_COMPANY_NAME, null);
        }

        return result;
    }

    public String getCompanyCEO()
    {
        String result = null;

        if (mPreferences != null)
        {
            result = mPreferences.getString(KEY_COMPANY_CEO, null);
        }

        return result;
    }

    public String getCompanyBizRegNumber()
    {
        String result = null;

        if (mPreferences != null)
        {
            result = mPreferences.getString(KEY_COMPANY_BIZREGNUMBER, null);
        }

        return result;
    }

    public String getCompanyItcRegNumber()
    {
        String result = null;

        if (mPreferences != null)
        {
            result = mPreferences.getString(KEY_COMPANY_ITCREGNUMBER, null);
        }

        return result;
    }

    public String getCompanyAddress()
    {
        String result = null;

        if (mPreferences != null)
        {
            result = mPreferences.getString(KEY_COMPANY_ADDRESS, null);
        }

        return result;
    }

    public String getCompanyPhoneNumber()
    {
        String result = null;

        if (mPreferences != null)
        {
            result = mPreferences.getString(KEY_COMPANY_PHONENUMBER, null);
        }

        return result;
    }
}
