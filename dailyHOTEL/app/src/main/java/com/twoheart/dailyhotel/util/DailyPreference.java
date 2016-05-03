package com.twoheart.dailyhotel.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.GourmetPaymentInformation;
import com.twoheart.dailyhotel.model.HotelPaymentInformation;
import com.twoheart.dailyhotel.model.SaleRoomInformation;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.TicketInformation;
import com.twoheart.dailyhotel.network.request.DailyHotelRequest;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 */
public class DailyPreference
{
    public static final String DAILYHOTEL_SHARED_PREFERENCE = "GOOD_NIGHT"; // 기존에 존재하던
    public static final String DAILYHOTEL_SHARED_PREFERENCE_V1 = "dailyHOTEL_v1"; // 새로 만든
    public static final String DAILYHOTEL_VBANK_PREFERENCE_V1 = "DAIYHOTEK_VBANKL_v1"; // 새로 만든

    /////////////////////////////////////////////////////////////////////////////////////////
    // "dailyHOTEL_v1" Preference
    /////////////////////////////////////////////////////////////////////////////////////////

    private static final String KEY_OPENING_ALARM = "1"; // 알람
    private static final String KEY_LAST_MENU = "3"; // 마지막 메뉴 리스트가 무엇인지
    private static final String KEY_SHOW_GUIDE = "4"; // 가이드를 봤는지 여부
    private static final String KEY_ALLOW_PUSH = "5";
    private static final String KEY_ALLOW_SMS = "6";

    private static final String KEY_COLLAPSEKEY = "10"; // 푸시 중복 되지 않도록
    //    private static final String KEY_SOCIAL_SIGNUP = "11"; // 회원가입시 소셜 가입자인 경우

    private static final String KEY_HOTEL_REGION_ISOVERSEA = "12"; // 현재 선택된 지역이 국내/해외
    private static final String KEY_GOURMET_REGION_ISOVERSEA = "13"; // 현재 선택된 지역이 국내/해외

    private static final String KEY_NEW_EVENT = "14"; // 현재 이벤트 유무

    private static final String KEY_NOTIFICATION_UID = "20"; // 노티피케이션 UID

    private static final String KEY_AGREE_TERMS_OF_LOCATION = "21"; // 위치 약관 동의 여부
    private static final String KEY_INFORMATION_CS_OPERATION_TIMEMESSAGE = "22"; // 운영시간 문구

    private static final String KEY_COMPANY_NAME = "100";
    private static final String KEY_COMPANY_CEO = "101";
    private static final String KEY_COMPANY_BIZREGNUMBER = "102";
    private static final String KEY_COMPANY_ITCREGNUMBER = "103";
    private static final String KEY_COMPANY_ADDRESS = "104";
    private static final String KEY_COMPANY_PHONENUMBER = "105";
    private static final String KEY_COMPANY_FAX = "106";
    private static final String KEY_COMPANY_PRIVACY_EMAIL = "107";

    private static final String KEY_HOTEL_SEARCH_RECENTLY = "200";
    private static final String KEY_GOURMET_SEARCH_RECENTLY = "201";

    private static final String KEY_AUTHORIZATION = "1000";


    /////////////////////////////////////////////////////////////////////////////////////////
    // "GOOD_NIGHT" Preference
    /////////////////////////////////////////////////////////////////////////////////////////

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

