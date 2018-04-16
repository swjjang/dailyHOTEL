package com.daily.dailyhotel.storage.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.crashlytics.android.Crashlytics;
import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;

/**
 */
public class DailyRemoteConfigPreference
{
    private static final String PREFERENCE_REMOTE_CONFIG = "DH_RemoteConfig";

    /////////////////////////////////////////////////////////////////////////////////////////
    // Remote Config Preference
    /////////////////////////////////////////////////////////////////////////////////////////

    // remote config text
    private static final String KEY_REMOTE_CONFIG_INTRO_VERSION = "1";
    private static final String KEY_REMOTE_CONFIG_INTRO_NEW_VERSION = "2";
    private static final String KEY_REMOTE_CONFIG_INTRO_NEW_URL = "3";

//    private static final String KEY_REMOTE_CONFIG_TEXT_VERSION = "100";
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
    //    private static final String KEY_REMOTE_CONFIG_STAMP_ENABLED = "318";
    //    private static final String KEY_REMOTE_CONFIG_STAMP_STAY_DETAIL_MESSAGE1 = "319";
    //    private static final String KEY_REMOTE_CONFIG_STAMP_STAY_DETAIL_MESSAGE2 = "320";
    //    private static final String KEY_REMOTE_CONFIG_STAMP_STAY_DETAIL_MESSAGE3 = "321";
    //    private static final String KEY_REMOTE_CONFIG_STAMP_STAY_DETAIL_MESSAGE3_ENABLED = "322";
    //    private static final String KEY_REMOTE_CONFIG_STAMP_STAY_DETAIL_POPUP_TITLE = "323";
    //    private static final String KEY_REMOTE_CONFIG_STAMP_STAY_DETAIL_POPUP_MESSAGE = "324";
    //    private static final String KEY_REMOTE_CONFIG_STAMP_STAY_THANKYOU_MESSAGE1 = "325";
    //    private static final String KEY_REMOTE_CONFIG_STAMP_STAY_THANKYOU_MESSAGE2 = "326";
    //    private static final String KEY_REMOTE_CONFIG_STAMP_STAY_THANKYOU_MESSAGE3 = "327";
    //    private static final String KEY_REMOTE_CONFIG_STAMP_STAMP_DATE1 = "328";
    //    private static final String KEY_REMOTE_CONFIG_STAMP_STAMP_DATE2 = "329";
    //    private static final String KEY_REMOTE_CONFIG_STAMP_STAMP_DATE3 = "330";
    //    private static final String KEY_REMOTE_CONFIG_STAMP_END_EVENT_POPUP_ENABLED = "331";
    //    private static final String KEY_REMOTE_CONFIG_STAMP_STAMP_HOME_MESSAGE1 = "333";
    //    private static final String KEY_REMOTE_CONFIG_STAMP_STAMP_HOME_MESSAGE2 = "334";
    //    private static final String KEY_REMOTE_CONFIG_STAMP_STAMP_HOME_ENABLED = "335";

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
    //    private static final String KEY_REMOTE_CONFIG_STATIC_URL_STAMP = "381";
    private static final String KEY_REMOTE_CONFIG_STATIC_URL_REVIEW = "382";
    private static final String KEY_REMOTE_CONFIG_STATIC_URL_LIFESTYLEPROJECT = "383";
    //    private static final String KEY_REMOTE_CONFIG_STATIC_URL_DAILYSTAMPHOME = "384";
    private static final String KEY_REMOTE_CONFIG_STATIC_URL_COLLECTPERSONAL = "385";
    private static final String KEY_REMOTE_CONFIG_STATIC_URL_DAILYREWARD = "386";
    private static final String KEY_REMOTE_CONFIG_STATIC_URL_DAILYREWARD_TERMS = "387";
    private static final String KEY_REMOTE_CONFIG_STATIC_URL_DAILYREWARD_COUPON_TERMS = "388";
    private static final String KEY_REMOTE_CONFIG_STATIC_URL_DAILY_TRUE_AWARDS = "389";

    private static final String KEY_REMOTE_CONFIG_OB_SEARCH_KEYWORD = "390";

