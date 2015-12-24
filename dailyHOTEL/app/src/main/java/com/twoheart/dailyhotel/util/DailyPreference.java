package com.twoheart.dailyhotel.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 */
public class DailyPreference
{
    public static final String DAILYHOTEL_SHARED_PREFERENCE = "GOOD_NIGHT"; // 기존에 존재하던
    public static final String DAILYHOTEL_SHARED_PREFERENCE_V1 = "dailyHOTEL_v1"; // 새로 만든

    /////////////////////////////////////////////////////////////////////////////////////////
    // "dailyHOTEL_v1" Preference
    /////////////////////////////////////////////////////////////////////////////////////////

    private static final String KEY_OPENING_ALARM = "1"; // 알람
    private static final String KEY_NEW_EVENT_TODAY_FNB = "2"; // 앱 처음 실행시 FNB에  New 아이콘 넣기
    private static final String KEY_LAST_MENU = "3"; // 마지막 메뉴 리스트가 무엇인지
    private static final String KEY_SHOW_GUIDE = "4"; // 가이드를 봤는지 여부
    private static final String KEY_ALLOW_PUSH = "5";

    private static final String KEY_COLLAPSEKEY = "10"; // 푸시 중복 되지 않도록
    private static final String KEY_SOCIAL_SIGNUP = "11"; // 회원가입시 소셜 가입자인 경우

    private static final String KEY_HOTEL_REGION_ISOVERSEA = "12"; // 현재 선택된 지역이 국내/해외
    private static final String KEY_GOURMET_REGION_ISOVERSEA = "13"; // 현재 선택된 지역이 국내/해외

    private static final String KEY_COMPANY_NAME = "100";
    private static final String KEY_COMPANY_CEO = "101";
    private static final String KEY_COMPANY_BIZREGNUMBER = "102";
    private static final String KEY_COMPANY_ITCREGNUMBER = "103";
    private static final String KEY_COMPANY_ADDRESS = "104";
    private static final String KEY_COMPANY_PHONENUMBER = "105";
    private static final String KEY_COMPANY_FAX = "106";

    /////////////////////////////////////////////////////////////////////////////////////////
    // "GOOD_NIGHT" Preference
    /////////////////////////////////////////////////////////////////////////////////////////

    private static final String KEY_PREFERENCE_BY_SHARE = "BY_SHARE";
    private static final String KEY_PREFERENCE_GCM_ID = "PUSH_ID";

    // Event
    private static final String RESULT_ACTIVITY_SPLASH_NEW_EVENT = "NEW_EVENT";
    private static final String KEY_PREFERENCE_LOOKUP_EVENT_TIME = "LOOKUP_EVENT_TIME";
    private static final String KEY_PREFERENCE_NEW_EVENT_TIME = "NEW_EVENT_TIME";

    // Region
    private static final String KEY_PREFERENCE_REGION_SELECT = "REGION_SELECT";
    //    private static final String KEY_PREFERENCE_REGION_SELECT_BEFORE = "REGION_SELECT_BEFORE";
    private static final String KEY_PREFERENCE_REGION_SETTING = "REGION_SETTING";
    private static final String KEY_PREFERENCE_FNB_REGION_SETTING = "FNB_REGION_SETTING";
    private static final String KEY_PREFERENCE_FNB_REGION_SELECT = "FNB_REGION_SELECT";
    //    private static final String KEY_PREFERENCE_FNB_REGION_SELECT_BEFORE = "FNB_REGION_SELECT_BEFORE";

    // Virtual Account
    private static final String KEY_PREFERENCE_USER_IDX = "USER_IDX"; // 예약 성공했을때 예약 사용함, 이름과 용도가 맞지 않음 -> 기존 코드
    private static final String KEY_PREFERENCE_HOTEL_NAME = "HOTEL_NAME";
    private static final String KEY_PREFERENCE_HOTEL_ROOM_IDX = "HOTEL_RESERVATION_IDX";
    private static final String KEY_PREFERENCE_HOTEL_CHECKOUT = "HOTEL_CHECKOUT";
    private static final String KEY_PREFERENCE_HOTEL_CHECKIN = "HOTEL_CHECKIN";
    private static final String KEY_PREFERENCE_ACCOUNT_READY_FLAG = "ACCOUNT_READY_FLAG"; //