    // Virtual Account
    private static final String KEY_PREFERENCE_VBANK_USER_INDEX = "VBANK_USER_IDX"; // 예약 성공했을때 예약 사용함, 이름과 용도가 맞지 않음 -> 기존 코드
    private static final String KEY_PREFERENCE_VBANK_PLACE_NAME = "VBANK_PLACE_NAME";
    private static final String KEY_PREFERENCE_VBANK_PRICE = "VBANK_PRICE";
    private static final String KEY_PREFERENCE_VBANK_QUANTITY = "VBANK_QUANTITY";
    private static final String KEY_PREFERENCE_VBANK_TOTAL_PRICE = "VBANK_TOTAL_PRICE";
    private static final String KEY_PREFERENCE_VBANK_PLACE_INDEX = "VBANK_PLACE_INDEX";
    private static final String KEY_PREFERENCE_VBANK_TICKET_NAME = "VBANK_TICKET_NAME";
    private static final String KEY_PREFERENCE_VBANK_TICKET_INDEX = "VBANK_TICKET_INDEX";
    private static final String KEY_PREFERENCE_VBANK_CHECKOUT = "VBANK_CHECKOUT";
    private static final String KEY_PREFERENCE_VBANK_CHECKIN = "VBANK_CHECKIN";
    private static final String KEY_PREFERENCE_VBANK_DATE = "VBANK_DATE";
    private static final String KEY_PREFERENCE_VBANK_USED_BONUS = "VBANK_USED_BONUS";
    private static final String KEY_PREFERENCE_VBANK_PAYMENT_PRICE = "VBANK_PAYMENT_PRICE";
    private static final String KEY_PREFERENCE_VBANK_PAYMENT_TYPE = "VBANK_PAYMENT_TYPE";
    private static final String KEY_PREFERENCE_VBANK_RESERVATION_TIME = "VBANK_RESERVATION_TIME";
    private static final String KEY_PREFERENCE_VBANK_PLACE_TYPE = "VBANK_PLACE_TYPE";
    private static final String KEY_PREFERENCE_VBANK_CATEGORY = "VBANK_CATEGORY";
    private static final String KEY_PREFERENCE_VBANK_GRADE = "VBANK_GRADE";
    private static final String KEY_PREFERENCE_VBANK_DBENEFIT = "VBANK_DBENEFIT";

    private static final String KEY_PREFERENCE_ACCOUNT_READY_FLAG = "ACCOUNT_READY_FLAG"; //

    /////////////////////////////////////////////////////////////////////////////////////////

    private static DailyPreference mInstance;
    private SharedPreferences mPreferences;
    private SharedPreferences mOldPreferences;
    private SharedPreferences mVBankPreferences;
    private Editor mEditor;
    private Editor mOldEditor;
    private Editor mVBankEditor;

    private DailyPreference(Context context)
    {
        mPreferences = context.getSharedPreferences(DAILYHOTEL_SHARED_PREFERENCE_V1, Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();

        mOldPreferences = context.getSharedPreferences(DAILYHOTEL_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        mOldEditor = mOldPreferences.edit();

        mVBankPreferences = context.getSharedPreferences(DAILYHOTEL_VBANK_PREFERENCE_V1, Context.MODE_PRIVATE);
        mVBankEditor = mVBankPreferences.edit();
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
        String privacyEmail = getCompanyPrivacyEmail();

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

        if (mVBankEditor != null)
        {
            mVBankEditor.clear();
            mVBankEditor.apply();
        }

        setCompanyInformation(name, ceo, bizRegNumber, itcRegNumber, address, phoneNumber, fax, privacyEmail);
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
            editor.putString(key, value);
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

    public boolean isAllowPush()
    {
        return getValue(mPreferences, KEY_ALLOW_PUSH, false);
    }

    public void setAllowPush(boolean value)
    {
        setValue(mEditor, KEY_ALLOW_PUSH, value);
    }

    public boolean isAllowSMS()
    {
        return getValue(mPreferences, KEY_ALLOW_SMS, false);
    }

    public void setAllowSMS(boolean value)
    {
        setValue(mEditor, KEY_ALLOW_SMS, value);
    }

    public void setCompanyInformation(String name, String ceo, String bizRegNumber//
        , String itcRegNumber, String address, String phoneNumber, String fax, String email)
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

            if (Util.isTextEmpty(email) == false)
            {
                mEditor.putString(KEY_COMPANY_PRIVACY_EMAIL, email);
            }

            mEditor.apply();
        }
    }

    public String getCompanyName()
    {
        return getValue(mPreferences, KEY_COMPANY_NAME, null);
    }

    public String getCompanyCEO()
    {
        return getValue(mPreferences, KEY_COMPANY_CEO, null);
    }

    public String getCompanyBizRegNumber()
    {
        return getValue(mPreferences, KEY_COMPANY_BIZREGNUMBER, null);
    }

    public String getCompanyItcRegNumber()
    {
        return getValue(mPreferences, KEY_COMPANY_ITCREGNUMBER, null);
    }

    public String getCompanyAddress()
    {
        return getValue(mPreferences, KEY_COMPANY_ADDRESS, null);
    }

    public String getCompanyPhoneNumber()
    {
        return getValue(mPreferences, KEY_COMPANY_PHONENUMBER, null);
    }

    public String getCompanyFax()
    {
        return getValue(mPreferences, KEY_COMPANY_FAX, null);
    }

    public String getCompanyPrivacyEmail()
    {
        return getValue(mPreferences, KEY_COMPANY_PRIVACY_EMAIL, "privacy.korea@dailyhotel.com");
    }

    public String getCollapsekey()
    {
        return getValue(mPreferences, KEY_COLLAPSEKEY, null);
    }

    public void setCollapsekey(String value)
    {
        setValue(mEditor, KEY_COLLAPSEKEY, value);
    }

    //    public boolean isSocialSignUp()
    //    {
    //        return getValue(mPreferences, KEY_SOCIAL_SIGNUP, false);
    //    }
    //
    //    public void setSocialSignUp(boolean value)
    //    {
    //        setValue(mEditor, KEY_SOCIAL_SIGNUP, value);
    //    }

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
        return DailyHotelRequest.urlDecrypt(getValue(mPreferences, KEY_AUTHORIZATION, null));
    }

