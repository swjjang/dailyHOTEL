package com.twoheart.dailyhotel.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.crashlytics.android.Crashlytics;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.request.DailyHotelRequest;

/**
 */
public class DailyPreference
{
    public static final String DAILYHOTEL_SHARED_PREFERENCE = "GOOD_NIGHT"; // 기존에 존재하던
    public static final String DAILYHOTEL_SHARED_PREFERENCE_V1 = "dailyHOTEL_v1"; // 새로 만든

    public static final String PREFERENCE_REMOTE_CONFIG = "DH_RemoteConfig";

    /////////////////////////////////////////////////////////////////////////////////////////
    // "dailyHOTEL_v1" Preference
    /////////////////////////////////////////////////////////////////////////////////////////

    private static final String KEY_OPENING_ALARM = "1"; // 알람
    private static final String KEY_LAST_MENU = "3"; // 마지막 메뉴 리스트가 무엇인지
    private static final String KEY_SHOW_GUIDE = "4"; // 가이드를 봤는지 여부
    //    private static final String KEY_ALLOW_PUSH = "5";
    //    private static final String KEY_ALLOW_BENEFIT_ALARM = "6";

    private static final String KEY_COLLAPSEKEY = "10"; // 푸시 중복 되지 않도록
    //    private static final String KEY_SOCIAL_SIGNUP = "11"; // 회원가입시 소셜 가입자인 경우

    private static final String KEY_HOTEL_REGION_ISOVERSEA = "12"; // 현재 선택된 지역이 국내/해외
    private static final String KEY_GOURMET_REGION_ISOVERSEA = "13"; // 현재 선택된 지역이 국내/해외

    private static final String KEY_NEW_EVENT = "14"; // 현재 이벤트 유무
    private static final String KEY_NEW_COUPON = "15"; // 현재 새로운 쿠폰 유무(로그인 사용자만 보임)
    private static final String KEY_NEW_NOTICE = "16"; // 현재 새로운 쿠폰 유무(로그인 사용자만 보임)

    private static final String KEY_NOTIFICATION_UID = "20"; // 노티피케이션 UID

    private static final String KEY_AGREE_TERMS_OF_LOCATION = "21"; // 위치 약관 동의 여부
    private static final String KEY_INFORMATION_CS_OPERATION_TIMEMESSAGE = "22"; // 운영시간 문구
    private static final String KEY_APP_VERSION = "23";

    private static final String KEY_SHOW_BENEFIT_ALARM = "24";
    private static final String KEY_BENEFIT_ALARM_MESSAGE = "25";
    private static final String KEY_FIRST_BUYER = "26";
    private static final String KEY_FIRST_APP_VERSION = "27";

    private static final String KEY_STAY_LAST_VIEW_DATE = "108";
    private static final String KEY_GOURMET_LAST_VIEW_DATE = "109";

    private static final String KEY_HOTEL_SEARCH_RECENTLY = "200";
    private static final String KEY_GOURMET_SEARCH_RECENTLY = "201";

    private static final String KEY_NOTICE_NEW_LIST = "202";
    private static final String KEY_NOTICE_NEW_REMOVE_LIST = "203";

    private static final String KEY_SELECTED_SIMPLE_CARD = "204"; // 마지막으로 간편결제된 카드

    private static final String KEY_AUTHORIZATION = "1000";
    private static final String KEY_VERIFICATION = "1001";

    private static final String KEY_SETTING_MIGRATION_FLAG = "1003";
    private static final String KEY_STAY_CATEGORY_CODE = "1010";
    private static final String KEY_STAY_CATEGORY_NAME = "1011";

    private static final String KEY_BACKGROUND_APP_TIME = "2000";

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

    /////////////////////////////////////////////////////////////////////////////////////////
    // "GOOD_NIGHT" Preference - 1.9.4 이상의 버전에서 강업 2회 이후 삭제 예정
    /////////////////////////////////////////////////////////////////////////////////////////

    // Setting
    private static final String KEY_OLD_SETTING_GCM_ID = "PUSH_ID";

