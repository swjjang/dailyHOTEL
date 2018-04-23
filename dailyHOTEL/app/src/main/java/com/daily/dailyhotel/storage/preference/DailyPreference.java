package com.daily.dailyhotel.storage.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.crashlytics.android.Crashlytics;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.PreferenceRegion;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.Setting;
import com.twoheart.dailyhotel.model.DailyCategoryType;
import com.twoheart.dailyhotel.util.Crypto;

import org.json.JSONObject;

/**
 */
public class DailyPreference
{
    public static final String DAILYHOTEL_SHARED_PREFERENCE_V1 = "dailyHOTEL_v1"; // 새로 만든

    /////////////////////////////////////////////////////////////////////////////////////////
    // "dailyHOTEL_v1" Preference
    /////////////////////////////////////////////////////////////////////////////////////////

    //    private static final String KEY_OPENING_ALARM = "1"; // 알람
    //    private static final String KEY_LAST_MENU = "3"; // 마지막 메뉴 리스트가 무엇인지
    //    private static final String KEY_SHOW_GUIDE = "4"; // 가이드를 봤는지 여부
    //    private static final String KEY_ALLOW_PUSH = "5";
    //    private static final String KEY_ALLOW_BENEFIT_ALARM = "6";

    //    private static final String KEY_COLLAPSEKEY = "10"; // 푸시 중복 되지 않도록
    //    private static final String KEY_SOCIAL_SIGNUP = "11"; // 회원가입시 소셜 가입자인 경우

    // version - 2.0.4 로 강업 이후 삭제 필요 부분
    //    @Deprecated
    //    private static final String KEY_HOTEL_REGION_ISOVERSEA = "12"; // 현재 선택된 지역이 국내/해외
    // version - 2.0.4 로 강업 이후 삭제 필요 부분
    //    @Deprecated
    //    private static final String KEY_GOURMET_REGION_ISOVERSEA = "13"; // 현재 선택된 지역이 국내/해외

    private static final String KEY_NEW_EVENT = "14"; // 현재 이벤트 유무
    private static final String KEY_NEW_COUPON = "15"; // 현재 새로운 쿠폰 유무(로그인 사용자만 보임)
    private static final String KEY_NEW_NOTICE = "16"; // 현재 새로운 쿠폰 유무(로그인 사용자만 보임)

    private static final String KEY_AGREE_TERMS_OF_LOCATION = "21"; // 위치 약관 동의 여부
    //    private static final String KEY_INFORMATION_CS_OPERATION_TIMEMESSAGE = "22"; // 운영시간 문구
    private static final String KEY_APP_VERSION = "23";

    private static final String KEY_SHOW_BENEFIT_ALARM = "24";
    private static final String KEY_BENEFIT_ALARM_MESSAGE = "25";
    private static final String KEY_FIRST_BUYER = "26";
    private static final String KEY_FIRST_APP_VERSION = "27";

    //    private static final String KEY_IS_VIEW_RECENT_PLACE_TOOLTIP = "28"; // 삭제! - 30 으로 대체 됨
    private static final String KEY_INFORMATION_CS_OPERATION_TIME = "29"; // 운영시간 H,H (앞은 시작 뒤는 끝나는 시간)
    //    private static final String KEY_IS_VIEW_WISHLIST_TOOLTIP = "30";
    //    private static final String KEY_IS_VIEW_SEARCH_TOOLTIP = "31";

    private static final String KEY_IS_REQUEST_REVIEW = "32";

    private static final String KEY_IS_VIEW_WISH_TOOLTIP = "33";

    private static final String KEY_IS_VIEW_STAY_CATEGORY_TOOLTIP = "34";

    private static final String KEY_IS_VIEW_STAY_SEARCH_OB_TOOLTIP = "35";

    //    private static final String KEY_STAY_LAST_VIEW_DATE = "108";
    //    private static final String KEY_GOURMET_LAST_VIEW_DATE = "109";

    private static final String KEY_HOTEL_SEARCH_RECENTLY = "200";
    private static final String KEY_GOURMET_SEARCH_RECENTLY = "201";

    private static final String KEY_NOTICE_NEW_LIST = "202";
    private static final String KEY_NOTICE_NEW_REMOVE_LIST = "203";

    private static final String KEY_SELECTED_SIMPLE_CARD = "204"; // 마지막으로 간편결제된 카드

