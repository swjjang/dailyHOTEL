package com.twoheart.dailyhotel.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.crashlytics.android.Crashlytics;
import com.daily.base.util.DailyTextUtils;

/**
 */
public class DailyRemoteConfigPreference
{
    public static final String PREFERENCE_REMOTE_CONFIG = "DH_RemoteConfig";

    /////////////////////////////////////////////////////////////////////////////////////////
    // Remote Config Preference
    /////////////////////////////////////////////////////////////////////////////////////////

    // remote config text
    private static final String KEY_REMOTE_CONFIG_INTRO_VERSION = "1";
    private static final String KEY_REMOTE_CONFIG_INTRO_NEW_VERSION = "2";
    private static final String KEY_REMOTE_CONFIG_INTRO_NEW_URL = "3";

    private static final String KEY_REMOTE_CONFIG_TEXT_VERSION = "100";
    private static final String KEY_REMOTE_CONFIG_TEXT_LOGINTEXT01 = "101";
    private static final String KEY_REMOTE_CONFIG_TEXT_SIGNUPTEXT01 = "102";
    private static final String KEY_REMOTE_CONFIG_TEXT_SIGNUPTEXT02 = "103";
    private static final String KEY_REMOTE_CONFIG_TEXT = "110";

    private static final String KEY_REMOTE_CONFIG_COMPANY_NAME = "200";
    private static final String KEY_REMOTE_CONFIG_COMPANY_CEO = "201";
    private static final String KEY_REMOTE_CONFIG_COMPANY_BIZREGNUMBER = "202";
    private static final String KEY_REMOTE_CONFIG_COMPANY_ITCREGNUMBER = "203";
    private static final String KEY_REMOTE_CONFIG_COMPANY_ADDRESS = "204";
    private static final String KEY_REMOTE_CONFIG_COMPANY_PHONENUMBER = "205";
    private static final String KEY_REMOTE_CONFIG_COMPANY_FAX = "206";
    private static final String KEY_REMOTE_CONFIG_COMPANY_PRIVACY_EMAIL = "207";

    private static final String KEY_REMOTE_CONFIG_STAY_PAYMENT_IS_SIMPLECARD_ENABLED = "300";
    private static final String KEY_REMOTE_CONFIG_STAY_PAYMENT_IS_CARD_ENABLED = "301";
    private static final String KEY_REMOTE_CONFIG_STAY_PAYMENT_IS_PHONE_ENABLED = "302";
    private static final String KEY_REMOTE_CONFIG_STAY_PAYMENT_IS_VIRTUAL_ENABLED = "303";

    private static final String KEY_REMOTE_CONFIG_GOURMET_PAYMENT_IS_SIMPLECARD_ENABLED = "304";
    private static final String KEY_REMOTE_CONFIG_GOURMET_PAYMENT_IS_CARD_ENABLED = "305";
    private static final String KEY_REMOTE_CONFIG_GOURMET_PAYMENT_IS_PHONE_ENABLED = "306";
    private static final String KEY_REMOTE_CONFIG_GOURMET_PAYMENT_IS_VIRTUAL_ENABLED = "307";

    private static final String KEY_REMOTE_CONFIG_HOME_MESSAGE_AREA_LOGIN_ENABLED = "310";
    private static final String KEY_REMOTE_CONFIG_HOME_MESSAGE_AREA_LOGOUT_ENABLED = "311";
    private static final String KEY_REMOTE_CONFIG_HOME_MESSAGE_AREA_LOGOUT_TITLE = "312";
    private static final String KEY_REMOTE_CONFIG_HOME_MESSAGE_AREA_LOGOUT_CTA = "313";

    private static final String KEY_REMOTE_CONFIG_HOME_EVENT_CURRENT_VERSION = "314";
    private static final String KEY_REMOTE_CONFIG_HOME_EVENT_TITLE = "315";
    private static final String KEY_REMOTE_CONFIG_HOME_EVENT_URL = "316";
    private static final String KEY_REMOTE_CONFIG_HOME_EVENT_INDEX = "317";

