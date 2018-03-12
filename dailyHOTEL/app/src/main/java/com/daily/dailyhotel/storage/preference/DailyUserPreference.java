package com.daily.dailyhotel.storage.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.util.Crypto;

/**
 */
public class DailyUserPreference
{
    public static final String DAILYHOTEL_SHARED_PREFERENCE_USER = "dailyUser";

    /////////////////////////////////////////////////////////////////////////////////////////
    //
    /////////////////////////////////////////////////////////////////////////////////////////

    // User
    private static final String KEY_EMAIL = "1";
    private static final String KEY_TYPE = "2";
    private static final String KEY_NAME = "3";
    private static final String KEY_RECOMMENDER = "4";
    private static final String KEY_BENEFIT_ALARM = "5";
    private static final String KEY_IS_EXCEED_BONUS = "6";
    private static final String KEY_BIRTHDAY = "7";
    private static final String KEY_AUTHORIZATION = "8";
    private static final String KEY_THANK_YOU_BENEFIT_ALARM = "9";

    // Payment
    private static final String KEY_OVERSEAS_NAME = "20";
    private static final String KEY_OVERSEAS_PHONE = "21";
    private static final String KEY_OVERSEAS_EMAIL = "22";
    private static final String KEY_OVERSEAS_FIRST_NAME = "23";
    private static final String KEY_OVERSEAS_LAST_NAME = "24";

    /////////////////////////////////////////////////////////////////////////////////////////
    // "dailyUser" Preference
    /////////////////////////////////////////////////////////////////////////////////////////

    private static DailyUserPreference mInstance;
    private SharedPreferences mPreferences;
    private Editor mEditor;

    private DailyUserPreference(Context context)
    {
        mPreferences = context.getSharedPreferences(DAILYHOTEL_SHARED_PREFERENCE_USER, Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }

    public static synchronized DailyUserPreference getInstance(Context context)
    {
        if (mInstance == null)
        {
            mInstance = new DailyUserPreference(context);
        }

        return mInstance;
    }

    /**
     * 앱 삭제시에도 해당 데이터는 남기도록 한다.
     */
    public void clear()
    {
        boolean isUserBenefitAlarm = isBenefitAlarm();

        // 해택 알림 내용은 유지 하도록 한다. 단 로그인시에는 서버에서 다시 가져와서 세팅한다.
        if (mEditor != null)
        {
            mEditor.clear();
            mEditor.apply();
        }

        setBenefitAlarm(isUserBenefitAlarm);

        DailyHotel.AUTHORIZATION = null;
    }

    private String getValueDecrypt(SharedPreferences sharedPreferences, String key, String defaultValue)
    {
        String result = defaultValue;

        if (sharedPreferences != null)
        {
            result = Crypto.urlDecrypt(sharedPreferences.getString(key, defaultValue));
        }

        return result;
    }

    private void setValueEncrypt(Editor editor, String key, String value)
    {
        if (editor != null)
        {
            if (DailyTextUtils.isTextEmpty(value) == true)
            {
                editor.remove(key);
            } else
            {
                editor.putString(key, Crypto.urlEncrypt(value));
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

    public String getOverseasName()
    {
        return getValueDecrypt(mPreferences, KEY_OVERSEAS_NAME, null);
    }

    public String getOverseasFirstName()
    {
        return getValueDecrypt(mPreferences, KEY_OVERSEAS_FIRST_NAME, null);
    }

    public String getOverseasLastName()
    {
        return getValueDecrypt(mPreferences, KEY_OVERSEAS_LAST_NAME, null);
    }

    public String getOverseasPhone()
    {
        return getValueDecrypt(mPreferences, KEY_OVERSEAS_PHONE, null);
    }

    public String getOverseasEmail()
    {
        return getValueDecrypt(mPreferences, KEY_OVERSEAS_EMAIL, null);
    }

    public void setOverseasInformation(String name, String phone, String email)
    {
        if (mEditor != null)
        {
            setValueEncrypt(mEditor, KEY_OVERSEAS_NAME, name);
            setValueEncrypt(mEditor, KEY_OVERSEAS_PHONE, phone);
            setValueEncrypt(mEditor, KEY_OVERSEAS_EMAIL, email);
            mEditor.apply();
        }
    }

    public void setOverseasInformation(String firstName, String lastName, String phone, String email)
    {
        if (mEditor != null)
        {
            setValueEncrypt(mEditor, KEY_OVERSEAS_FIRST_NAME, firstName);
            setValueEncrypt(mEditor, KEY_OVERSEAS_LAST_NAME, lastName);
            setValueEncrypt(mEditor, KEY_OVERSEAS_PHONE, phone);
            setValueEncrypt(mEditor, KEY_OVERSEAS_EMAIL, email);
            mEditor.apply();
        }
    }

    public String getType()
    {
        return getValueDecrypt(mPreferences, KEY_TYPE, null);
    }

    public String getName()
    {
        return getValueDecrypt(mPreferences, KEY_NAME, null);
    }

    public String getEmail()
    {
        return getValueDecrypt(mPreferences, KEY_EMAIL, null);
    }

    public String getBirthday()
    {
        return getValueDecrypt(mPreferences, KEY_BIRTHDAY, null);
    }

    public String getRecommender()
    {
        return getValueDecrypt(mPreferences, KEY_RECOMMENDER, null);
    }

    public boolean isBenefitAlarm()
    {
        return getValue(mPreferences, KEY_BENEFIT_ALARM, false);
    }

    public void setBenefitAlarm(boolean value)
    {
        setValue(mEditor, KEY_BENEFIT_ALARM, value);
    }

    public boolean isThankYouBenefitAlarmEnabled()
    {
        return getValue(mPreferences, KEY_THANK_YOU_BENEFIT_ALARM, false);
    }

    public void setThankYouBenefitAlarmEnabled(boolean value)
    {
        setValue(mEditor, KEY_THANK_YOU_BENEFIT_ALARM, value);
    }

    public boolean isExceedBonus()
    {
        return getValue(mPreferences, KEY_IS_EXCEED_BONUS, false);
    }

    public void setExceedBonus(boolean value)
    {
        setValue(mEditor, KEY_IS_EXCEED_BONUS, value);
    }

    public void setInformation(String type, String email, String name, String birthday, String recommender)
    {
        if (mEditor != null)
        {
            setValueEncrypt(mEditor, KEY_TYPE, type);
            setValueEncrypt(mEditor, KEY_EMAIL, email);
            setValueEncrypt(mEditor, KEY_NAME, name);
            setValueEncrypt(mEditor, KEY_BIRTHDAY, birthday);
            setValueEncrypt(mEditor, KEY_RECOMMENDER, recommender);
            mEditor.apply();
        }
    }

    public void removeInformation()
    {
        if (mEditor != null)
        {
            mEditor.remove(KEY_TYPE);
            mEditor.remove(KEY_EMAIL);
            mEditor.remove(KEY_NAME);
            mEditor.remove(KEY_BIRTHDAY);
            mEditor.remove(KEY_RECOMMENDER);
            mEditor.remove(KEY_AUTHORIZATION);

            DailyHotel.AUTHORIZATION = null;

            mEditor.apply();
        }
    }

    public String getAuthorization()
    {
        return getValueDecrypt(mPreferences, KEY_AUTHORIZATION, null);
    }

    public void setAuthorization(String value)
    {
        DailyHotel.AUTHORIZATION = value;

        setValueEncrypt(mEditor, KEY_AUTHORIZATION, value);
    }
}