    // Recently 의 경우 DailyDb 사용으로 변경 됨 또한 해외 호텔 적용으로 기존 Preference 삭제
    //    private static final String KEY_STAY_RECENT_PLACES = "210";
    //    private static final String KEY_GOURMET_RECENT_PLACES = "211";
    //    private static final String KEY_ALL_RECENT_PLACES = "212";

    private static final String KEY_TRUE_VR_SUPPORT = "213";
    private static final String KEY_TRUE_VR_CHECK_DATA_GUIDE = "214";
    private static final String KEY_PREVIEW_GUIDE = "215";
    private static final String KEY_APP_PERMISSIONS_GUIDE = "216";

    //    @Deprecated
    //    private static final String KEY_STAY_OUTBOUND_SEARCH_CALENDAR = "217"; // 최초에 1회 캘린더 띄우기
    private static final String KEY_GOURMET_PRODUCT_DETAIL_GUIDE = "220"; // 최초에 1회 고메 상세 가이드 띄우기

    //    private static final String KEY_STAY_OUTBOUND_SEARCH_SUGGEST = "221"; // 아웃바운드 검색 추천
    private static final String KEY_STAY_OUTBOUND_SEARCH_CHECK_IN_DATE = "222"; // 아웃바운드 검색 Check In Date
    private static final String KEY_STAY_OUTBOUND_SEARCH_CHECK_OUT_DATE = "223"; // 아웃바운드 검색 Check Out Date
    private static final String KEY_STAY_OUTBOUND_SEARCH_PEOPLE = "224"; // 아웃바운드 검색 숙박인원

    private static final String KEY_STAY_PRODUCT_DETAIL_GUIDE = "225"; // 최초에 1회 스테이 객실 상세 가이드 띄우기

    // ----> DailyPreference 로 이동
    //    private static final String KEY_AUTHORIZATION = "1000";
    // <-----

    private static final String KEY_VERIFICATION = "1001";
    private static final String KEY_BASE_URL = "1005"; // 앱의 기본 URL
    private static final String KEY_BASE_OUTBOUND_URL = "1006"; // 앱의 기본 OUTBOUND URL

    //    private static final String KEY_SETTING_MIGRATION_FLAG = "1003"; // 2.0.0 이후 사용안함
    private static final String KEY_STAY_CATEGORY_CODE = "1010";
    private static final String KEY_STAY_CATEGORY_NAME = "1011";

    private static final String KEY_CALENDAR_HOLIDAYS = "1012";
    private static final String KEY_CHECK_CALENDAR_HOLIDAYS_STARTDAY = "1013";
    private static final String KEY_HAPPY_TALK_CATEGORY = "1014"; // 해피톡 상담유형 저장하기

    private static final String KEY_BACKGROUND_APP_TIME = "2000";

    //    private static final String KEY_HOME_SHORT_CUT_STAY_OUTBOUND_NEW = "10001";
    private static final String KEY_HOME_SHORT_CUT_STAY_OUTBOUND_NEW_DATE = "10002";

    /////////////////////////////////////////////////////////////////////////////////////////
    // New Key old --> v1
    /////////////////////////////////////////////////////////////////////////////////////////

    // Setting
    //    private static final String KEY_SETTING_GCM_ID = "1002";
    private static final String KEY_SETTING_VERSION_SKIP_MAX_VERSION = "1004";

    // Setting - Region - Old 2017.04.07
    // version - 2.0.4 로 강업 이후 삭제 필요 부분
    //    @Deprecated
    //    private static final String KEY_SETTING_REGION_STAY_SELECT = "1110";
    //        private static final String KEY_SETTING_REGION_STAY_SETTING = "1111"; // home 이후 사용안하는 부분
    // version - 2.0.4 로 강업 이후 삭제 필요 부분
    //    @Deprecated
    //    private static final String KEY_SETTING_REGION_STAY_SETTINGN_PROVINCE_STAY_SELECT = "1112"; // adjust
    // version - 2.0.4 로 강업 이후 삭제 필요 부분
    //    @Deprecated
    //    private static final String KEY_SETTING_REGION_FNB_SELECT = "1120";
    //        private static final String KEY_SETTING_REGION_FNB_SETTING = "1121"; // home 이후 사용안하는 부분
    // version - 2.0.4 로 강업 이후 삭제 필요 부분
    //    @Deprecated
    //    private static final String KEY_SETTING_REGION_PROVINCE_FNB_SELECT = "1122"; // adjust
    // Setting - Region New 2017.04.07 - 스테이 호텔 구분 안하고 전체를 다 개별 카테고리로 봄
    private static final String KEY_SETTING_REGION_STAY_ALL = "1130";
    private static final String KEY_SETTING_REGION_GOURMET_ALL = "1131";
    private static final String KEY_SETTING_REGION_STAY_HOTEL = "1132";
    private static final String KEY_SETTING_REGION_STAY_BOUTIQUE = "1133";
    private static final String KEY_SETTING_REGION_STAY_PENSION = "1134";
    private static final String KEY_SETTING_REGION_STAY_RESORT = "1135";