    // Stamp
    private static final String KEY_REMOTE_CONFIG_STAMP_ENABLED = "318";
    private static final String KEY_REMOTE_CONFIG_STAMP_STAY_DETAIL_MESSAGE1 = "319";
    private static final String KEY_REMOTE_CONFIG_STAMP_STAY_DETAIL_MESSAGE2 = "320";
    private static final String KEY_REMOTE_CONFIG_STAMP_STAY_DETAIL_MESSAGE3 = "321";
    private static final String KEY_REMOTE_CONFIG_STAMP_STAY_DETAIL_MESSAGE3_ENABLED = "322";
    private static final String KEY_REMOTE_CONFIG_STAMP_STAY_DETAIL_POPUP_TITLE = "323";
    private static final String KEY_REMOTE_CONFIG_STAMP_STAY_DETAIL_POPUP_MESSAGE = "324";
    private static final String KEY_REMOTE_CONFIG_STAMP_STAY_THANKYOU_MESSAGE1 = "325";
    private static final String KEY_REMOTE_CONFIG_STAMP_STAY_THANKYOU_MESSAGE2 = "326";
    private static final String KEY_REMOTE_CONFIG_STAMP_STAY_THANKYOU_MESSAGE3 = "327";
    private static final String KEY_REMOTE_CONFIG_STAMP_STAMP_DATE1 = "328";
    private static final String KEY_REMOTE_CONFIG_STAMP_STAMP_DATE2 = "329";
    private static final String KEY_REMOTE_CONFIG_STAMP_STAMP_DATE3 = "330";
    private static final String KEY_REMOTE_CONFIG_STAMP_END_EVENT_POPUP_ENABLED = "331";
    private static final String KEY_REMOTE_CONFIG_STAMP_STAMP_HOME_MESSAGE1 = "333";
    private static final String KEY_REMOTE_CONFIG_STAMP_STAMP_HOME_MESSAGE2 = "334";
    private static final String KEY_REMOTE_CONFIG_STAMP_STAMP_HOME_ENABLED = "335";

    // Home - Category
    private static final String KEY_REMOTE_CONFIG_HOME_CATEGORY_ENABLED = "332";

    //    private static final String KEY_REMOTE_CONFIG_ABTEST_GOURMET_PRODUCT_LIST = "340";
    //    private static final String KEY_REMOTE_CONFIG_ABTEST_HOME_BUTTON = "341";

    private static final String KEY_REMOTE_CONFIG_UPDATE_OPTIONAL = "342";
    private static final String KEY_REMOTE_CONFIG_UPDATE_FORCE = "343";
    private static final String KEY_REMOTE_CONFIG_OPERATION_LUNCH = "344";

    // Boutique BM - Test
    private static final String KEY_REMOTE_CONFIG_BOUTIQUE_BM = "350";

    private static final String KEY_REMOTE_CONFIG_STAY_OUTBOUND_PAYMENT_IS_SIMPLECARD_ENABLED = "360";
    private static final String KEY_REMOTE_CONFIG_STAY_OUTBOUND_PAYMENT_IS_CARD_ENABLED = "361";
    private static final String KEY_REMOTE_CONFIG_STAY_OUTBOUND_PAYMENT_IS_PHONE_ENABLED = "362";

    // Static URL
    private static final String KEY_REMOTE_CONFIG_STATIC_URL_PRIVACY = "370";
    private static final String KEY_REMOTE_CONFIG_STATIC_URL_TERMS = "371";
    private static final String KEY_REMOTE_CONFIG_STATIC_URL_ABOUT = "372";
    private static final String KEY_REMOTE_CONFIG_STATIC_URL_LOCATION = "373";
    private static final String KEY_REMOTE_CONFIG_STATIC_URL_CHILDPROTECT = "374";
    private static final String KEY_REMOTE_CONFIG_STATIC_URL_BONUS = "375";
    private static final String KEY_REMOTE_CONFIG_STATIC_URL_COUPON = "376";
    private static final String KEY_REMOTE_CONFIG_STATIC_URL_PRODCOUPONNOTE = "377";
    private static final String KEY_REMOTE_CONFIG_STATIC_URL_DEVCOUPONNOTE = "378";
    private static final String KEY_REMOTE_CONFIG_STATIC_URL_FAQ = "379";
    private static final String KEY_REMOTE_CONFIG_STATIC_URL_LICENSE = "380";
    private static final String KEY_REMOTE_CONFIG_STATIC_URL_STAMP = "381";
    private static final String KEY_REMOTE_CONFIG_STATIC_URL_REVIEW = "382";
    private static final String KEY_REMOTE_CONFIG_STATIC_URL_LIFESTYLEPROJECT = "383";
    private static final String KEY_REMOTE_CONFIG_STATIC_URL_DAILYSTAMPHOME = "384";
    private static final String KEY_REMOTE_CONFIG_STATIC_URL_COLLECTPERSONAL = "385";