    // User Information
    private static final String KEY_PREFERENCE_AUTO_LOGIN = "AUTO_LOGIN";
    private static final String KEY_PREFERENCE_USER_ID = "USER_ID";
    private static final String KEY_PREFERENCE_USER_ACCESS_TOKEN = "USER_ACCESSTOKEN";
    private static final String KEY_PREFERENCE_USER_PWD = "USER_PWD";
    private static final String KEY_PREFERENCE_USER_TYPE = "USER_TYPE";
    private static final String KEY_PREFERENCE_USER_NAME = "USER_NAME";

    // Version
    private static final String KEY_PREFERENCE_MIN_VERSION_NAME = "MIN_VERSION_NAME";
    private static final String KEY_PREFERENCE_MAX_VERSION_NAME = "MAX_VERSION_NAME";
    private static final String KEY_PREFERENCE_SKIP_MAX_VERSION = "SKIP_MAX_VERSION";


    // payment
    private static final String KEY_PREFERENCE_OVERSEAS_NAME = "OVERSEAS_NAME";
    private static final String KEY_PREFERENCE_OVERSEAS_PHONE = "OVERSEAS_PHONE";
    private static final String KEY_PREFERENCE_OVERSEAS_EMAIL = "OVERSEAS_EMAIL";

    // Google Analytics
    private static final String KEY_PREFERENCE_REGION_SELECT_GA = "REGION_SELECT_GA";
    private static final String KEY_PREFERENCE_HOTEL_NAME_GA = "HOTEL_NAME_GA";
    private static final String KEY_PREFERENCE_PLACE_REGION_SELECT_GA = "PLACE_REGION_SELECT_GA";
    private static final String KEY_PREFERENCE_PLACE_NAME_GA = "PLACE_NAME_GA";


    /////////////////////////////////////////////////////////////////////////////////////////

    private static DailyPreference mInstance;
    private SharedPreferences mPreferences;
    private SharedPreferences mOldPreferences;
    private Editor mEditor;
    private Editor mOldEditor;