    // Setting - Home
    private static final String KEY_SETTING_HOME_MESSAGE_AREA_ENABLED = "1201";

    // User
    // -----> DailyUserPreference 로 이동
    //    private static final String KEY_USER_EMAIL = "2001";
    //    private static final String KEY_USER_TYPE = "2002";
    //    private static final String KEY_USER_NAME = "2003";
    //    private static final String KEY_USER_RECOMMENDER = "2004";
    //    private static final String KEY_USER_BENEFIT_ALARM = "2005";
    //    private static final String KEY_USER_IS_EXCEED_BONUS = "2006";
    //    private static final String KEY_USER_BIRTHDAY = "2007";
    //
    //
    //    private static final String KEY_PAYMENT_OVERSEAS_NAME = "4000";
    //    private static final String KEY_PAYMENT_OVERSEAS_PHONE = "4001";
    //    private static final String KEY_PAYMENT_OVERSEAS_EMAIL = "4002";
    // <------

    // Payment
    //    private static final String KEY_PAYMENT_INFORMATION = "4003";


    // payment - Virtual Account
    //    private static final String KEY_PAYMENT_ACCOUNT_READY_FLAG = "4100";

    // Event
    private static final String KEY_EVENT_LATEST_EVENT_TIME = "6100";
    private static final String KEY_EVENT_LATEST_COUPON_TIME = "6101";
    private static final String KEY_EVENT_LATEST_NOTICE_TIME = "6102";
    private static final String KEY_EVENT_VIEWED_EVENT_TIME = "6200";
    private static final String KEY_EVENT_VIEWED_COUPON_TIME = "6201";
    private static final String KEY_EVENT_VIEWED_NOTICE_TIME = "6202";
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    private static DailyPreference mInstance;
    private SharedPreferences mPreferences;
    private Editor mEditor;