    // A/B Test
    private static final String KEY_REMOTE_CONFIG_STAY_RANK_TEST_NAME = "1000";
    private static final String KEY_REMOTE_CONFIG_STAY_RANK_TEST_TYPE = "1001";


    private static DailyRemoteConfigPreference mInstance;
    private SharedPreferences mRemoteConfigPreferences;
    private Editor mRemoteConfigEditor;

    private DailyRemoteConfigPreference(Context context)
    {
        mRemoteConfigPreferences = context.getSharedPreferences(PREFERENCE_REMOTE_CONFIG, Context.MODE_PRIVATE);
        mRemoteConfigEditor = mRemoteConfigPreferences.edit();
    }

    public static synchronized DailyRemoteConfigPreference getInstance(Context context)
    {
        if (mInstance == null)
        {
            mInstance = new DailyRemoteConfigPreference(context);
        }

        return mInstance;
    }

    private String getValue(SharedPreferences sharedPreferences, String key, String defaultValue)
    {
        String result = defaultValue;

        try
        {
            if (sharedPreferences != null)
            {
                result = sharedPreferences.getString(key, defaultValue);
            }

        } catch (ClassCastException e)
        {
            if (Constants.DEBUG == false)
            {
                try
                {
                    Object object = sharedPreferences.getAll().get(key);
                    String msg = "key : " + key + ", value : " + sharedPreferences.getAll().get(key) + ", Type : " + object.toString();

                    Crashlytics.log(msg);
                    Crashlytics.logException(e);
                } catch (Exception e1)
                {

                }
            }

            if (sharedPreferences != null)
            {
                sharedPreferences.edit().remove(key);
                sharedPreferences.edit().putString(key, defaultValue);
                sharedPreferences.edit().apply();
            }
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

        try
        {
            if (sharedPreferences != null)
            {
                result = sharedPreferences.getBoolean(key, defaultValue);
            }

        } catch (ClassCastException e)
        {
            if (Constants.DEBUG == false)
            {
                try
                {
                    Object object = sharedPreferences.getAll().get(key);
                    String msg = "key : " + key + ", value : " + sharedPreferences.getAll().get(key) + ", Type : " + object.toString();

                    Crashlytics.log(msg);
                    Crashlytics.logException(e);
                } catch (Exception e1)
                {

                }
            }

            if (sharedPreferences != null)
            {
                sharedPreferences.edit().remove(key);
                sharedPreferences.edit().putBoolean(key, defaultValue);
                sharedPreferences.edit().apply();
            }
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

        try
        {
            if (sharedPreferences != null)
            {
                result = sharedPreferences.getLong(key, defaultValue);
            }

        } catch (ClassCastException e)
        {
            if (Constants.DEBUG == false)
            {
                try
                {
                    Object object = sharedPreferences.getAll().get(key);
                    String msg = "key : " + key + ", value : " + sharedPreferences.getAll().get(key) + ", Type : " + object.toString();

                    Crashlytics.log(msg);
                    Crashlytics.logException(e);
                } catch (Exception e1)
                {

                }
            }

            if (sharedPreferences != null)
            {
                sharedPreferences.edit().remove(key);
                sharedPreferences.edit().putLong(key, defaultValue);
                sharedPreferences.edit().apply();
            }
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

        try
        {
            if (sharedPreferences != null)
            {
                result = sharedPreferences.getInt(key, defaultValue);
            }

        } catch (ClassCastException e)
        {
            if (Constants.DEBUG == false)
            {
                try
                {
                    Object object = sharedPreferences.getAll().get(key);
                    String msg = "key : " + key + ", value : " + sharedPreferences.getAll().get(key) + ", Type : " + object.toString();

                    Crashlytics.log(msg);
                    Crashlytics.logException(e);
                } catch (Exception e1)
                {

                }
            }

            if (sharedPreferences != null)
            {
                sharedPreferences.edit().remove(key);
                sharedPreferences.edit().putInt(key, defaultValue);
                sharedPreferences.edit().apply();
            }
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
    // Remote Config Text
    /////////////////////////////////////////////////////////////////////////////////////////

    public String getRemoteConfigIntroImageVersion()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_INTRO_VERSION, Constants.DAILY_INTRO_CURRENT_VERSION);
    }

    public void setRemoteConfigIntroImageVersion(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_INTRO_VERSION, value);
    }

    public String getRemoteConfigIntroImageNewVersion()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_INTRO_NEW_VERSION, null);
    }