    private DailyPreference(Context context)
    {
        mPreferences = context.getSharedPreferences(DAILYHOTEL_SHARED_PREFERENCE_V1, Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();

        mOldPreferences = context.getSharedPreferences(DAILYHOTEL_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        mOldEditor = mOldPreferences.edit();
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
        // 회사 정보는 삭제되면 안된다
        String name = getCompanyName();
        String ceo = getCompanyCEO();
        String bizRegNumber = getCompanyBizRegNumber();
        String itcRegNumber = getCompanyItcRegNumber();
        String address = getCompanyAddress();
        String phoneNumber = getCompanyPhoneNumber();
        String fax = getCompanyFax();

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

        setCompanyInformation(name, ceo, bizRegNumber, itcRegNumber, address, phoneNumber, fax);
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    // "dailyHOTEL_v1" Preference
    /////////////////////////////////////////////////////////////////////////////////////////

    private String getV1Value(String key, String defaultValue)
    {
        String result = defaultValue;

        if (mPreferences != null)
        {
            result = mPreferences.getString(key, defaultValue);
        }

        return result;
    }

    private void setV1Value(String key, String value)
    {
        if (mEditor != null)
        {
            mEditor.putString(key, value);
            mEditor.apply();
        }
    }

    private boolean getV1Value(String key, boolean defaultValue)
    {
        boolean result = defaultValue;

        if (mPreferences != null)
        {
            result = mPreferences.getBoolean(key, defaultValue);
        }

        return result;
    }

    private void setV1Value(String key, boolean value)
    {
        if (mEditor != null)
        {
            mEditor.putBoolean(key, value);
            mEditor.apply();
        }
    }

    private void removeV1Value(String key)
    {
        if (mEditor != null)
        {
            mEditor.remove(key);
            mEditor.apply();
        }
    }

    public boolean getEnabledOpeningAlarm()
    {
        return getV1Value(KEY_OPENING_ALARM, false);
    }

    public void setEnabledOpeningAlarm(boolean value)
    {
        setV1Value(KEY_OPENING_ALARM, value);
    }

    public boolean isNewTodayFnB()
    {
        return getV1Value(KEY_NEW_EVENT_TODAY_FNB, false);
    }

    public void setNewTodayFnB(boolean value)
    {
        setV1Value(KEY_NEW_EVENT_TODAY_FNB, value);
    }

    public String getLastMenu()
    {
        return getV1Value(KEY_LAST_MENU, null);
    }

    public void setLastMenu(String value)
    {
        setV1Value(KEY_LAST_MENU, value);
    }

    public boolean isShowGuide()
    {
        return getV1Value(KEY_SHOW_GUIDE, false);
    }

    public void setShowGuide(boolean value)
    {
        setV1Value(KEY_SHOW_GUIDE, value);
    }

    public boolean isAllowPush()
    {
        return getV1Value(KEY_ALLOW_PUSH, true);
    }

    public void setAllowPush(boolean value)
    {
        setV1Value(KEY_ALLOW_PUSH, value);
    }

    public void setCompanyInformation(String name, String ceo, String bizRegNumber//
        , String itcRegNumber, String address, String phoneNumber, String fax)
    {
        if (mEditor != null)
        {
            mEditor.putString(KEY_COMPANY_NAME, name);
            mEditor.putString(KEY_COMPANY_CEO, ceo);
            mEditor.putString(KEY_COMPANY_BIZREGNUMBER, bizRegNumber);
            mEditor.putString(KEY_COMPANY_ITCREGNUMBER, itcRegNumber);
            mEditor.putString(KEY_COMPANY_ADDRESS, address);
            mEditor.putString(KEY_COMPANY_PHONENUMBER, phoneNumber);
            mEditor.putString(KEY_COMPANY_FAX, fax);
            mEditor.apply();
        }
    }

    public String getCompanyName()
    {
        return getV1Value(KEY_COMPANY_NAME, null);
    }

    public String getCompanyCEO()
    {
        return getV1Value(KEY_COMPANY_CEO, null);
    }

    public String getCompanyBizRegNumber()
    {
        return getV1Value(KEY_COMPANY_BIZREGNUMBER, null);
    }

    public String getCompanyItcRegNumber()
    {
        return getV1Value(KEY_COMPANY_ITCREGNUMBER, null);
    }

    public String getCompanyAddress()
    {
        return getV1Value(KEY_COMPANY_ADDRESS, null);
    }

    public String getCompanyPhoneNumber()
    {
        return getV1Value(KEY_COMPANY_PHONENUMBER, null);
    }

    public String getCompanyFax()
    {
        return getV1Value(KEY_COMPANY_FAX, null);
    }

    public String getCollapsekey()
    {
        return getV1Value(KEY_COLLAPSEKEY, null);
    }

    public void setCollapsekey(String value)
    {
        setV1Value(KEY_COLLAPSEKEY, value);
    }

    public boolean isSocialSignUp()
    {
        return getV1Value(KEY_SOCIAL_SIGNUP, false);
    }

    public void setSocialSignUp(boolean value)
    {
        setV1Value(KEY_SOCIAL_SIGNUP, value);
    }

    public boolean isSelectedOverseaRegion(Constants.TYPE type)
    {
        switch (type)
        {
            case FNB:
                return getV1Value(KEY_GOURMET_REGION_ISOVERSEA, false);

            case HOTEL:
            default:
                return getV1Value(KEY_HOTEL_REGION_ISOVERSEA, false);
        }
    }

    public void setSelectedOverseaRegion(Constants.TYPE type, boolean value)
    {
        switch (type)
        {
            case HOTEL:
                setV1Value(KEY_HOTEL_REGION_ISOVERSEA, value);
                break;

            case FNB:
                setV1Value(KEY_GOURMET_REGION_ISOVERSEA, value);
                break;
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    // "GOOD_NIGHT" Preference
    /////////////////////////////////////////////////////////////////////////////////////////

    private String getValue(String key, String defaultValue)
    {
        String result = defaultValue;

        if (mOldPreferences != null)
        {
            result = mOldPreferences.getString(key, defaultValue);
        }

        return result;
    }

    private void setValue(String key, String value)
    {
        if (mOldEditor != null)
        {
            mOldEditor.putString(key, value);
            mOldEditor.apply();
        }
    }

    private boolean getValue(String key, boolean defaultValue)
    {
        boolean result = defaultValue;

        if (mOldPreferences != null)
        {
            result = mOldPreferences.getBoolean(key, defaultValue);
        }

        return result;
    }

    private void setValue(String key, boolean value)
    {
        if (mOldEditor != null)
        {
            mOldEditor.putBoolean(key, value);
            mOldEditor.apply();
        }
    }

    private long getValue(String key, long defaultValue)
    {
        long result = defaultValue;

        if (mOldPreferences != null)
        {
            result = mOldPreferences.getLong(key, defaultValue);
        }

        return result;
    }

    private void setValue(String key, int value)
    {
        if (mOldEditor != null)
        {
            mOldEditor.putInt(key, value);
            mOldEditor.apply();
        }
    }

    private int getValue(String key, int defaultValue)
    {
        int result = defaultValue;

        if (mOldPreferences != null)
        {
            result = mOldPreferences.getInt(key, defaultValue);
        }

        return result;
    }

    private void setValue(String key, long value)
    {
        if (mOldEditor != null)
        {
            mOldEditor.putLong(key, value);
            mOldEditor.apply();
        }
    }

    private void removeValue(String key)
    {
        if (mOldEditor != null)
        {
            mOldEditor.remove(key);
            mOldEditor.apply();
        }
    }

    public void removeDeepLink()
    {
        removeValue(KEY_PREFERENCE_BY_SHARE);
    }

    public String getDeepLink()
    {
        return getValue(KEY_PREFERENCE_BY_SHARE, null);
    }

    public void setDeepLink(String value)
    {
        setValue(KEY_PREFERENCE_BY_SHARE, value);
    }

    public String getGCMRegistrationId()
    {
        return getValue(KEY_PREFERENCE_GCM_ID, null);
    }

    public void setGCMRegistrationId(String value)
    {
        setValue(KEY_PREFERENCE_GCM_ID, value);
    }

    public boolean hasNewEvent()
    {
        return getValue(RESULT_ACTIVITY_SPLASH_NEW_EVENT, false);
    }

    public void setNewEvent(boolean value)
    {
        setValue(RESULT_ACTIVITY_SPLASH_NEW_EVENT, value);
    }

    public long getLookUpEventTime()
    {
        return getValue(KEY_PREFERENCE_LOOKUP_EVENT_TIME, 0L);
    }

    public void setLookUpEventTime(long value)
    {
        setValue(KEY_PREFERENCE_LOOKUP_EVENT_TIME, value);
    }

    public long getNewEventTime()
    {
        return getValue(KEY_PREFERENCE_NEW_EVENT_TIME, 0L);
    }

    public void setNewEventTime(long value)
    {
        setValue(KEY_PREFERENCE_NEW_EVENT_TIME, value);
    }

    public String getSelectedRegion(Constants.TYPE type)
    {
        switch (type)
        {
            case FNB:
                return getValue(KEY_PREFERENCE_FNB_REGION_SELECT, null);

            case HOTEL:
            default:
                return getValue(KEY_PREFERENCE_REGION_SELECT, null);
        }
    }

    public void setSelectedRegion(Constants.TYPE type, String value)
    {
        switch (type)
        {
            case HOTEL:
                setValue(KEY_PREFERENCE_REGION_SELECT, value);
                break;

            case FNB:
                setValue(KEY_PREFERENCE_FNB_REGION_SELECT, value);
                break;
        }
    }

    public boolean isSettingRegion(Constants.TYPE type)
    {
        switch (type)
        {
            case FNB:
                return getValue(KEY_PREFERENCE_FNB_REGION_SETTING, false);

            case HOTEL:
            default:
                return getValue(KEY_PREFERENCE_REGION_SETTING, false);
        }
    }

    public void setSettingRegion(Constants.TYPE type, boolean value)
    {
        switch (type)
        {
            case HOTEL:
                setValue(KEY_PREFERENCE_REGION_SETTING, value);
                break;

            case FNB:
                setValue(KEY_PREFERENCE_FNB_REGION_SETTING, value);
                break;
        }
    }

    public String getOverseasName()
    {
        return getValue(KEY_PREFERENCE_OVERSEAS_NAME, null);
    }

    public String getOverseasPhone()
    {
        return getValue(KEY_PREFERENCE_OVERSEAS_PHONE, null);
    }

    public String getOverseasEmail()
    {
        return getValue(KEY_PREFERENCE_OVERSEAS_EMAIL, null);
    }

    public void setOverseasUserInformation(String name, String phone, String email)
    {
        if (mOldEditor != null)
        {
            mOldEditor.putString(KEY_PREFERENCE_OVERSEAS_NAME, name);
            mOldEditor.putString(KEY_PREFERENCE_OVERSEAS_PHONE, phone);
            mOldEditor.putString(KEY_PREFERENCE_OVERSEAS_EMAIL, email);
            mOldEditor.apply();
        }
    }

    public String getVirtualAccountUserIndex()
    {
        return getValue(KEY_PREFERENCE_USER_IDX, "0");
    }

    public String getVirtualAccountHotelName()
    {
        return getValue(KEY_PREFERENCE_HOTEL_NAME, "0");
    }

    public String getVirtualAccountRoomIndex()
    {
        return getValue(KEY_PREFERENCE_HOTEL_ROOM_IDX, "0");
    }

    public String getVirtualAccountCheckIn()
    {
        return getValue(KEY_PREFERENCE_HOTEL_CHECKIN, "0");
    }

    public String getVirtualAccountCheckOut()
    {
        return getValue(KEY_PREFERENCE_HOTEL_CHECKOUT, "0");
    }

    public void setVirtuaAccountInformation(String userIndex, String hotelName, String roomIndex//
        , String checkIn, String checkOut)
    {
        if (mOldEditor != null)
        {
            mOldEditor.putString(KEY_PREFERENCE_USER_IDX, userIndex);
            mOldEditor.putString(KEY_PREFERENCE_HOTEL_NAME, hotelName);
            mOldEditor.putString(KEY_PREFERENCE_HOTEL_ROOM_IDX, roomIndex);
            mOldEditor.putString(KEY_PREFERENCE_HOTEL_CHECKIN, checkIn);
            mOldEditor.putString(KEY_PREFERENCE_HOTEL_CHECKOUT, checkOut);
            mOldEditor.apply();
        }
    }

    public void removeVirtualAccountInformation()
    {
        if (mOldEditor != null)
        {
            mOldEditor.remove(KEY_PREFERENCE_USER_IDX);
            mOldEditor.remove(KEY_PREFERENCE_HOTEL_NAME);
            mOldEditor.remove(KEY_PREFERENCE_HOTEL_ROOM_IDX);
            mOldEditor.remove(KEY_PREFERENCE_HOTEL_CHECKIN);
            mOldEditor.remove(KEY_PREFERENCE_HOTEL_CHECKOUT);
            mOldEditor.apply();
        }
    }

    public int getVirtualAccountReadyFlag()
    {
        return getValue(KEY_PREFERENCE_ACCOUNT_READY_FLAG, -1);
    }

    public void setVirtualAccountReadyFlag(int value)
    {
        setValue(KEY_PREFERENCE_ACCOUNT_READY_FLAG, value);
    }


    public boolean isAutoLogin()
    {
        return getValue(KEY_PREFERENCE_AUTO_LOGIN, false);
    }

    public void setAutoLogin(boolean value)
    {
        setValue(KEY_PREFERENCE_AUTO_LOGIN, value);
    }

    public String getUserId()
    {
        return getValue(KEY_PREFERENCE_USER_ID, null);
    }

    public void setUserId(String value)
    {
        setValue(KEY_PREFERENCE_USER_ID, value);
    }

    public String getUserAccessToken()
    {
        return getValue(KEY_PREFERENCE_USER_ACCESS_TOKEN, null);
    }

    public void setUserAccessToken(String value)
    {
        setValue(KEY_PREFERENCE_USER_ACCESS_TOKEN, value);
    }

    public String getUserPassword()
    {
        return getValue(KEY_PREFERENCE_USER_PWD, null);
    }

    public void setUserPassword(String value)
    {
        setValue(KEY_PREFERENCE_USER_PWD, value);
    }

    public String getUserType()
    {
        return getValue(KEY_PREFERENCE_USER_TYPE, null);
    }

    public void setUserType(String value)
    {
        setValue(KEY_PREFERENCE_USER_TYPE, value);
    }

    public String getUserName()
    {
        return getValue(KEY_PREFERENCE_USER_NAME, null);
    }

    public void setUserName(String value)
    {
        setValue(KEY_PREFERENCE_USER_NAME, value);
    }

    public void setUserInformation(boolean isAutoLogin, String id, String password, String type, String name)
    {
        if (mOldEditor != null)
        {
            mOldEditor.putBoolean(KEY_PREFERENCE_AUTO_LOGIN, isAutoLogin);
            mOldEditor.putString(KEY_PREFERENCE_USER_ID, id);
            mOldEditor.putString(KEY_PREFERENCE_USER_PWD, password);
            mOldEditor.putString(KEY_PREFERENCE_USER_TYPE, type);
            mOldEditor.putString(KEY_PREFERENCE_USER_NAME, name);
            mOldEditor.apply();
        }
    }

    public void removeUserInformation()
    {
        if (mOldEditor != null)
        {
            mOldEditor.remove(KEY_PREFERENCE_AUTO_LOGIN);
            mOldEditor.remove(KEY_PREFERENCE_USER_ID);
            mOldEditor.remove(KEY_PREFERENCE_USER_PWD);
            mOldEditor.remove(KEY_PREFERENCE_USER_TYPE);
            mOldEditor.remove(KEY_PREFERENCE_USER_ACCESS_TOKEN);
            mOldEditor.remove(KEY_PREFERENCE_USER_NAME);
            mOldEditor.apply();
        }
    }

    public String getMaxVersion()
    {
        return getValue(KEY_PREFERENCE_MAX_VERSION_NAME, "1.0.0");
    }

    public void setMaxVersion(String value)
    {
        setValue(KEY_PREFERENCE_MAX_VERSION_NAME, value);
    }

    public String getMinVersion()
    {
        return getValue(KEY_PREFERENCE_MIN_VERSION_NAME, "1.0.0");
    }

    public void setMinVersion(String value)
    {
        setValue(KEY_PREFERENCE_MIN_VERSION_NAME, value);
    }

    public String getSkipVersion()
    {
        return getValue(KEY_PREFERENCE_SKIP_MAX_VERSION, "1.0.0");
    }

    public void setSkipVersion(String value)
    {
        setValue(KEY_PREFERENCE_SKIP_MAX_VERSION, value);
    }


    public String getGASelectedRegion()
    {
        return getValue(KEY_PREFERENCE_REGION_SELECT_GA, null);
    }

    public void setGASelectedRegion(String value)
    {
        setValue(KEY_PREFERENCE_REGION_SELECT_GA, value);
    }

    public String getGAHotelName()
    {
        return getValue(KEY_PREFERENCE_HOTEL_NAME_GA, null);
    }

    public void setGAHotelName(String value)
    {
        setValue(KEY_PREFERENCE_HOTEL_NAME_GA, value);
    }

    public String getGASelectedPlaceRegion()
    {
        return getValue(KEY_PREFERENCE_PLACE_REGION_SELECT_GA, null);
    }

    public void setGASelectedPlaceRegion(String value)
    {
        setValue(KEY_PREFERENCE_PLACE_REGION_SELECT_GA, value);
    }

    public String getGASelectedPlaceName()
    {
        return getValue(KEY_PREFERENCE_PLACE_NAME_GA, null);
    }

    public void setGASelectedPlaceName(String value)
    {
        setValue(KEY_PREFERENCE_PLACE_NAME_GA, value);
    }


}