    private DailyPreference(Context context)
    {
        mPreferences = context.getSharedPreferences(DAILYHOTEL_SHARED_PREFERENCE_V1, Context.MODE_PRIVATE);
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

    /**
     * 앱 삭제시에도 해당 데이터는 남기도록 한다.
     */
    public void clear()
    {
        // 해택 알림 내용은 유지 하도록 한다. 단 로그인시에는 서버에서 다시 가져와서 세팅한다.
        boolean isShowBenefitAlarm = isShowBenefitAlarm();
        boolean isShowTrueVRTooltip = isWishTooltip();
        int supportTrueVR = getTrueVRSupport();

        String baseUrl = getBaseUrl();
        String baseOutBoundUrl = getBaseOutBoundUrl();
        boolean isHomeTextMessageAreaEnable = isHomeTextMessageAreaEnabled();
        int countPreviewGuide = getCountPreviewGuide();
        boolean isShowAppPermissionsGuide = isShowAppPermissionsGuide();
        boolean isProductDetailGuide = getGourmetProductDetailGuide();
        String stayOutboundNewDate = getHomeShortCutStayOutboundNewDate();

        String stayRecentSearches = getHotelRecentSearches();
        String gourmetRecentSearches = getGourmetRecentSearches();

        String stayOutboundSearchCheckInDate = getStayOutboundSearchCheckInDate();
        String stayOutboundSearchCheckOutDate = getStayOutboundSearchCheckOutDate();
        JSONObject stayOutboundSearchPeople = getStayOutboundSearchPeople();
        boolean isStaySearchObTooltip = isStaySearchObTooltip();

        if (mEditor != null)
        {
            mEditor.clear();
            mEditor.apply();
        }

        setShowBenefitAlarm(isShowBenefitAlarm);
        setWishTooltip(isShowTrueVRTooltip);
        setTrueVRSupport(supportTrueVR);
        setBaseUrl(baseUrl);
        setBaseOutBoundUrl(baseOutBoundUrl);
        setHomeTextMessageAreaEnabled(isHomeTextMessageAreaEnable);
        setCountPreviewGuide(countPreviewGuide);
        setShowAppPermissionsGuide(isShowAppPermissionsGuide);
        setGourmetProductDetailGuide(isProductDetailGuide);
        setHomeShortCutStayOutboundNewDate(stayOutboundNewDate);
        setHotelRecentSearches(stayRecentSearches);
        setGourmetRecentSearches(gourmetRecentSearches);
        setStayOutboundSearchCheckInDate(stayOutboundSearchCheckInDate);
        setStayOutboundSearchCheckOutDate(stayOutboundSearchCheckOutDate);
        setStayOutboundSearchPeople(stayOutboundSearchPeople == null ? null : stayOutboundSearchPeople.toString());
        setStaySearchObTooltip(isStaySearchObTooltip);

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

    public boolean isVerification()
    {
        return getValue(mPreferences, KEY_VERIFICATION, false);
    }

    public void setVerification(boolean value)
    {
        setValue(mEditor, KEY_VERIFICATION, value);
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

    public String getOperationTime()
    {
        return getValue(mPreferences, KEY_INFORMATION_CS_OPERATION_TIME, "9,3");
    }

    public void setOperationTime(String text)
    {
        setValue(mEditor, KEY_INFORMATION_CS_OPERATION_TIME, text);
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

    //    public void setViewSearchTooltip(boolean value)
    //    {
    //        setValue(mEditor, KEY_IS_VIEW_SEARCH_TOOLTIP, value);
    //    }
    //
    //    public boolean isViewSearchTooltip()
    //    {
    //        return getValue(mPreferences, KEY_IS_VIEW_SEARCH_TOOLTIP, false);
    //    }

    public void setWishTooltip(boolean value)
    {
        setValue(mEditor, KEY_IS_VIEW_WISH_TOOLTIP, value);
    }

    public boolean isWishTooltip()
    {
        return getValue(mPreferences, KEY_IS_VIEW_WISH_TOOLTIP, true);
    }

    public void setStayCategoryListTooltip(boolean value)
    {
        setValue(mEditor, KEY_IS_VIEW_STAY_CATEGORY_TOOLTIP, value);
    }

    public boolean isStayCategoryListTooltip()
    {
        return getValue(mPreferences, KEY_IS_VIEW_STAY_CATEGORY_TOOLTIP, false);
    }

    public void setStaySearchObTooltip(boolean value)
    {
        setValue(mEditor, KEY_IS_VIEW_STAY_SEARCH_OB_TOOLTIP, value);
    }

    public boolean isStaySearchObTooltip()
    {
        return getValue(mPreferences, KEY_IS_VIEW_STAY_SEARCH_OB_TOOLTIP, true);
    }

    public void setIsRequestReview(boolean value)
    {
        setValue(mEditor, KEY_IS_REQUEST_REVIEW, value);
    }

    public boolean isRequestReview()
    {
        return getValue(mPreferences, KEY_IS_REQUEST_REVIEW, false);
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

    public String getBaseUrl()
    {
        return getValue(mPreferences, KEY_BASE_URL, Crypto.getUrlDecoderEx(Setting.getServerUrl()));
    }

    public void setBaseUrl(String value)
    {
        setValue(mEditor, KEY_BASE_URL, value);

        // 반영이 안되는 경우가 있어서 특별히 추가 하였습니다.
        mEditor.commit();
    }

    public String getBaseOutBoundUrl()
    {
        return getValue(mPreferences, KEY_BASE_OUTBOUND_URL, Crypto.getUrlDecoderEx(Setting.getOutboundServerUrl()));
    }

    public void setBaseOutBoundUrl(String value)
    {
        setValue(mEditor, KEY_BASE_OUTBOUND_URL, value);

        // 반영이 안되는 경우가 있어서 특별히 추가 하였습니다.
        mEditor.commit();
    }

    public void setCalendarHolidays(String value)
    {
        setValue(mEditor, KEY_CALENDAR_HOLIDAYS, value);
    }

    public String getCalendarHolidays()
    {
        return getValue(mPreferences, KEY_CALENDAR_HOLIDAYS, "");
    }

    public void setCheckCalendarHolidays(String value)
    {
        setValue(mEditor, KEY_CHECK_CALENDAR_HOLIDAYS_STARTDAY, value);
    }

    public String getCheckCalendarHolidays()
    {
        return getValue(mPreferences, KEY_CHECK_CALENDAR_HOLIDAYS_STARTDAY, null);
    }

    public void setHappyTalkCategory(String value)
    {
        setValue(mEditor, KEY_HAPPY_TALK_CATEGORY, value);
    }

    public String getHappyTalkCategory()
    {
        return getValue(mPreferences, KEY_HAPPY_TALK_CATEGORY, null);
    }

    public void setTrueVRSupport(int value)
    {
        setValue(mEditor, KEY_TRUE_VR_SUPPORT, value);
    }

    public int getTrueVRSupport()
    {
        return getValue(mPreferences, KEY_TRUE_VR_SUPPORT, 0);
    }

    public void setTrueVRCheckDataGuide(boolean value)
    {
        setValue(mEditor, KEY_TRUE_VR_CHECK_DATA_GUIDE, value);
    }

    public boolean isTrueVRCheckDataGuide()
    {
        return getValue(mPreferences, KEY_TRUE_VR_CHECK_DATA_GUIDE, false);
    }

    public void setCountPreviewGuide(int value)
    {
        setValue(mEditor, KEY_PREVIEW_GUIDE, value);
    }

    public int getCountPreviewGuide()
    {
        return getValue(mPreferences, KEY_PREVIEW_GUIDE, 0);
    }

    public void setShowAppPermissionsGuide(boolean value)
    {
        setValue(mEditor, KEY_APP_PERMISSIONS_GUIDE, value);
    }

    public boolean isShowAppPermissionsGuide()
    {
        return getValue(mPreferences, KEY_APP_PERMISSIONS_GUIDE, true);
    }

    //    public void setShowStayOutboundSearchCalendar(boolean value)
    //    {
    //        setValue(mEditor, KEY_STAY_OUTBOUND_SEARCH_CALENDAR, value);
    //    }
    //
    //    public boolean isShowStayOutboundSearchCalendar()
    //    {
    //        return getValue(mPreferences, KEY_STAY_OUTBOUND_SEARCH_CALENDAR, true);
    //    }

    public void setGourmetProductDetailGuide(boolean value)
    {
        setValue(mEditor, KEY_GOURMET_PRODUCT_DETAIL_GUIDE, value);
    }

    public boolean getGourmetProductDetailGuide()
    {
        return getValue(mPreferences, KEY_GOURMET_PRODUCT_DETAIL_GUIDE, true);
    }

    public void setStayProductDetailGuide(boolean value)
    {
        setValue(mEditor, KEY_STAY_PRODUCT_DETAIL_GUIDE, value);
    }

    public boolean getStayProductDetailGuide()
    {
        return getValue(mPreferences, KEY_STAY_PRODUCT_DETAIL_GUIDE, true);
    }

    public void setStayOutboundSearchCheckInDate(String checkInDate)
    {
        setValue(mEditor, KEY_STAY_OUTBOUND_SEARCH_CHECK_IN_DATE, checkInDate);
    }

    public String getStayOutboundSearchCheckInDate()
    {
        return getValue(mPreferences, KEY_STAY_OUTBOUND_SEARCH_CHECK_IN_DATE, null);
    }

    public void setStayOutboundSearchCheckOutDate(String checkOutDate)
    {
        setValue(mEditor, KEY_STAY_OUTBOUND_SEARCH_CHECK_OUT_DATE, checkOutDate);
    }

    public String getStayOutboundSearchCheckOutDate()
    {
        return getValue(mPreferences, KEY_STAY_OUTBOUND_SEARCH_CHECK_OUT_DATE, null);
    }

    public void setStayOutboundSearchPeople(String people)
    {
        setValue(mEditor, KEY_STAY_OUTBOUND_SEARCH_PEOPLE, people);
    }

    public JSONObject getStayOutboundSearchPeople()
    {
        String value = getValue(mPreferences, KEY_STAY_OUTBOUND_SEARCH_PEOPLE, null);
        if (DailyTextUtils.isTextEmpty(value) == true)
        {
            return null;
        }

        JSONObject jsonObject = null;
        try
        {
            jsonObject = new JSONObject(value);
        } catch (Exception e)
        {
            com.daily.base.util.ExLog.e(e.toString());
        }

        return jsonObject;
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    // new
    /////////////////////////////////////////////////////////////////////////////////////////

    public String getLatestEventTime()
    {
        return getValue(mPreferences, KEY_EVENT_LATEST_EVENT_TIME, null);
    }

    public void setLatestEventTime(String value)
    {
        setValue(mEditor, KEY_EVENT_LATEST_EVENT_TIME, value);
    }

    public String getLatestCouponTime()
    {
        return getValue(mPreferences, KEY_EVENT_LATEST_COUPON_TIME, null);
    }

    public void setLatestCouponTime(String value)
    {
        setValue(mEditor, KEY_EVENT_LATEST_COUPON_TIME, value);
    }

    public String getLatestNoticeTime()
    {
        return getValue(mPreferences, KEY_EVENT_LATEST_NOTICE_TIME, null);
    }

    public void setLatestNoticeTime(String value)
    {
        setValue(mEditor, KEY_EVENT_LATEST_NOTICE_TIME, value);
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

    public String getFavoriteCard()
    {
        String value = getValue(mPreferences, KEY_SELECTED_SIMPLE_CARD, null);

        if (DailyTextUtils.isTextEmpty(value) == false)
        {
            value = Crypto.urlDecrypt(value);
        }

        return value;
    }

    public void setFavoriteCard(String number, String billingKey)
    {
        if (DailyTextUtils.isTextEmpty(number, billingKey) == true)
        {
            setValue(mEditor, KEY_SELECTED_SIMPLE_CARD, null);
        } else
        {
            setValue(mEditor, KEY_SELECTED_SIMPLE_CARD, Crypto.urlEncrypt(number.replaceAll("\\*|-", "") + billingKey.substring(3, 7)));
        }
    }

    /**
     * @param value 보여주는 기간 동안에는 ISO-8601 타입으로 값을 넣어주지만, 2 주후에는 "NONE"으로 바꾼다.
     */
    public void setHomeShortCutStayOutboundNewDate(String value)
    {
        setValue(mEditor, KEY_HOME_SHORT_CUT_STAY_OUTBOUND_NEW_DATE, value);
    }

    public String getHomeShortCutStayOutboundNewDate()
    {
        return getValue(mPreferences, KEY_HOME_SHORT_CUT_STAY_OUTBOUND_NEW_DATE, null);
    }

    public PreferenceRegion getDailyRegion(DailyCategoryType type)
    {
        try
        {
            return new PreferenceRegion(new JSONObject(getValue(mPreferences, getDailyRegionKey(type), null)));
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        return null;
    }

    public void setDailyRegion(DailyCategoryType type, PreferenceRegion regionType)
    {
        setDailyRegion(type, regionType.toJSONObject());
    }

    private void setDailyRegion(DailyCategoryType type, JSONObject jsonObject)
    {
        String value;

        if (jsonObject == null)
        {
            value = "";
        } else
        {
            value = jsonObject.toString();
        }

        setValue(mEditor, getDailyRegionKey(type), value);
    }

    private String getDailyRegionKey(DailyCategoryType type)
    {
        switch (type)
        {
            case STAY_ALL:
                return KEY_SETTING_REGION_STAY_ALL;

            case GOURMET_ALL:
                return KEY_SETTING_REGION_GOURMET_ALL;

            case STAY_HOTEL:
                return KEY_SETTING_REGION_STAY_HOTEL;

            case STAY_BOUTIQUE:
                return KEY_SETTING_REGION_STAY_BOUTIQUE;

            case STAY_PENSION:
                return KEY_SETTING_REGION_STAY_PENSION;

            case STAY_RESORT:
                return KEY_SETTING_REGION_STAY_RESORT;

            default:
                return null;
        }
    }

    public String getSkipVersion()
    {
        return getValue(mPreferences, KEY_SETTING_VERSION_SKIP_MAX_VERSION, "0");
    }

    public void setSkipVersion(String value)
    {
        setValue(mEditor, KEY_SETTING_VERSION_SKIP_MAX_VERSION, value);
    }

    public boolean isHomeTextMessageAreaEnabled()
    {
        return getValue(mPreferences, KEY_SETTING_HOME_MESSAGE_AREA_ENABLED, true);
    }

    public void setHomeTextMessageAreaEnabled(boolean isEnabled)
    {
        setValue(mEditor, KEY_SETTING_HOME_MESSAGE_AREA_ENABLED, isEnabled);
    }
}