    public void setAuthorization(String value)
    {
        DailyHotel.AUTHORIZATION = value;

        setValue(mEditor, KEY_AUTHORIZATION, DailyHotelRequest.urlEncrypt(value));
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

    /////////////////////////////////////////////////////////////////////////////////////////
    // "GOOD_NIGHT" Preference
    /////////////////////////////////////////////////////////////////////////////////////////

    public String getGCMRegistrationId()
    {
        return getValue(mOldPreferences, KEY_PREFERENCE_GCM_ID, null);
    }

    public void setGCMRegistrationId(String value)
    {
        setValue(mOldEditor, KEY_PREFERENCE_GCM_ID, value);
    }

    public long getLookUpEventTime()
    {
        return getValue(mOldPreferences, KEY_PREFERENCE_LOOKUP_EVENT_TIME, 0L);
    }

    public void setLookUpEventTime(long value)
    {
        setValue(mOldEditor, KEY_PREFERENCE_LOOKUP_EVENT_TIME, value);
    }

    public long getNewEventTime()
    {
        return getValue(mOldPreferences, KEY_PREFERENCE_NEW_EVENT_TIME, 0L);
    }

    public void setNewEventTime(long value)
    {
        setValue(mOldEditor, KEY_PREFERENCE_NEW_EVENT_TIME, value);
    }

    public String getSelectedRegion(Constants.PlaceType placeType)
    {
        switch (placeType)
        {
            case FNB:
                return getValue(mOldPreferences, KEY_PREFERENCE_FNB_REGION_SELECT, null);

            case HOTEL:
            default:
                return getValue(mOldPreferences, KEY_PREFERENCE_REGION_SELECT, null);
        }
    }

    public void setSelectedRegion(Constants.PlaceType placeType, String value)
    {
        switch (placeType)
        {
            case HOTEL:
                setValue(mOldEditor, KEY_PREFERENCE_REGION_SELECT, value);
                break;

            case FNB:
                setValue(mOldEditor, KEY_PREFERENCE_FNB_REGION_SELECT, value);
                break;
        }
    }

    public boolean isSettingRegion(Constants.PlaceType placeType)
    {
        switch (placeType)
        {
            case FNB:
                return getValue(mOldPreferences, KEY_PREFERENCE_FNB_REGION_SETTING, false);

            case HOTEL:
            default:
                return getValue(mOldPreferences, KEY_PREFERENCE_REGION_SETTING, false);
        }
    }

    public void setSettingRegion(Constants.PlaceType placeType, boolean value)
    {
        switch (placeType)
        {
            case HOTEL:
                setValue(mOldEditor, KEY_PREFERENCE_REGION_SETTING, value);
                break;

            case FNB:
                setValue(mOldEditor, KEY_PREFERENCE_FNB_REGION_SETTING, value);
                break;
        }
    }

    public String getOverseasName()
    {
        return getValue(mOldPreferences, KEY_PREFERENCE_OVERSEAS_NAME, null);
    }

    public String getOverseasPhone()
    {
        return getValue(mOldPreferences, KEY_PREFERENCE_OVERSEAS_PHONE, null);
    }

    public String getOverseasEmail()
    {
        return getValue(mOldPreferences, KEY_PREFERENCE_OVERSEAS_EMAIL, null);
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

    public boolean isAutoLogin()
    {
        return getValue(mOldPreferences, KEY_PREFERENCE_AUTO_LOGIN, true);
    }

    public void setAutoLogin(boolean value)
    {
        setValue(mOldEditor, KEY_PREFERENCE_AUTO_LOGIN, value);
    }

    public String getUserId()
    {
        return getValue(mOldPreferences, KEY_PREFERENCE_USER_ID, null);
    }

    public void setUserId(String value)
    {
        setValue(mOldEditor, KEY_PREFERENCE_USER_ID, value);
    }

    public String getUserAccessToken()
    {
        return getValue(mOldPreferences, KEY_PREFERENCE_USER_ACCESS_TOKEN, null);
    }

    public void setUserAccessToken(String value)
    {
        setValue(mOldEditor, KEY_PREFERENCE_USER_ACCESS_TOKEN, value);
    }

    public String getUserPassword()
    {
        return getValue(mOldPreferences, KEY_PREFERENCE_USER_PWD, null);
    }

    public void setUserPassword(String value)
    {
        setValue(mOldEditor, KEY_PREFERENCE_USER_PWD, value);
    }

    public String getUserType()
    {
        return getValue(mOldPreferences, KEY_PREFERENCE_USER_TYPE, null);
    }

    public void setUserType(String value)
    {
        setValue(mOldEditor, KEY_PREFERENCE_USER_TYPE, value);
    }

    public String getUserName()
    {
        return getValue(mOldPreferences, KEY_PREFERENCE_USER_NAME, null);
    }

    public void setUserName(String value)
    {
        setValue(mOldEditor, KEY_PREFERENCE_USER_NAME, value);
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
            mEditor.remove(KEY_AUTHORIZATION);

            DailyHotel.AUTHORIZATION = null;

            mOldEditor.apply();
        }
    }

    public String getMaxVersion()
    {
        return getValue(mOldPreferences, KEY_PREFERENCE_MAX_VERSION_NAME, "1.0.0");
    }

    public void setMaxVersion(String value)
    {
        setValue(mOldEditor, KEY_PREFERENCE_MAX_VERSION_NAME, value);
    }

    public String getMinVersion()
    {
        return getValue(mOldPreferences, KEY_PREFERENCE_MIN_VERSION_NAME, "1.0.0");
    }

    public void setMinVersion(String value)
    {
        setValue(mOldEditor, KEY_PREFERENCE_MIN_VERSION_NAME, value);
    }

    public String getSkipVersion()
    {
        return getValue(mOldPreferences, KEY_PREFERENCE_SKIP_MAX_VERSION, "1.0.0");
    }

    public void setSkipVersion(String value)
    {
        setValue(mOldEditor, KEY_PREFERENCE_SKIP_MAX_VERSION, value);
    }


    public String getGASelectedRegion()
    {
        return getValue(mOldPreferences, KEY_PREFERENCE_REGION_SELECT_GA, null);
    }

    public void setGASelectedRegion(String value)
    {
        setValue(mOldEditor, KEY_PREFERENCE_REGION_SELECT_GA, value);
    }

    public String getGAHotelName()
    {
        return getValue(mOldPreferences, KEY_PREFERENCE_HOTEL_NAME_GA, null);
    }

    public void setGAHotelName(String value)
    {
        setValue(mOldEditor, KEY_PREFERENCE_HOTEL_NAME_GA, value);
    }

    public String getGASelectedPlaceRegion()
    {
        return getValue(mOldPreferences, KEY_PREFERENCE_PLACE_REGION_SELECT_GA, null);
    }

    public void setGASelectedPlaceRegion(String value)
    {
        setValue(mOldEditor, KEY_PREFERENCE_PLACE_REGION_SELECT_GA, value);
    }

    public String getGASelectedPlaceName()
    {
        return getValue(mOldPreferences, KEY_PREFERENCE_PLACE_NAME_GA, null);
    }

    public void setGASelectedPlaceName(String value)
    {
        setValue(mOldEditor, KEY_PREFERENCE_PLACE_NAME_GA, value);
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    // "DAIYHOTEK_VBANKL_v1" Preference
    /////////////////////////////////////////////////////////////////////////////////////////

    public String getVBankPlaceType()
    {
        return getValue(mVBankPreferences, KEY_PREFERENCE_VBANK_PLACE_TYPE, null);
    }

    public String getVBankUserIndex()
    {
        return getValue(mVBankPreferences, KEY_PREFERENCE_VBANK_USER_INDEX, null);
    }

    public void setVirtuaAccountHotelInformation(Context context, HotelPaymentInformation hotelPaymentInformation, SaleTime checkInSaleTime)
    {
        if (mVBankEditor != null)
        {
            mVBankEditor.clear();

            SaleRoomInformation saleRoomInformation = hotelPaymentInformation.getSaleRoomInformation();

            mVBankEditor.putString(KEY_PREFERENCE_VBANK_PLACE_TYPE, AnalyticsManager.Label.HOTEL);
            mVBankEditor.putString(KEY_PREFERENCE_VBANK_USER_INDEX, hotelPaymentInformation.getCustomer().getUserIdx());
            mVBankEditor.putString(KEY_PREFERENCE_VBANK_PLACE_NAME, saleRoomInformation.hotelName);
            mVBankEditor.putString(KEY_PREFERENCE_VBANK_PRICE, Integer.toString(saleRoomInformation.averageDiscount));
            mVBankEditor.putString(KEY_PREFERENCE_VBANK_QUANTITY, Integer.toString(saleRoomInformation.nights));
            mVBankEditor.putString(KEY_PREFERENCE_VBANK_TOTAL_PRICE, Integer.toString(saleRoomInformation.totalDiscount));
            mVBankEditor.putString(KEY_PREFERENCE_VBANK_PLACE_INDEX, Integer.toString(hotelPaymentInformation.placeIndex));
            mVBankEditor.putString(KEY_PREFERENCE_VBANK_TICKET_NAME, saleRoomInformation.roomName);
            mVBankEditor.putString(KEY_PREFERENCE_VBANK_TICKET_INDEX, Integer.toString(saleRoomInformation.roomIndex));
            mVBankEditor.putString(KEY_PREFERENCE_VBANK_GRADE, hotelPaymentInformation.getSaleRoomInformation().grade.getName(context));
            mVBankEditor.putString(KEY_PREFERENCE_VBANK_DBENEFIT, hotelPaymentInformation.isDBenefit ? "yes" : "no");

            SaleTime checkOutSaleTime = checkInSaleTime.getClone(checkInSaleTime.getOffsetDailyDay() + saleRoomInformation.nights);
            mVBankEditor.putString(KEY_PREFERENCE_VBANK_CHECKIN, checkInSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"));
            mVBankEditor.putString(KEY_PREFERENCE_VBANK_CHECKOUT, checkOutSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"));

            if (hotelPaymentInformation.isEnabledBonus == true)
            {
                int payPrice = saleRoomInformation.totalDiscount - hotelPaymentInformation.bonus;
                int bonus;

                if (payPrice <= 0)
                {
                    payPrice = 0;
                    bonus = saleRoomInformation.totalDiscount;
                } else
                {
                    bonus = hotelPaymentInformation.bonus;
                }

                mVBankEditor.putString(KEY_PREFERENCE_VBANK_USED_BONUS, Integer.toString(bonus));
                mVBankEditor.putString(KEY_PREFERENCE_VBANK_PAYMENT_PRICE, Integer.toString(payPrice));
            } else
            {
                mVBankEditor.putString(KEY_PREFERENCE_VBANK_USED_BONUS, "0");
                mVBankEditor.putString(KEY_PREFERENCE_VBANK_PAYMENT_PRICE, Integer.toString(saleRoomInformation.totalDiscount));
            }

            mVBankEditor.putString(KEY_PREFERENCE_VBANK_PAYMENT_TYPE, hotelPaymentInformation.paymentType.name());
            mVBankEditor.apply();
        }
    }

    public Map<String, String> getVirtuaAccountHotelInformation()
    {
        Map<String, String> params = new HashMap<>();

        params.put(AnalyticsManager.KeyType.USER_INDEX, getValue(mVBankPreferences, KEY_PREFERENCE_VBANK_USER_INDEX, null));
        params.put(AnalyticsManager.KeyType.NAME, getValue(mVBankPreferences, KEY_PREFERENCE_VBANK_PLACE_NAME, null));
        params.put(AnalyticsManager.KeyType.PRICE, getValue(mVBankPreferences, KEY_PREFERENCE_VBANK_PRICE, null));
        params.put(AnalyticsManager.KeyType.QUANTITY, getValue(mVBankPreferences, KEY_PREFERENCE_VBANK_QUANTITY, null));
        params.put(AnalyticsManager.KeyType.TOTAL_PRICE, getValue(mVBankPreferences, KEY_PREFERENCE_VBANK_TOTAL_PRICE, null));
        params.put(AnalyticsManager.KeyType.PLACE_INDEX, getValue(mVBankPreferences, KEY_PREFERENCE_VBANK_PLACE_INDEX, null));
        params.put(AnalyticsManager.KeyType.TICKET_NAME, getValue(mVBankPreferences, KEY_PREFERENCE_VBANK_TICKET_NAME, null));
        params.put(AnalyticsManager.KeyType.TICKET_INDEX, getValue(mVBankPreferences, KEY_PREFERENCE_VBANK_TICKET_INDEX, null));
        params.put(AnalyticsManager.KeyType.CHECK_IN, getValue(mVBankPreferences, KEY_PREFERENCE_VBANK_CHECKIN, null));
        params.put(AnalyticsManager.KeyType.CHECK_OUT, getValue(mVBankPreferences, KEY_PREFERENCE_VBANK_CHECKOUT, null));
        params.put(AnalyticsManager.KeyType.USED_BOUNS, getValue(mVBankPreferences, KEY_PREFERENCE_VBANK_USED_BONUS, null));
        params.put(AnalyticsManager.KeyType.PAYMENT_PRICE, getValue(mVBankPreferences, KEY_PREFERENCE_VBANK_PAYMENT_PRICE, null));
        params.put(AnalyticsManager.KeyType.PAYMENT_TYPE, getValue(mVBankPreferences, KEY_PREFERENCE_VBANK_PAYMENT_TYPE, null));
        params.put(AnalyticsManager.KeyType.GRADE, getValue(mVBankPreferences, KEY_PREFERENCE_VBANK_GRADE, null));
        params.put(AnalyticsManager.KeyType.DBENEFIT, getValue(mVBankPreferences, KEY_PREFERENCE_VBANK_DBENEFIT, null));

        return params;
    }

    public void setVirtuaAccountGourmetInformation(GourmetPaymentInformation gourmetPaymentInformation, SaleTime dateSaleTime)
    {
        if (mVBankEditor != null)
        {
            mVBankEditor.clear();

            TicketInformation ticketInformation = gourmetPaymentInformation.getTicketInformation();

            mVBankEditor.putString(KEY_PREFERENCE_VBANK_PLACE_TYPE, AnalyticsManager.Label.GOURMET);
            mVBankEditor.putString(KEY_PREFERENCE_VBANK_USER_INDEX, gourmetPaymentInformation.getCustomer().getUserIdx());
            mVBankEditor.putString(KEY_PREFERENCE_VBANK_PLACE_NAME, ticketInformation.placeName);
            mVBankEditor.putString(KEY_PREFERENCE_VBANK_PRICE, Integer.toString(ticketInformation.discountPrice));
            mVBankEditor.putString(KEY_PREFERENCE_VBANK_QUANTITY, Integer.toString(gourmetPaymentInformation.ticketCount));
            mVBankEditor.putString(KEY_PREFERENCE_VBANK_TOTAL_PRICE, Integer.toString(ticketInformation.discountPrice * gourmetPaymentInformation.ticketCount));
            mVBankEditor.putString(KEY_PREFERENCE_VBANK_PLACE_INDEX, Integer.toString(gourmetPaymentInformation.placeIndex));
            mVBankEditor.putString(KEY_PREFERENCE_VBANK_TICKET_NAME, ticketInformation.placeName);
            mVBankEditor.putString(KEY_PREFERENCE_VBANK_TICKET_INDEX, Integer.toString(ticketInformation.index));
            mVBankEditor.putString(KEY_PREFERENCE_VBANK_DATE, dateSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"));
            mVBankEditor.putString(KEY_PREFERENCE_VBANK_PAYMENT_PRICE, Integer.toString(ticketInformation.discountPrice * gourmetPaymentInformation.ticketCount));
            mVBankEditor.putString(KEY_PREFERENCE_VBANK_USED_BONUS, "0");
            mVBankEditor.putString(KEY_PREFERENCE_VBANK_PAYMENT_TYPE, gourmetPaymentInformation.paymentType.name());
            mVBankEditor.putString(KEY_PREFERENCE_VBANK_CATEGORY, gourmetPaymentInformation.category);
            mVBankEditor.putString(KEY_PREFERENCE_VBANK_DBENEFIT, gourmetPaymentInformation.isDBenefit ? "yes" : "no");

            Calendar calendarTime = DailyCalendar.getInstance();
            calendarTime.setTimeZone(TimeZone.getTimeZone("GMT"));

            SimpleDateFormat formatDay = new SimpleDateFormat("HH:mm", Locale.KOREA);
            formatDay.setTimeZone(TimeZone.getTimeZone("GMT"));

            mVBankEditor.putString(KEY_PREFERENCE_VBANK_RESERVATION_TIME, formatDay.format(gourmetPaymentInformation.ticketTime));
            mVBankEditor.apply();
        }
    }

    public Map<String, String> getVirtuaAccountGourmetInformation()
    {
        Map<String, String> params = new HashMap<>();

        params.put(AnalyticsManager.KeyType.USER_INDEX, getValue(mVBankPreferences, KEY_PREFERENCE_VBANK_USER_INDEX, null));
        params.put(AnalyticsManager.KeyType.NAME, getValue(mVBankPreferences, KEY_PREFERENCE_VBANK_PLACE_NAME, null));
        params.put(AnalyticsManager.KeyType.PRICE, getValue(mVBankPreferences, KEY_PREFERENCE_VBANK_PRICE, null));
        params.put(AnalyticsManager.KeyType.QUANTITY, getValue(mVBankPreferences, KEY_PREFERENCE_VBANK_QUANTITY, null));
        params.put(AnalyticsManager.KeyType.TOTAL_PRICE, getValue(mVBankPreferences, KEY_PREFERENCE_VBANK_TOTAL_PRICE, null));
        params.put(AnalyticsManager.KeyType.PLACE_INDEX, getValue(mVBankPreferences, KEY_PREFERENCE_VBANK_PLACE_INDEX, null));
        params.put(AnalyticsManager.KeyType.TICKET_NAME, getValue(mVBankPreferences, KEY_PREFERENCE_VBANK_TICKET_NAME, null));
        params.put(AnalyticsManager.KeyType.TICKET_INDEX, getValue(mVBankPreferences, KEY_PREFERENCE_VBANK_TICKET_INDEX, null));
        params.put(AnalyticsManager.KeyType.DATE, getValue(mVBankPreferences, KEY_PREFERENCE_VBANK_DATE, null));
        params.put(AnalyticsManager.KeyType.USED_BOUNS, getValue(mVBankPreferences, KEY_PREFERENCE_VBANK_USED_BONUS, null));
        params.put(AnalyticsManager.KeyType.PAYMENT_PRICE, getValue(mVBankPreferences, KEY_PREFERENCE_VBANK_PAYMENT_PRICE, null));
        params.put(AnalyticsManager.KeyType.PAYMENT_TYPE, getValue(mVBankPreferences, KEY_PREFERENCE_VBANK_PAYMENT_TYPE, null));
        params.put(AnalyticsManager.KeyType.RESERVATION_TIME, getValue(mVBankPreferences, KEY_PREFERENCE_VBANK_RESERVATION_TIME, null));
        params.put(AnalyticsManager.KeyType.CATEGORY, getValue(mVBankPreferences, KEY_PREFERENCE_VBANK_CATEGORY, null));
        params.put(AnalyticsManager.KeyType.DBENEFIT, getValue(mVBankPreferences, KEY_PREFERENCE_VBANK_DBENEFIT, null));

        return params;
    }

    public void removeVirtualAccountInformation()
    {
        if (mVBankEditor != null)
        {
            mVBankEditor.clear();
            mVBankEditor.apply();
        }
    }

    public int getVirtualAccountReadyFlag()
    {
        return getValue(mVBankPreferences, KEY_PREFERENCE_ACCOUNT_READY_FLAG, -1);
    }

    public void setVirtualAccountReadyFlag(int value)
    {
        setValue(mVBankEditor, KEY_PREFERENCE_ACCOUNT_READY_FLAG, value);
    }
}