    public void setRemoteConfigIntroImageNewVersion(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_INTRO_NEW_VERSION, value);
    }

    public String getRemoteConfigIntroImageNewUrl()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_INTRO_NEW_URL, null);
    }

    public void setRemoteConfigIntroImageNewUrl(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_INTRO_NEW_URL, value);
    }

    public void setRemoteConfigText(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_TEXT, value);
    }

    public String getRemoteConfigText()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_TEXT, null);
    }

    public void setRemoteConfigTextVersion(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_TEXT_VERSION, value);
    }

    public String getRemoteConfigTextVersion()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_TEXT_VERSION, null);
    }

    public void setRemoteConfigTextLoginText01(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_TEXT_LOGINTEXT01, value);
    }

    public String getRemoteConfigTextLoginText01()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_TEXT_LOGINTEXT01, null);
    }

    public void setRemoteConfigTextSignUpText01(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_TEXT_SIGNUPTEXT01, value);
    }

    public String getRemoteConfigTextSignUpText01()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_TEXT_SIGNUPTEXT01, null);
    }

    public void setRemoteConfigTextSignUpText02(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_TEXT_SIGNUPTEXT02, value);
    }

    public String getRemoteConfigTextSignUpText02()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_TEXT_SIGNUPTEXT02, null);
    }

    public void setRemoteConfigCompanyInformation(String name, String ceo, String bizRegNumber//
        , String itcRegNumber, String address, String phoneNumber, String fax, String email)
    {
        if (mRemoteConfigEditor != null)
        {
            mRemoteConfigEditor.putString(KEY_REMOTE_CONFIG_COMPANY_NAME, name);
            mRemoteConfigEditor.putString(KEY_REMOTE_CONFIG_COMPANY_CEO, ceo);
            mRemoteConfigEditor.putString(KEY_REMOTE_CONFIG_COMPANY_BIZREGNUMBER, bizRegNumber);
            mRemoteConfigEditor.putString(KEY_REMOTE_CONFIG_COMPANY_ITCREGNUMBER, itcRegNumber);
            mRemoteConfigEditor.putString(KEY_REMOTE_CONFIG_COMPANY_ADDRESS, address);
            mRemoteConfigEditor.putString(KEY_REMOTE_CONFIG_COMPANY_PHONENUMBER, phoneNumber);
            mRemoteConfigEditor.putString(KEY_REMOTE_CONFIG_COMPANY_FAX, fax);

            if (DailyTextUtils.isTextEmpty(email) == false)
            {
                mRemoteConfigEditor.putString(KEY_REMOTE_CONFIG_COMPANY_PRIVACY_EMAIL, email);
            }

            mRemoteConfigEditor.apply();
        }
    }

    public String getRemoteConfigCompanyName()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_COMPANY_NAME, null);
    }

    public String getRemoteConfigCompanyCEO()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_COMPANY_CEO, null);
    }

    public String getRemoteConfigCompanyBizRegNumber()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_COMPANY_BIZREGNUMBER, null);
    }

    public String getRemoteConfigCompanyItcRegNumber()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_COMPANY_ITCREGNUMBER, null);
    }

    public String getRemoteConfigCompanyAddress()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_COMPANY_ADDRESS, null);
    }

    public String getRemoteConfigCompanyPhoneNumber()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_COMPANY_PHONENUMBER, null);
    }

    public String getRemoteConfigCompanyFax()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_COMPANY_FAX, null);
    }

    public String getRemoteConfigCompanyPrivacyEmail()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_COMPANY_PRIVACY_EMAIL, "privacy.korea@dailyhotel.com");
    }

    public void setRemoteConfigStaySimpleCardPaymentEnabled(boolean value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STAY_PAYMENT_IS_SIMPLECARD_ENABLED, value);
    }

    public boolean isRemoteConfigStaySimpleCardPaymentEnabled()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STAY_PAYMENT_IS_SIMPLECARD_ENABLED, true);
    }

    public void setRemoteConfigStayCardPaymentEnabled(boolean value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STAY_PAYMENT_IS_CARD_ENABLED, value);
    }

    public boolean isRemoteConfigStayCardPaymentEnabled()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STAY_PAYMENT_IS_CARD_ENABLED, true);
    }

    public void setRemoteConfigStayPhonePaymentEnabled(boolean value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STAY_PAYMENT_IS_PHONE_ENABLED, value);
    }

    public boolean isRemoteConfigStayPhonePaymentEnabled()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STAY_PAYMENT_IS_PHONE_ENABLED, true);
    }

    public void setRemoteConfigStayVirtualPaymentEnabled(boolean value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STAY_PAYMENT_IS_VIRTUAL_ENABLED, value);
    }

    public boolean isRemoteConfigStayVirtualPaymentEnabled()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STAY_PAYMENT_IS_VIRTUAL_ENABLED, true);
    }

    public void setRemoteConfigGourmetSimpleCardPaymentEnabled(boolean value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_GOURMET_PAYMENT_IS_SIMPLECARD_ENABLED, value);
    }

    public boolean isRemoteConfigGourmetSimpleCardPaymentEnabled()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_GOURMET_PAYMENT_IS_SIMPLECARD_ENABLED, true);
    }

    public void setRemoteConfigGourmetCardPaymentEnabled(boolean value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_GOURMET_PAYMENT_IS_CARD_ENABLED, value);
    }

    public boolean isRemoteConfigGourmetCardPaymentEnabled()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_GOURMET_PAYMENT_IS_CARD_ENABLED, true);
    }

    public void setRemoteConfigGourmetPhonePaymentEnabled(boolean value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_GOURMET_PAYMENT_IS_PHONE_ENABLED, value);
    }

    public boolean isRemoteConfigGourmetPhonePaymentEnabled()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_GOURMET_PAYMENT_IS_PHONE_ENABLED, true);
    }

    public void setRemoteConfigGourmetVirtualPaymentEnabled(boolean value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_GOURMET_PAYMENT_IS_VIRTUAL_ENABLED, value);
    }

    public boolean isRemoteConfigGourmetVirtualPaymentEnabled()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_GOURMET_PAYMENT_IS_VIRTUAL_ENABLED, true);
    }

    public void setRemoteConfigHomeMessageAreaLoginEnabled(boolean value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_HOME_MESSAGE_AREA_LOGIN_ENABLED, value);
    }

    public boolean isRemoteConfigHomeMessageAreaLoginEnabled()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_HOME_MESSAGE_AREA_LOGIN_ENABLED, true);
    }

    public void setRemoteConfigHomeMessageAreaLogoutEnabled(boolean value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_HOME_MESSAGE_AREA_LOGOUT_ENABLED, value);
    }

    public boolean isRemoteConfigHomeMessageAreaLogoutEnabled()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_HOME_MESSAGE_AREA_LOGOUT_ENABLED, true);
    }

    public void setRemoteConfigHomeMessageAreaLogoutTitle(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_HOME_MESSAGE_AREA_LOGOUT_TITLE, value);
    }

    public String getRemoteConfigHomeMessageAreaLogoutTitle()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_HOME_MESSAGE_AREA_LOGOUT_TITLE, null);
    }

    public void setRemoteConfigHomeMessageAreaLogoutCallToAction(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_HOME_MESSAGE_AREA_LOGOUT_CTA, value);
    }

    public String getRemoteConfigHomeMessageAreaLogoutCallToAction()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_HOME_MESSAGE_AREA_LOGOUT_CTA, null);
    }

    public void setRemoteConfigHomeEventCurrentVersion(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_HOME_EVENT_CURRENT_VERSION, value);
    }

    public String getRemoteConfigHomeEventCurrentVersion()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_HOME_EVENT_CURRENT_VERSION, null);
    }

    public void setRemoteConfigHomeEventUrl(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_HOME_EVENT_URL, value);
    }

    public String getRemoteConfigHomeEventUrl()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_HOME_EVENT_URL, null);
    }

    public void setRemoteConfigHomeEventTitle(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_HOME_EVENT_TITLE, value);
    }

    public String getRemoteConfigHomeEventTitle()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_HOME_EVENT_TITLE, null);
    }

    public void setRemoteConfigHomeEventIndex(int index)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_HOME_EVENT_INDEX, index);
    }

    public int getRemoteConfigHomeEventIndex()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_HOME_EVENT_INDEX, -1);
    }

    public void setRemoteConfigHomeCategoryEnabled(boolean enabled)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_HOME_CATEGORY_ENABLED, enabled);
    }

    public boolean getRemoteConfigHomeCategoryEnabled()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_HOME_CATEGORY_ENABLED, false);
    }

    public void setRemoteConfigStampEnabled(boolean enabled)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STAMP_ENABLED, enabled);
    }

    public boolean isRemoteConfigStampEnabled()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STAMP_ENABLED, false);
    }

    public void setRemoteConfigStampStayDetailMessage(String message1, String message2, String message3, boolean message3Enabled)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STAMP_STAY_DETAIL_MESSAGE1, message1);
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STAMP_STAY_DETAIL_MESSAGE2, message2);
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STAMP_STAY_DETAIL_MESSAGE3, message3);
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STAMP_STAY_DETAIL_MESSAGE3_ENABLED, message3Enabled);
    }

    public String getRemoteConfigStampStayDetailMessage1()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STAMP_STAY_DETAIL_MESSAGE1, null);
    }

    public String getRemoteConfigStampStayDetailMessage2()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STAMP_STAY_DETAIL_MESSAGE2, null);
    }

    public String getRemoteConfigStampStayDetailMessage3()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STAMP_STAY_DETAIL_MESSAGE3, null);
    }

    public boolean isRemoteConfigStampStayDetailMessage3Enabled()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STAMP_STAY_DETAIL_MESSAGE3_ENABLED, false);
    }

    public void setRemoteConfigStampStayDetailPopup(String title, String message)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STAMP_STAY_DETAIL_POPUP_TITLE, title);
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STAMP_STAY_DETAIL_POPUP_MESSAGE, message);
    }

    public String getRemoteConfigStampStayDetailPopupTitle()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STAMP_STAY_DETAIL_POPUP_TITLE, null);
    }

    public String getRemoteConfigStampStayDetailPopupMessage()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STAMP_STAY_DETAIL_POPUP_MESSAGE, null);
    }

    public void setRemoteConfigStampStayThankYouMessage(String message1, String message2, String message3)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STAMP_STAY_THANKYOU_MESSAGE1, message1);
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STAMP_STAY_THANKYOU_MESSAGE2, message2);
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STAMP_STAY_THANKYOU_MESSAGE3, message3);
    }

    public String getRemoteConfigStampStayThankYouMessage1()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STAMP_STAY_THANKYOU_MESSAGE1, null);
    }

    public String getRemoteConfigStampStayThankYouMessage2()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STAMP_STAY_THANKYOU_MESSAGE2, null);
    }

    public String getRemoteConfigStampStayThankYouMessage3()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STAMP_STAY_THANKYOU_MESSAGE3, null);
    }

    public void setRemoteConfigStampStayEndEventPopupEnabled(boolean value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STAMP_END_EVENT_POPUP_ENABLED, value);
    }

    public boolean isRemoteConfigStampStayEndEventPopupEnabled()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STAMP_END_EVENT_POPUP_ENABLED, true);
    }

    public void setRemoteConfigStampDate(String date1, String date2, String date3)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STAMP_STAMP_DATE1, date1);
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STAMP_STAMP_DATE2, date2);
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STAMP_STAMP_DATE3, date3);
    }

    public String getRemoteConfigStampDate1()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STAMP_STAMP_DATE1, null);
    }

    public String getRemoteConfigStampDate2()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STAMP_STAMP_DATE2, null);
    }

    public String getRemoteConfigStampDate3()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STAMP_STAMP_DATE3, null);
    }

    public void setRemoteConfigUpdateOptional(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_UPDATE_OPTIONAL, value);
    }

    public void setRemoteConfigStampHomeMessage(String message1, String message2, boolean enabled)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STAMP_STAMP_HOME_MESSAGE1, message1);
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STAMP_STAMP_HOME_MESSAGE2, message2);
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STAMP_STAMP_HOME_ENABLED, enabled);
    }

    public String getRemoteConfigStampHomeMessage1()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STAMP_STAMP_HOME_MESSAGE1, null);
    }

    public String getRemoteConfigStampHomeMessage2()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STAMP_STAMP_HOME_MESSAGE2, null);
    }

    public boolean isRemoteConfigStampHomeEnabled()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STAMP_STAMP_HOME_ENABLED, false);
    }

    public String getRemoteConfigUpdateOptional()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_UPDATE_OPTIONAL, null);
    }

    public void setRemoteConfigUpdateForce(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_UPDATE_FORCE, value);
    }

    public String getRemoteConfigUpdateForce()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_UPDATE_FORCE, null);
    }

    public void setRemoteConfigOperationLunchTime(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_OPERATION_LUNCH, value);
    }

    public String getRemoteConfigOperationLunchTime()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_OPERATION_LUNCH, "11:50,13:00");
    }

    public void setRemoteConfigBoutiqueBMEnabled(boolean enabled)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_BOUTIQUE_BM, enabled);
    }

    public boolean isRemoteConfigBoutiqueBMEnabled()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_BOUTIQUE_BM, false);
    }

    public boolean isRemoteConfigStayOutboundSimpleCardPaymentEnabled()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STAY_OUTBOUND_PAYMENT_IS_SIMPLECARD_ENABLED, true);
    }

    public void setRemoteConfigStayOutboundSimpleCardPaymentEnabled(boolean value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STAY_OUTBOUND_PAYMENT_IS_SIMPLECARD_ENABLED, value);
    }

    public boolean isRemoteConfigStayOutboundCardPaymentEnabled()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STAY_OUTBOUND_PAYMENT_IS_CARD_ENABLED, true);
    }

    public void setRemoteConfigStayOutboundCardPaymentEnabled(boolean value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STAY_OUTBOUND_PAYMENT_IS_CARD_ENABLED, value);
    }

    public boolean isRemoteConfigStayOutboundPhonePaymentEnabled()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STAY_OUTBOUND_PAYMENT_IS_PHONE_ENABLED, true);
    }

    public void setRemoteConfigStayOutboundPhonePaymentEnabled(boolean value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STAY_OUTBOUND_PAYMENT_IS_PHONE_ENABLED, value);
    }

    public void setKeyRemoteConfigStaticUrlPrivacy(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STATIC_URL_PRIVACY, value);
    }

    public String getKeyRemoteConfigStaticUrlPrivacy()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STATIC_URL_PRIVACY, Crypto.getUrlDecoderEx(Constants.URL_WEB_PRIVACY));
    }

    public void setKeyRemoteConfigStaticUrlTerms(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STATIC_URL_TERMS, value);
    }

    public String getKeyRemoteConfigStaticUrlTerms()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STATIC_URL_TERMS, Crypto.getUrlDecoderEx(Constants.URL_WEB_TERMS));
    }

    public void setKeyRemoteConfigStaticUrlAbout(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STATIC_URL_ABOUT, value);
    }

    public String getKeyRemoteConfigStaticUrlAbout()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STATIC_URL_ABOUT, Crypto.getUrlDecoderEx(Constants.URL_WEB_ABOUT));
    }

    public void setKeyRemoteConfigStaticUrlLocation(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STATIC_URL_LOCATION, value);
    }

    public String getKeyRemoteConfigStaticUrlLocation()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STATIC_URL_LOCATION, Crypto.getUrlDecoderEx(Constants.URL_WEB_LOCATION_TERMS));
    }

    public void setKeyRemoteConfigStaticUrlChildProtect(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STATIC_URL_CHILDPROTECT, value);
    }

    public String getKeyRemoteConfigStaticUrlChildProtect()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STATIC_URL_CHILDPROTECT, Crypto.getUrlDecoderEx(Constants.URL_WEB_CHILD_PROTECT_TERMS));
    }

    public void setKeyRemoteConfigStaticUrlBonus(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STATIC_URL_BONUS, value);
    }

    public String getKeyRemoteConfigStaticUrlBonus()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STATIC_URL_BONUS, Crypto.getUrlDecoderEx(Constants.URL_WEB_BONUS_TERMS));
    }

    public void setKeyRemoteConfigStaticUrlCoupon(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STATIC_URL_COUPON, value);
    }

    public String getKeyRemoteConfigStaticUrlCoupon()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STATIC_URL_COUPON, Crypto.getUrlDecoderEx(Constants.URL_WEB_COMMON_COUPON_TERMS));
    }

    public void setKeyRemoteConfigStaticUrlProdCouponNote(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STATIC_URL_PRODCOUPONNOTE, value);
    }

    public String getKeyRemoteConfigStaticUrlProdCouponNote()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STATIC_URL_PRODCOUPONNOTE, Crypto.getUrlDecoderEx(Constants.URL_WEB_EACH_COUPON_TERMS));
    }

    public void setKeyRemoteConfigStaticUrlDevCouponNote(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STATIC_URL_DEVCOUPONNOTE, value);
    }

    public String getKeyRemoteConfigStaticUrlDevCouponNote()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STATIC_URL_DEVCOUPONNOTE, Crypto.getUrlDecoderEx(Constants.URL_WEB_EACH_COUPON_TERMS_DEV));
    }

    public void setKeyRemoteConfigStaticUrlFaq(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STATIC_URL_FAQ, value);
    }

    public String getKeyRemoteConfigStaticUrlFaq()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STATIC_URL_FAQ, Crypto.getUrlDecoderEx(Constants.URL_WEB_FAQ));
    }

    public void setKeyRemoteConfigStaticUrlLicense(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STATIC_URL_LICENSE, value);
    }

    public String getKeyRemoteConfigStaticUrlLicense()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STATIC_URL_LICENSE, Crypto.getUrlDecoderEx(Constants.URL_WEB_LICNESE));
    }

    public void setKeyRemoteConfigStaticUrlStamp(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STATIC_URL_STAMP, value);
    }

    public String getKeyRemoteConfigStaticUrlStamp()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STATIC_URL_STAMP, Crypto.getUrlDecoderEx(Constants.URL_WEB_STAMP_TERMS));
    }

    public void setKeyRemoteConfigStaticUrlReview(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STATIC_URL_REVIEW, value);
    }

    public String getKeyRemoteConfigStaticUrlReview()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STATIC_URL_REVIEW, Crypto.getUrlDecoderEx(Constants.URL_WEB_REVIEW_TERMS));
    }

    public void setKeyRemoteConfigStaticUrlLifeStyleProject(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STATIC_URL_LIFESTYLEPROJECT, value);
    }

    public String getKeyRemoteConfigStaticUrlLifeStyleProject()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STATIC_URL_LIFESTYLEPROJECT, Crypto.getUrlDecoderEx(Constants.URL_WEB_LIFESTYLE));
    }

    public void setKeyRemoteConfigStaticUrlDailyStampHome(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STATIC_URL_DAILYSTAMPHOME, value);
    }

    public String getKeyRemoteConfigStaticUrlDailyStampHome()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STATIC_URL_DAILYSTAMPHOME, Crypto.getUrlDecoderEx(Constants.URL_WEB_STAMP_EVENT));
    }

    public void setKeyRemoteConfigStaticUrlCollectPersonalInformation(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STATIC_URL_COLLECTPERSONAL, value);
    }

    public String getKeyRemoteConfigStaticUrlCollectPersonalInformation()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STATIC_URL_COLLECTPERSONAL, Crypto.getUrlDecoderEx(Constants.URL_WEB_COLLECT_PERSONAL));
    }

    public void setKeyRemoteConfigStayRankTestName(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STAY_RANK_TEST_NAME, value);
    }

    public String getKeyRemoteConfigStayRankTestName()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STAY_RANK_TEST_NAME, null);
    }

    public void setKeyRemoteConfigStayRankTestType(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STAY_RANK_TEST_TYPE, value);
    }

    public String getKeyRemoteConfigStayRankTestType()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STAY_RANK_TEST_TYPE, null);
    }
}