    //Setting - Region
    private static final String KEY_OLD_SETTING_REGION_STAY_SELECT = "REGION_SELECT";
    private static final String KEY_OLD_SETTING_REGION_STAY_SETTING = "REGION_SETTING";
    private static final String KEY_OLD_SETTING_REGION_FNB_SETTING = "FNB_REGION_SETTING";
    private static final String KEY_OLD_SETTING_REGION_FNB_SELECT = "FNB_REGION_SELECT";

    // Setting - Version
    private static final String KEY_OLD_SETTING_VERSION_SKIP_MAX_VERSION = "SKIP_MAX_VERSION";

    // User - Information
    private static final String KEY_OLD_USER_EMAIL = "USER_EMAIL";
    private static final String KEY_OLD_USER_TYPE = "USER_TYPE";
    private static final String KEY_OLD_USER_NAME = "USER_NAME";
    private static final String KEY_OLD_USER_RECOMMENDER = "USER_RECOMMENDER";
    private static final String KEY_OLD_USER_BENEFIT_ALARM = "USER_BENEFIT_ALARM";
    private static final String KEY_OLD_USER_IS_EXCEED_BONUS = "USER_IS_EXCEED_BONUS";

    // payment
    private static final String KEY_OLD_PAYMENT_OVERSEAS_NAME = "OVERSEAS_NAME";
    private static final String KEY_OLD_PAYMENT_OVERSEAS_PHONE = "OVERSEAS_PHONE";
    private static final String KEY_OLD_PAYMENT_OVERSEAS_EMAIL = "OVERSEAS_EMAIL";

    // payment - Virtual Account
    private static final String KEY_OLD_PAYMENT_ACCOUNT_READY_FLAG = "ACCOUNT_READY_FLAG";

    // Event
    private static final String KEY_OLD_EVENT_LASTEST_EVENT_TIME = "LATEST_EVENT_TIME";
    private static final String KEY_OLD_EVENT_LASTEST_COUPON_TIME = "LATEST_COUPON_TIME";
    private static final String KEY_OLD_EVENT_VIEWED_EVENT_TIME = "VIEWED_EVENT_TIME";
    private static final String KEY_OLD_EVENT_VIEWED_COUPON_TIME = "VIEWED_COUPON_TIME";

    /////////////////////////////////////////////////////////////////////////////////////////
    // New Key old --> v1
    /////////////////////////////////////////////////////////////////////////////////////////

    // Setting
    private static final String KEY_SETTING_GCM_ID = "1002";
    private static final String KEY_SETTING_VERSION_SKIP_MAX_VERSION = "1004";

    // Setting - Region
    private static final String KEY_SETTING_REGION_STAY_SELECT = "1110";
    private static final String KEY_SETTING_REGION_STAY_SETTING = "1111";
    private static final String KEY_SETTING_REGION_PROVINCE_STAY_SELECT = "1112";
    private static final String KEY_SETTING_REGION_FNB_SELECT = "1120";
    private static final String KEY_SETTING_REGION_FNB_SETTING = "1121";
    private static final String KEY_SETTING_REGION_PROVINCE_FNB_SELECT = "1122";

    // User
    private static final String KEY_USER_EMAIL = "2001";
    private static final String KEY_USER_TYPE = "2002";
    private static final String KEY_USER_NAME = "2003";
    private static final String KEY_USER_RECOMMENDER = "2004";
    private static final String KEY_USER_BENEFIT_ALARM = "2005";
    private static final String KEY_USER_IS_EXCEED_BONUS = "2006";

    // Payment
    private static final String KEY_PAYMENT_OVERSEAS_NAME = "4000";
    private static final String KEY_PAYMENT_OVERSEAS_PHONE = "4001";
    private static final String KEY_PAYMENT_OVERSEAS_EMAIL = "4002";

    // payment - Virtual Account
    private static final String KEY_PAYMENT_ACCOUNT_READY_FLAG = "4100";

    // Event
    private static final String KEY_EVENT_LASTEST_EVENT_TIME = "6100";
    private static final String KEY_EVENT_LASTEST_COUPON_TIME = "6101";
    private static final String KEY_EVENT_LASTEST_NOTICE_TIME = "6102";
    private static final String KEY_EVENT_VIEWED_EVENT_TIME = "6200";
    private static final String KEY_EVENT_VIEWED_COUPON_TIME = "6201";
    private static final String KEY_EVENT_VIEWED_NOTICE_TIME = "6202";
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    private static DailyPreference mInstance;
    private SharedPreferences mPreferences;
    private SharedPreferences mRemoteConfigPreferences;
    private SharedPreferences mOldPreferences;
    private Editor mEditor;
    private Editor mRemoteConfigEditor;
    private Editor mOldEditor;

