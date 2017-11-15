package com.daily.dailyhotel.storage.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.DailyHotel;

/**
 */
public class DailyCartPreference
{
    public static final String DAILYHOTEL_SHARED_PREFERENCE_CART = "dailyCart";

    /////////////////////////////////////////////////////////////////////////////////////////
    //
    /////////////////////////////////////////////////////////////////////////////////////////


    private static final String KEY_GOURMET_CART = "1";


    /////////////////////////////////////////////////////////////////////////////////////////
    // "dailyCart" Preference
    /////////////////////////////////////////////////////////////////////////////////////////

    private static DailyCartPreference mInstance;
    private SharedPreferences mPreferences;
    private Editor mEditor;

    private DailyCartPreference(Context context)
    {
        mPreferences = context.getSharedPreferences(DAILYHOTEL_SHARED_PREFERENCE_CART, Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }

    public static synchronized DailyCartPreference getInstance(Context context)
    {
        if (mInstance == null)
        {
            mInstance = new DailyCartPreference(context);
        }

        return mInstance;
    }

    /**
     * 앱 삭제시에도 해당 데이터는 남기도록 한다.
     */
    public void clear()
    {
        // 해택 알림 내용은 유지 하도록 한다. 단 로그인시에는 서버에서 다시 가져와서 세팅한다.
        if (mEditor != null)
        {
            mEditor.clear();
            mEditor.apply();
        }

        DailyHotel.AUTHORIZATION = null;
    }

    private String getValue(SharedPreferences sharedPreferences, String key, String defaultValue)
    {
        String result = defaultValue;

        if (sharedPreferences != null)
        {
            result = sharedPreferences.getString(key, defaultValue);
        }

        return result;
    }

    private void setValue(Editor editor, String key, String value)
    {
        if (editor != null)
        {
            if (DailyTextUtils.isTextEmpty(value) == true)
            {
                editor.remove(key);
            } else
            {
                editor.putString(key, value);
            }

            editor.apply();
        }
    }

    private boolean getValue(SharedPreferences sharedPreferences, String key, boolean defaultValue)
    {
        boolean result = defaultValue;

        if (sharedPreferences != null)
        {
            result = sharedPreferences.getBoolean(key, defaultValue);
        }

        return result;
    }

    private void setValue(Editor editor, String key, boolean value)
    {
        if (editor != null)
        {
            editor.putBoolean(key, value);
            editor.apply();
        }
    }

    private long getValue(SharedPreferences sharedPreferences, String key, long defaultValue)
    {
        long result = defaultValue;

        if (sharedPreferences != null)
        {
            result = sharedPreferences.getLong(key, defaultValue);
        }

        return result;
    }

    private void setValue(Editor editor, String key, int value)
    {
        if (editor != null)
        {
            editor.putInt(key, value);
            editor.apply();
        }
    }

    private int getValue(SharedPreferences sharedPreferences, String key, int defaultValue)
    {
        int result = defaultValue;

        if (sharedPreferences != null)
        {
            result = sharedPreferences.getInt(key, defaultValue);
        }

        return result;
    }

    private void setValue(Editor editor, String key, long value)
    {
        if (editor != null)
        {
            editor.putLong(key, value);
            editor.apply();
        }
    }

    private void removeValue(Editor editor, String key)
    {
        if (editor != null)
        {
            editor.remove(key);
            editor.apply();
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////


    public String getGourmetCart()
    {
        return getValue(mPreferences, KEY_GOURMET_CART, null);
    }

    public void setGourmetCart(String value)
    {
        setValue(mEditor, KEY_GOURMET_CART, value);
    }
}