    // Daily Reward Sticker
    //    private static final String KEY_REMOTE_CONFIG_REWARD_ENABLED = "400";
    private static final String KEY_REMOTE_CONFIG_REWARD_STICKER_ENABLED = "401";
    private static final String KEY_REMOTE_CONFIG_REWARD_STICKER_CARD_TITLE_MESSAGE = "402";
    private static final String KEY_REMOTE_CONFIG_REWARD_STICKER_CAMPAIGN_ENALBED = "403";
    private static final String KEY_REMOTE_CONFIG_REWARD_STICKER_NONMEMBER_DEFAULT_MESSAGE = "404";
    private static final String KEY_REMOTE_CONFIG_REWARD_STICKER_NONMEMBER_CAMPAIGN_MESSAGE = "405";
    private static final String KEY_REMOTE_CONFIG_REWARD_STICKER_NONMEMBER_CAMPAIGN_FREE_NIGHTS = "406";
    private static final String KEY_REMOTE_CONFIG_REWARD_STICKER_MEMBER_MESSAGE_0_NIGHTS = "410";
    private static final String KEY_REMOTE_CONFIG_REWARD_STICKER_MEMBER_MESSAGE_1_NIGHTS = "411";
    private static final String KEY_REMOTE_CONFIG_REWARD_STICKER_MEMBER_MESSAGE_2_NIGHTS = "412";
    private static final String KEY_REMOTE_CONFIG_REWARD_STICKER_MEMBER_MESSAGE_3_NIGHTS = "413";
    private static final String KEY_REMOTE_CONFIG_REWARD_STICKER_MEMBER_MESSAGE_4_NIGHTS = "414";
    private static final String KEY_REMOTE_CONFIG_REWARD_STICKER_MEMBER_MESSAGE_5_NIGHTS = "415";
    private static final String KEY_REMOTE_CONFIG_REWARD_STICKER_MEMBER_MESSAGE_6_NIGHTS = "416";
    private static final String KEY_REMOTE_CONFIG_REWARD_STICKER_MEMBER_MESSAGE_7_NIGHTS = "417";
    private static final String KEY_REMOTE_CONFIG_REWARD_STICKER_MEMBER_MESSAGE_8_NIGHTS = "418";
    private static final String KEY_REMOTE_CONFIG_REWARD_STICKER_MEMBER_MESSAGE_9_NIGHTS = "419";
    //    private static final String KEY_REMOTE_CONFIG_REWARD_STICKER_GUIDE_TITLE_MESSAGE = "420";
    //    private static final String KEY_REMOTE_CONFIG_REWARD_STICKER_GUIDE_DESCRIPTION_MESSAGE = "421";
    private static final String KEY_REMOTE_CONFIG_REWARD_STICKER_REWARD_TITLE_MESSAGE = "422";
    private static final String KEY_REMOTE_CONFIG_REWARD_STICKER_GUIDES = "423";

    // Gourmet Keywrod
    private static final String KEY_REMOTE_CONFIG_GOURMET_SEARCH_KEYWORD = "430";

    // Payment Card Event
    private static final String KEY_REMOTE_CONFIG_PAYMENT_CARD_EVENT = "431";

    // Search
    private static final String KEY_REMOTE_CONFIG_SEARCH_STAY_SUGGEST_HINT = "440";
    private static final String KEY_REMOTE_CONFIG_SEARCH_STAYOUTBOUND_SUGGEST_HINT = "441";
    private static final String KEY_REMOTE_CONFIG_SEARCH_GOURMET_SUGGEST_HINT = "442";

    // 상세 트루리뷰 상품 정보
    private static final String KEY_REMOTE_CONFIG_STAY_DETAIL_TRUEREVIEW_PRODUCT_VISIBLE = "450";
    private static final String KEY_REMOTE_CONFIG_STAY_OUTBOUND_DETAIL_TRUEREVIEW_PRODUCT_VISIBLE = "451";
    private static final String KEY_REMOTE_CONFIG_GOURMET_DETAIL_TRUEREVIEW_PRODUCT_VISIBLE = "452";