    private DailyPreference(Context context)
    {
        mPreferences = context.getSharedPreferences(DAILYHOTEL_SHARED_PREFERENCE_V1, Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();

        mRemoteConfigPreferences = context.getSharedPreferences(PREFERENCE_REMOTE_CONFIG, Context.MODE_PRIVATE);
        mRemoteConfigEditor = mRemoteConfigPreferences.edit();

        boolean isMigrationFlag = isMigrationFlag();

        boolean isFirst = false;

        String firstAppVersion = getFirstAppVersion();
        String currentAppVersion = Util.getAppVersion(context);

        if (Util.isTextEmpty(firstAppVersion) || firstAppVersion.equalsIgnoreCase(currentAppVersion))
        {
            isFirst = true;
        }

        if (isFirst == false && isMigrationFlag == false)
        {
            mOldPreferences = context.getSharedPreferences(DAILYHOTEL_SHARED_PREFERENCE, Context.MODE_PRIVATE);
            mOldEditor = mOldPreferences.edit();
        }

    }

    public static synchronized DailyPreference getInstance(Context context)
    {
        if (mInstance == null)
        {
            mInstance = new DailyPreference(context);
        }

        return mInstance;
    }

    /**
     * 앱 삭제시에도 해당 데이터는 남기도록 한다.
     */
    public void clear()
    {
        // 해택 알림 내용은 유지 하도록 한다. 단 로그인시에는 서버에서 다시 가져와서 세팅한다.
        boolean isUserBenefitAlarm = isUserBenefitAlarm();
        boolean isShowBenefitAlarm = isShowBenefitAlarm();

        if (mEditor != null)
        {
            mEditor.clear();
            mEditor.apply();
        }

        if (mOldEditor != null)
        {
            mOldEditor.clear();
            mOldEditor.apply();
        }

        setUserBenefitAlarm(isUserBenefitAlarm);
        setShowBenefitAlarm(isShowBenefitAlarm);

        DailyHotel.AUTHORIZATION = null;
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
            try
            {
                Object object = sharedPreferences.getAll().get(key);
                String msg = "key : " + key + ", value : " + sharedPreferences.getAll().get(key) + ", Type : " + object.toString();

                Crashlytics.log(msg);
                Crashlytics.logException(e);
            } catch (Exception e1)
            {

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
            if (Util.isTextEmpty(value) == true)
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
            try
            {
                Object object = sharedPreferences.getAll().get(key);
                String msg = "key : " + key + ", value : " + sharedPreferences.getAll().get(key) + ", Type : " + object.toString();

                Crashlytics.log(msg);
                Crashlytics.logException(e);
            } catch (Exception e1)
            {

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
            try
            {
                Object object = sharedPreferences.getAll().get(key);
                String msg = "key : " + key + ", value : " + sharedPreferences.getAll().get(key) + ", Type : " + object.toString();

                Crashlytics.log(msg);
                Crashlytics.logException(e);
            } catch (Exception e1)
            {

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
    // "dailyHOTEL_v1" Preference
    /////////////////////////////////////////////////////////////////////////////////////////

    public boolean getEnabledOpeningAlarm()
    {
        return getValue(mPreferences, KEY_OPENING_ALARM, false);
    }

    public void setEnabledOpeningAlarm(boolean value)
    {
        setValue(mEditor, KEY_OPENING_ALARM, value);
    }

    public boolean hasNewEvent()
    {
        return getValue(mPreferences, KEY_NEW_EVENT, false);
    }

    public void setNewEvent(boolean value)
    {
        setValue(mEditor, KEY_NEW_EVENT, value);
    }

    public boolean hasNewCoupon()
    {
        return getValue(mPreferences, KEY_NEW_COUPON, false);
    }

    public void setNewCoupon(boolean value)
    {
        setValue(mEditor, KEY_NEW_COUPON, value);
    }

    public boolean hasNewNotice()
    {
        return getValue(mPreferences, KEY_NEW_NOTICE, false);
    }

    public void setNewNotice(boolean value)
    {
        setValue(mEditor, KEY_NEW_NOTICE, value);
    }

    public int getNotificationUid()
    {
        return getValue(mPreferences, KEY_NOTIFICATION_UID, -1);
    }

    public void setNotificationUid(int value)
    {
        setValue(mEditor, KEY_NOTIFICATION_UID, value);
    }

    public String getLastMenu()
    {
        return getValue(mPreferences, KEY_LAST_MENU, null);
    }

    public void setLastMenu(String value)
    {
        setValue(mEditor, KEY_LAST_MENU, value);
    }

    public boolean isShowGuide()
    {
        return getValue(mPreferences, KEY_SHOW_GUIDE, false);
    }

    public void setShowGuide(boolean value)
    {
        setValue(mEditor, KEY_SHOW_GUIDE, value);
    }

    public String getCollapsekey()
    {
        return getValue(mPreferences, KEY_COLLAPSEKEY, null);
    }

    public void setCollapsekey(String value)
    {
        setValue(mEditor, KEY_COLLAPSEKEY, value);
    }

    public String getStayLastViewDate()
    {
        return getValue(mPreferences, KEY_STAY_LAST_VIEW_DATE, null);
    }

    public void setStayLastViewDate(String value)
    {
        setValue(mEditor, KEY_STAY_LAST_VIEW_DATE, value);
    }

    public String getGourmetLastViewDate()
    {
        return getValue(mPreferences, KEY_GOURMET_LAST_VIEW_DATE, null);
    }

    public void setGourmetLastViewDate(String value)
    {
        setValue(mEditor, KEY_GOURMET_LAST_VIEW_DATE, value);
    }

    public boolean isSelectedOverseaRegion(Constants.PlaceType placeType)
    {
        switch (placeType)
        {
            case FNB:
                return getValue(mPreferences, KEY_GOURMET_REGION_ISOVERSEA, false);

            case HOTEL:
            default:
                return getValue(mPreferences, KEY_HOTEL_REGION_ISOVERSEA, false);
        }
    }

    public void setSelectedOverseaRegion(Constants.PlaceType placeType, boolean value)
    {
        switch (placeType)
        {
            case HOTEL:
                setValue(mEditor, KEY_HOTEL_REGION_ISOVERSEA, value);
                break;

            case FNB:
                setValue(mEditor, KEY_GOURMET_REGION_ISOVERSEA, value);
                break;
        }
    }

    public String getAuthorization()
    {
        try
        {
            return DailyHotelRequest.urlDecrypt(getValue(mPreferences, KEY_AUTHORIZATION, null));
        } catch (Exception e)
        {
            String authorization = DailyHotelRequest.oldUrlDecrypt(getValue(mPreferences, KEY_AUTHORIZATION, null));

            setValue(mEditor, KEY_AUTHORIZATION, authorization);

            return authorization;
        }
    }

    public void setAuthorization(String value)
    {
        DailyHotel.AUTHORIZATION = value;

        setValue(mEditor, KEY_AUTHORIZATION, DailyHotelRequest.urlEncrypt(value));
    }

    public boolean isVerification()
    {
        return getValue(mPreferences, KEY_VERIFICATION, false);
    }

    public void setVerification(boolean value)
    {
        setValue(mEditor, KEY_VERIFICATION, value);
    }

    public boolean isMigrationFlag()
    {
        return getValue(mPreferences, KEY_SETTING_MIGRATION_FLAG, false);
    }

    public void setMigrationFlag(boolean value)
    {
        setValue(mEditor, KEY_SETTING_MIGRATION_FLAG, value);
    }

    public void setHotelRecentSearches(String text)
    {
        setValue(mEditor, KEY_HOTEL_SEARCH_RECENTLY, text);
    }

    public String getHotelRecentSearches()
    {
        return getValue(mPreferences, KEY_HOTEL_SEARCH_RECENTLY, null);
    }

    public void setGourmetRecentSearches(String text)
    {
        setValue(mEditor, KEY_GOURMET_SEARCH_RECENTLY, text);
    }

    public String getGourmetRecentSearches()
    {
        return getValue(mPreferences, KEY_GOURMET_SEARCH_RECENTLY, null);
    }

    public void setTermsOfLocation(boolean value)
    {
        setValue(mEditor, KEY_AGREE_TERMS_OF_LOCATION, value);
    }

    public boolean isAgreeTermsOfLocation()
    {
        return getValue(mPreferences, KEY_AGREE_TERMS_OF_LOCATION, false);
    }

    public String getOperationTimeMessage(Context context)
    {
        return getValue(mPreferences, KEY_INFORMATION_CS_OPERATION_TIMEMESSAGE, context.getString(R.string.dialog_msg_call));
    }

    public void setOperationTimeMessage(String text)
    {
        setValue(mEditor, KEY_INFORMATION_CS_OPERATION_TIMEMESSAGE, text);
    }

    public void setAppVersion(String value)
    {
        setValue(mEditor, KEY_APP_VERSION, value);
    }

    public String getAppVersion()
    {
        return getValue(mPreferences, KEY_APP_VERSION, null);
    }

    public void setShowBenefitAlarm(boolean value)
    {
        setValue(mEditor, KEY_SHOW_BENEFIT_ALARM, value);
    }

    public boolean isShowBenefitAlarm()
    {
        return getValue(mPreferences, KEY_SHOW_BENEFIT_ALARM, false);
    }

    public void setBenefitAlarmMessage(String value)
    {
        setValue(mEditor, KEY_BENEFIT_ALARM_MESSAGE, value);
    }

    public String getBenefitAlarmMessage()
    {
        return getValue(mPreferences, KEY_BENEFIT_ALARM_MESSAGE, null);
    }

    public void setShowBenefitAlarmFirstBuyer(boolean value)
    {
        setValue(mEditor, KEY_FIRST_BUYER, value);
    }

    public boolean isShowBenefitAlarmFirstBuyer()
    {
        return getValue(mPreferences, KEY_FIRST_BUYER, false);
    }

    public void setFirstAppVersion(String value)
    {
        setValue(mEditor, KEY_FIRST_APP_VERSION, value);
    }

    public String getFirstAppVersion()
    {
        return getValue(mPreferences, KEY_FIRST_APP_VERSION, null);
    }

    public void setStayCategory(String name, String code)
    {
        setValue(mEditor, KEY_STAY_CATEGORY_NAME, name);
        setValue(mEditor, KEY_STAY_CATEGORY_CODE, code);
    }

    public String getStayCategoryCode()
    {
        return getValue(mPreferences, KEY_STAY_CATEGORY_CODE, null);
    }

    public String getStayCategoryName()
    {
        return getValue(mPreferences, KEY_STAY_CATEGORY_NAME, null);
    }

    public long getBackgroundAppTime()
    {
        return getValue(mPreferences, KEY_BACKGROUND_APP_TIME, 0L);
    }

    public void setBackgroundAppTime(long value)
    {
        setValue(mEditor, KEY_BACKGROUND_APP_TIME, value);
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

            if (Util.isTextEmpty(email) == false)
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

    /////////////////////////////////////////////////////////////////////////////////////////
    // new
    /////////////////////////////////////////////////////////////////////////////////////////

    public String getGCMRegistrationId()
    {
        return getValue(mPreferences, KEY_SETTING_GCM_ID, null);
    }

    public void setGCMRegistrationId(String value)
    {
        setValue(mEditor, KEY_SETTING_GCM_ID, value);
    }

    public String getLastestEventTime()
    {
        return getValue(mPreferences, KEY_EVENT_LASTEST_EVENT_TIME, null);
    }

    public void setLastestEventTime(String value)
    {
        setValue(mEditor, KEY_EVENT_LASTEST_EVENT_TIME, value);
    }

    public String getLastestCouponTime()
    {
        return getValue(mPreferences, KEY_EVENT_LASTEST_COUPON_TIME, null);
    }

    public void setLastestCouponTime(String value)
    {
        setValue(mEditor, KEY_EVENT_LASTEST_COUPON_TIME, value);
    }

    public String getLastestNoticeTime()
    {
        return getValue(mPreferences, KEY_EVENT_LASTEST_NOTICE_TIME, null);
    }

    public void setLastestNoticeTime(String value)
    {
        setValue(mEditor, KEY_EVENT_LASTEST_NOTICE_TIME, value);
    }

    public String getViewedEventTime()
    {
        return getValue(mPreferences, KEY_EVENT_VIEWED_EVENT_TIME, null);
    }

    public void setViewedEventTime(String value)
    {
        setValue(mEditor, KEY_EVENT_VIEWED_EVENT_TIME, value);
    }

    public String getViewedCouponTime()
    {
        return getValue(mPreferences, KEY_EVENT_VIEWED_COUPON_TIME, null);
    }

    public void setViewedCouponTime(String value)
    {
        setValue(mEditor, KEY_EVENT_VIEWED_COUPON_TIME, value);
    }

    public String getViewedNoticeTime()
    {
        return getValue(mPreferences, KEY_EVENT_VIEWED_NOTICE_TIME, null);
    }

    public void setViewedNoticeTime(String value)
    {
        setValue(mEditor, KEY_EVENT_VIEWED_NOTICE_TIME, value);
    }

    public String getNoticeNewList()
    {
        return getValue(mPreferences, KEY_NOTICE_NEW_LIST, null);
    }

    public void setNoticeNewList(String value)
    {
        setValue(mEditor, KEY_NOTICE_NEW_LIST, value);
    }

    public String getNoticeNewRemoveList()
    {
        return getValue(mPreferences, KEY_NOTICE_NEW_REMOVE_LIST, null);
    }

    public void setNoticeNewRemoveList(String value)
    {
        setValue(mEditor, KEY_NOTICE_NEW_REMOVE_LIST, value);
    }

    public String getSelectedSimpleCard()
    {
        return getValue(mPreferences, KEY_SELECTED_SIMPLE_CARD, null);
    }

    public void setSelectedSimpleCard(String value)
    {
        setValue(mEditor, KEY_SELECTED_SIMPLE_CARD, value);
    }

    public String getSelectedRegion(Constants.PlaceType placeType)
    {
        switch (placeType)
        {
            case FNB:
                return getValue(mPreferences, KEY_SETTING_REGION_FNB_SELECT, null);

            case HOTEL:
            default:
                return getValue(mPreferences, KEY_SETTING_REGION_STAY_SELECT, null);
        }
    }

    public void setSelectedRegion(Constants.PlaceType placeType, String value)
    {
        switch (placeType)
        {
            case HOTEL:
                setValue(mEditor, KEY_SETTING_REGION_STAY_SELECT, value);
                break;

            case FNB:
                setValue(mEditor, KEY_SETTING_REGION_FNB_SELECT, value);
                break;
        }
    }

    /**
     * 선택된 대지역 저장값 - Adjust 용
     *
     * @return
     */
    public String getSelectedRegionTypeProvince(Constants.PlaceType placeType)
    {
        switch (placeType)
        {
            case FNB:
                return getValue(mPreferences, KEY_SETTING_REGION_PROVINCE_FNB_SELECT, null);

            case HOTEL:
            default:
                return getValue(mPreferences, KEY_SETTING_REGION_PROVINCE_STAY_SELECT, null);
        }
    }

    public void setSelectedRegionTypeProvince(Constants.PlaceType placeType, String value)
    {
        switch (placeType)
        {
            case HOTEL:
                setValue(mEditor, KEY_SETTING_REGION_PROVINCE_STAY_SELECT, value);
                break;

            case FNB:
                setValue(mEditor, KEY_SETTING_REGION_PROVINCE_FNB_SELECT, value);
                break;
        }
    }

    public boolean isSettingRegion(Constants.PlaceType placeType)
    {
        switch (placeType)
        {
            case FNB:
                return getValue(mPreferences, KEY_SETTING_REGION_FNB_SETTING, false);

            case HOTEL:
            default:
                return getValue(mPreferences, KEY_SETTING_REGION_STAY_SETTING, false);
        }
    }

    public void setSettingRegion(Constants.PlaceType placeType, boolean value)
    {
        switch (placeType)
        {
            case HOTEL:
                setValue(mEditor, KEY_SETTING_REGION_STAY_SETTING, value);
                break;

            case FNB:
                setValue(mEditor, KEY_SETTING_REGION_FNB_SETTING, value);
                break;
        }
    }

    public String getOverseasName()
    {
        return getValue(mPreferences, KEY_PAYMENT_OVERSEAS_NAME, null);
    }

    public String getOverseasPhone()
    {
        return getValue(mPreferences, KEY_PAYMENT_OVERSEAS_PHONE, null);
    }

    public String getOverseasEmail()
    {
        return getValue(mPreferences, KEY_PAYMENT_OVERSEAS_EMAIL, null);
    }

    public void setOverseasUserInformation(String name, String phone, String email)
    {
        if (mEditor != null)
        {
            mEditor.putString(KEY_PAYMENT_OVERSEAS_NAME, name);
            mEditor.putString(KEY_PAYMENT_OVERSEAS_PHONE, phone);
            mEditor.putString(KEY_PAYMENT_OVERSEAS_EMAIL, email);
            mEditor.apply();
        }
    }

    public String getUserType()
    {
        return getValue(mPreferences, KEY_USER_TYPE, null);
    }

    public String getUserName()
    {
        return getValue(mPreferences, KEY_USER_NAME, null);
    }

    public String getUserEmail()
    {
        return getValue(mPreferences, KEY_USER_EMAIL, null);
    }

    public String getUserRecommender()
    {
        return getValue(mPreferences, KEY_USER_RECOMMENDER, null);
    }

    public boolean isUserBenefitAlarm()
    {
        return getValue(mPreferences, KEY_USER_BENEFIT_ALARM, false);
    }

    public void setUserBenefitAlarm(boolean value)
    {
        setValue(mEditor, KEY_USER_BENEFIT_ALARM, value);
    }

    public boolean isUserExceedBonus()
    {
        return getValue(mPreferences, KEY_USER_IS_EXCEED_BONUS, false);
    }

    public void setUserExceedBonus(boolean value)
    {
        setValue(mEditor, KEY_USER_IS_EXCEED_BONUS, value);
    }

    public void setUserInformation(String type, String email, String name, String recommender)
    {
        if (mEditor != null)
        {
            mEditor.putString(KEY_USER_TYPE, type);
            mEditor.putString(KEY_USER_EMAIL, email);
            mEditor.putString(KEY_USER_NAME, name);
            mEditor.putString(KEY_USER_RECOMMENDER, recommender);
            mEditor.apply();
        }
    }

    public void removeUserInformation()
    {
        if (mEditor != null)
        {
            mEditor.remove(KEY_USER_TYPE);
            mEditor.remove(KEY_USER_EMAIL);
            mEditor.remove(KEY_USER_NAME);
            mEditor.remove(KEY_AUTHORIZATION);

            DailyHotel.AUTHORIZATION = null;

            mEditor.apply();
        }
    }

    public String getSkipVersion()
    {
        return getValue(mPreferences, KEY_SETTING_VERSION_SKIP_MAX_VERSION, "1.0.0");
    }

    public void setSkipVersion(String value)
    {
        setValue(mEditor, KEY_SETTING_VERSION_SKIP_MAX_VERSION, value);
    }

    public int getVirtualAccountReadyFlag()
    {
        return getValue(mPreferences, KEY_PAYMENT_ACCOUNT_READY_FLAG, -1);
    }

    public void setVirtualAccountReadyFlag(int value)
    {
        setValue(mEditor, KEY_PAYMENT_ACCOUNT_READY_FLAG, value);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void clearOldPreference()
    {
        if (mOldEditor != null)
        {
            mOldEditor.clear();
            mOldEditor.apply();
        }
    }

    public void setPreferenceMigration()
    {
        boolean isMigrationFlag = isMigrationFlag();

        // 이주 필요 확인!
        if (isMigrationFlag == false && mOldPreferences != null)
        {
            try
            {
                if (mOldPreferences.contains(KEY_OLD_SETTING_GCM_ID) == true)
                {
                    setGCMRegistrationId(getValue(mOldPreferences, KEY_OLD_SETTING_GCM_ID, null));
                }

                if (mOldPreferences.contains(KEY_OLD_EVENT_LASTEST_EVENT_TIME) == true)
                {
                    setLastestEventTime(getValue(mOldPreferences, KEY_OLD_EVENT_LASTEST_EVENT_TIME, null));
                }

                if (mOldPreferences.contains(KEY_OLD_EVENT_LASTEST_COUPON_TIME) == true)
                {
                    setLastestCouponTime(getValue(mOldPreferences, KEY_OLD_EVENT_LASTEST_COUPON_TIME, null));
                }

                if (mOldPreferences.contains(KEY_OLD_EVENT_VIEWED_EVENT_TIME) == true)
                {
                    setViewedEventTime(getValue(mOldPreferences, KEY_OLD_EVENT_VIEWED_EVENT_TIME, null));
                }

                if (mOldPreferences.contains(KEY_OLD_SETTING_REGION_STAY_SELECT) == true)
                {
                    setSelectedRegion(Constants.PlaceType.HOTEL, getValue(mOldPreferences, KEY_OLD_SETTING_REGION_STAY_SELECT, null));
                }

                if (mOldPreferences.contains(KEY_OLD_SETTING_REGION_FNB_SELECT) == true)
                {
                    setSelectedRegion(Constants.PlaceType.FNB, getValue(mOldPreferences, KEY_OLD_SETTING_REGION_FNB_SELECT, null));
                }

                if (mOldPreferences.contains(KEY_OLD_SETTING_REGION_STAY_SETTING) == true)
                {
                    setSettingRegion(Constants.PlaceType.HOTEL, getValue(mOldPreferences, KEY_OLD_SETTING_REGION_STAY_SETTING, false));
                }

                if (mOldPreferences.contains(KEY_OLD_SETTING_REGION_FNB_SETTING) == true)
                {
                    setSettingRegion(Constants.PlaceType.FNB, getValue(mOldPreferences, KEY_OLD_SETTING_REGION_FNB_SETTING, false));
                }

                if (mOldPreferences.contains(KEY_OLD_EVENT_VIEWED_COUPON_TIME) == true)
                {
                    setViewedCouponTime(getValue(mOldPreferences, KEY_OLD_EVENT_VIEWED_COUPON_TIME, null));
                }

                if (mOldPreferences.contains(KEY_OLD_PAYMENT_OVERSEAS_NAME) == true)
                {
                    setOverseasUserInformation(getValue(mOldPreferences, KEY_OLD_PAYMENT_OVERSEAS_NAME, null), //
                        getValue(mOldPreferences, KEY_OLD_PAYMENT_OVERSEAS_PHONE, null), //
                        getValue(mOldPreferences, KEY_OLD_PAYMENT_OVERSEAS_EMAIL, null));
                }

                if (mOldPreferences.contains(KEY_OLD_USER_TYPE) == true)
                {
                    setUserInformation(getValue(mOldPreferences, KEY_OLD_USER_TYPE, null), //
                        getValue(mOldPreferences, KEY_OLD_USER_EMAIL, null), //
                        getValue(mOldPreferences, KEY_OLD_USER_NAME, null), //
                        getValue(mOldPreferences, KEY_OLD_USER_RECOMMENDER, null));
                }

                if (mOldPreferences.contains(KEY_OLD_USER_BENEFIT_ALARM) == true)
                {
                    setUserBenefitAlarm(getValue(mOldPreferences, KEY_OLD_USER_BENEFIT_ALARM, false));
                }

                if (mOldPreferences.contains(KEY_OLD_USER_IS_EXCEED_BONUS) == true)
                {
                    setUserExceedBonus(getValue(mOldPreferences, KEY_OLD_USER_IS_EXCEED_BONUS, false));
                }

                if (mOldPreferences.contains(KEY_OLD_SETTING_VERSION_SKIP_MAX_VERSION) == true)
                {
                    setSkipVersion(getValue(mOldPreferences, KEY_OLD_SETTING_VERSION_SKIP_MAX_VERSION, "1.0.0"));
                }

                if (mOldPreferences.contains(KEY_OLD_PAYMENT_ACCOUNT_READY_FLAG) == true)
                {
                    setVirtualAccountReadyFlag(getValue(mOldPreferences, KEY_OLD_PAYMENT_ACCOUNT_READY_FLAG, -1));
                }

                isMigrationFlag = true;
            } catch (Exception e)
            {
                ExLog.d(e.getMessage());
                isMigrationFlag = false;
            }

            setMigrationFlag(isMigrationFlag);

            if (isMigrationFlag == true)
            {
                clearOldPreference();
            }
        }

    }
}