    // A/B Test
    private static final String KEY_REMOTE_CONFIG_STAY_RANK_TEST_NAME = "1000";
    private static final String KEY_REMOTE_CONFIG_STAY_RANK_TEST_TYPE = "1001";

    // App Research
    private static final String KEY_REMOTE_CONFIG_APP_RESEARCH = "1002";

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

    public void setRemoteConfigUpdateOptional(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_UPDATE_OPTIONAL, value);
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

    public void setKeyRemoteConfigStaticUrlCollectPersonalInformation(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STATIC_URL_COLLECTPERSONAL, value);
    }

    public String getKeyRemoteConfigStaticUrlCollectPersonalInformation()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STATIC_URL_COLLECTPERSONAL, Crypto.getUrlDecoderEx(Constants.URL_WEB_COLLECT_PERSONAL));
    }

    public void setKeyRemoteConfigStaticUrlDailyReward(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STATIC_URL_DAILYREWARD, value);
    }

    public String getKeyRemoteConfigStaticUrlDailyReward()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STATIC_URL_DAILYREWARD, Crypto.getUrlDecoderEx(Constants.URL_WEB_DAILY_REWARD));
    }

    public void setKeyRemoteConfigStaticUrlDailyRewardTerms(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STATIC_URL_DAILYREWARD_TERMS, value);
    }

    public String getKeyRemoteConfigStaticUrlDailyRewardTerms()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STATIC_URL_DAILYREWARD_TERMS, Crypto.getUrlDecoderEx(Constants.URL_WEB_DAILY_REWARD_TERMS));
    }

    public void setKeyRemoteConfigStaticUrlDailyRewardCouponTerms(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STATIC_URL_DAILYREWARD_COUPON_TERMS, value);
    }

    public String getKeyRemoteConfigStaticUrlDailyRewardCouponTerms()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STATIC_URL_DAILYREWARD_COUPON_TERMS, Crypto.getUrlDecoderEx(Constants.URL_WEB_DAILY_REWARD_COUPON_TERMS));
    }

    public void setKeyRemoteConfigStaticUrlDailyTrueAwards(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STATIC_URL_DAILY_TRUE_AWARDS, value);
    }

    public String getKeyRemoteConfigStaticUrlDailyTrueAwards()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STATIC_URL_DAILY_TRUE_AWARDS, Crypto.getUrlDecoderEx(Constants.URL_WEB_DAILY_AWARDS));
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

    public void setKeyRemoteConfigObSearchKeyword(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_OB_SEARCH_KEYWORD, value);
    }

    public String getKeyRemoteConfigObSearchKeyword()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_OB_SEARCH_KEYWORD, null);
    }

    //    public void setKeyRemoteConfigRewardEnabled(boolean value)
    //    {
    //        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_REWARD_ENABLED, value);
    //    }
    //
    //    public boolean isKeyRemoteConfigRewardEnabled()
    //    {
    //        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_REWARD_ENABLED, false);
    //    }

    public void setKeyRemoteConfigRewardStickerEnabled(boolean value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_REWARD_STICKER_ENABLED, value);
    }

    public boolean isKeyRemoteConfigRewardStickerEnabled()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_REWARD_STICKER_ENABLED, false);
    }

    public void setKeyRemoteConfigRewardStickerCardTitleMessage(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_REWARD_STICKER_CARD_TITLE_MESSAGE, value);
    }

    public String getKeyRemoteConfigRewardStickerCardTitleMessage()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_REWARD_STICKER_CARD_TITLE_MESSAGE, null);
    }

    public void setKeyRemoteConfigRewardStickerRewardTitleMessage(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_REWARD_STICKER_REWARD_TITLE_MESSAGE, value);
    }

    public String getKeyRemoteConfigRewardStickerRewardTitleMessage()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_REWARD_STICKER_REWARD_TITLE_MESSAGE, null);
    }

    public void setKeyRemoteConfigRewardStickerCampaignEnabled(boolean value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_REWARD_STICKER_CAMPAIGN_ENALBED, value);
    }

    public boolean isKeyRemoteConfigRewardStickerCampaignEnabled()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_REWARD_STICKER_CAMPAIGN_ENALBED, false);
    }

    public void setKeyRemoteConfigRewardStickerNonMemberDefaultMessage(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_REWARD_STICKER_NONMEMBER_DEFAULT_MESSAGE, value);
    }

    public String getKeyRemoteConfigRewardStickerNonMemberDefaultMessage()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_REWARD_STICKER_NONMEMBER_DEFAULT_MESSAGE, null);
    }

    public void setKeyRemoteConfigRewardStickerNonMemberCampaignMessage(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_REWARD_STICKER_NONMEMBER_CAMPAIGN_MESSAGE, value);
    }

    public String getKeyRemoteConfigRewardStickerNonMemberCampaignMessage()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_REWARD_STICKER_NONMEMBER_CAMPAIGN_MESSAGE, null);
    }

    public void setKeyRemoteConfigRewardStickerNonMemberCampaignFreeNights(int value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_REWARD_STICKER_NONMEMBER_CAMPAIGN_FREE_NIGHTS, value);
    }

    public int getKeyRemoteConfigRewardStickerNonMemberCampaignFreeNights()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_REWARD_STICKER_NONMEMBER_CAMPAIGN_FREE_NIGHTS, 0);
    }

    public void setKeyRemoteConfigRewardStickerMemberMessage(int nights, String value)
    {
        switch (nights)
        {
            case 0:
                setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_REWARD_STICKER_MEMBER_MESSAGE_0_NIGHTS, value);
                break;
            case 1:
                setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_REWARD_STICKER_MEMBER_MESSAGE_1_NIGHTS, value);
                break;
            case 2:
                setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_REWARD_STICKER_MEMBER_MESSAGE_2_NIGHTS, value);
                break;
            case 3:
                setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_REWARD_STICKER_MEMBER_MESSAGE_3_NIGHTS, value);
                break;
            case 4:
                setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_REWARD_STICKER_MEMBER_MESSAGE_4_NIGHTS, value);
                break;
            case 5:
                setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_REWARD_STICKER_MEMBER_MESSAGE_5_NIGHTS, value);
                break;
            case 6:
                setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_REWARD_STICKER_MEMBER_MESSAGE_6_NIGHTS, value);
                break;
            case 7:
                setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_REWARD_STICKER_MEMBER_MESSAGE_7_NIGHTS, value);
                break;
            case 8:
                setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_REWARD_STICKER_MEMBER_MESSAGE_8_NIGHTS, value);
                break;
            case 9:
                setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_REWARD_STICKER_MEMBER_MESSAGE_9_NIGHTS, value);
                break;
        }
    }

    public String getKeyRemoteConfigRewardStickerMemberMessage(int nights)
    {
        switch (nights)
        {
            case 0:
                return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_REWARD_STICKER_MEMBER_MESSAGE_0_NIGHTS, null);
            case 1:
                return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_REWARD_STICKER_MEMBER_MESSAGE_1_NIGHTS, null);
            case 2:
                return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_REWARD_STICKER_MEMBER_MESSAGE_2_NIGHTS, null);
            case 3:
                return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_REWARD_STICKER_MEMBER_MESSAGE_3_NIGHTS, null);
            case 4:
                return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_REWARD_STICKER_MEMBER_MESSAGE_4_NIGHTS, null);
            case 5:
                return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_REWARD_STICKER_MEMBER_MESSAGE_5_NIGHTS, null);
            case 6:
                return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_REWARD_STICKER_MEMBER_MESSAGE_6_NIGHTS, null);
            case 7:
                return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_REWARD_STICKER_MEMBER_MESSAGE_7_NIGHTS, null);
            case 8:
                return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_REWARD_STICKER_MEMBER_MESSAGE_8_NIGHTS, null);
            case 9:
                return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_REWARD_STICKER_MEMBER_MESSAGE_9_NIGHTS, null);
            default:
                return null;
        }
    }

    //    public void setKeyRemoteConfigRewardStickerGuideTitleMessage(String value)
    //    {
    //        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_REWARD_STICKER_GUIDE_TITLE_MESSAGE, value);
    //    }
    //
    //    public String getKeyRemoteConfigRewardStickerGuideTitleMessage()
    //    {
    //        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_REWARD_STICKER_GUIDE_TITLE_MESSAGE, null);
    //    }

    public void setKeyRemoteConfigRewardStickerGuides(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_REWARD_STICKER_GUIDES, value);
    }

    public String getKeyRemoteConfigRewardStickerGuides()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_REWARD_STICKER_GUIDES, null);
    }

    //    public void setKeyRemoteConfigRewardStickerGuideDescriptionMessage(String value)
    //    {
    //        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_REWARD_STICKER_GUIDE_DESCRIPTION_MESSAGE, value);
    //    }
    //
    //    public String getKeyRemoteConfigRewardStickerGuideDescriptionMessage()
    //    {
    //        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_REWARD_STICKER_GUIDE_DESCRIPTION_MESSAGE, null);
    //    }

    public void setKeyRemoteConfigAppResearch(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_APP_RESEARCH, value);
    }

    public String getKeyRemoteConfigAppResearch()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_APP_RESEARCH, null);
    }

    public void setKeyRemoteConfigGourmetSearchKeyword(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_GOURMET_SEARCH_KEYWORD, value);
    }

    public String getKeyRemoteConfigGourmetSearchKeyword()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_GOURMET_SEARCH_KEYWORD, null);
    }

    public void setKeyRemoteConfigPaymentCardEvent(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_PAYMENT_CARD_EVENT, value);
    }

    public String getKeyRemoteConfigPaymentCardEvent()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_PAYMENT_CARD_EVENT, null);
    }

    public void setKeyRemoteConfigSearchStaySuggestHint(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_SEARCH_STAY_SUGGEST_HINT, value);
    }

    public String getKeyRemoteConfigSearchStaySuggestHint()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_SEARCH_STAY_SUGGEST_HINT, null);
    }

    public void setKeyRemoteConfigSearchStayOutboundSuggestHint(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_SEARCH_STAYOUTBOUND_SUGGEST_HINT, value);
    }

    public String getKeyRemoteConfigSearchStayOutboundSuggestHint()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_SEARCH_STAYOUTBOUND_SUGGEST_HINT, null);
    }

    public void setKeyRemoteConfigSearchGourmetSuggestHint(String value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_SEARCH_GOURMET_SUGGEST_HINT, value);
    }

    public String getKeyRemoteConfigSearchGourmetSuggestHint()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_SEARCH_GOURMET_SUGGEST_HINT, null);
    }


    // True Review
    public void setKeyRemoteConfigStayDetailTrueReviewProductVisible(boolean value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STAY_DETAIL_TRUEREVIEW_PRODUCT_VISIBLE, value);
    }

    public boolean isKeyRemoteConfigStayDetailTrueReviewProductVisible()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STAY_DETAIL_TRUEREVIEW_PRODUCT_VISIBLE, true);
    }

    public void setKeyRemoteConfigStayOutboundDetailTrueReviewProductVisible(boolean value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_STAY_OUTBOUND_DETAIL_TRUEREVIEW_PRODUCT_VISIBLE, value);
    }

    public boolean isKeyRemoteConfigStayOutboundDetailTrueReviewProductVisible()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_STAY_OUTBOUND_DETAIL_TRUEREVIEW_PRODUCT_VISIBLE, true);
    }

    public void setKeyRemoteConfigGourmetDetailTrueReviewProductVisible(boolean value)
    {
        setValue(mRemoteConfigEditor, KEY_REMOTE_CONFIG_GOURMET_DETAIL_TRUEREVIEW_PRODUCT_VISIBLE, value);
    }

    public boolean isKeyRemoteConfigGourmetDetailTrueReviewProductVisible()
    {
        return getValue(mRemoteConfigPreferences, KEY_REMOTE_CONFIG_GOURMET_DETAIL_TRUEREVIEW_PRODUCT_VISIBLE, true);
    }
}
