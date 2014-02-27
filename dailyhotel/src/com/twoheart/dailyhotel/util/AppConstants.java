package com.twoheart.dailyhotel.util;

public class AppConstants {
	
	 public static final boolean DEBUG = false;
	
	// url list19
	public static String REST_URL = "http://dailyhotel.kr/goodnight/";
	public static String LOGIN = "user/login/mobile";
	public static String LOGOUT = "user/logout";
	public static String LOCATION_LIST = "site/get";
	public static String HOTEL = "hotel/";
	public static String VERSION = "common/ver_dual";
	public static String HELP = "board/json/faq";
	public static String NOTICE = "board/json/notice";
	public static String LEGAL = "common/regal";
	public static String SIGNUP = "user/join";
	public static String PAYMENT = "reserv/session/req/";
	public static String PAYMENT_DISCOUNT = "reserv/session/bonus/";
	public static String RESERVE = "reserv/mine";
	public static String DEVICE = "user/device";
	public static String RECOMMEND = "user/";
	public static String SAVED_MONEY = "reserv/bonus";
	public static String DETAIL = "hotel/detail/";
	public static String MAP = "hotel/all";
	public static String USER_ALIVE = "user/alive";
	public static String CHECKIN ="reserv/checkinout/";
	public static String USERINFO = "user/session/myinfo";
	public static String BONUS_ALL = "user/session/bonus/all";
	public static String BONUS_VAILD = "user/session/bonus/vaild";
	public static String TIME = "time";
	public static String SALE_TIME = "common/sale_time";
	
	// Preference
	public static String SHARED_PREFERENCES_NAME = "GOOD_NIGHT";
	
	public static String PREFERENCE_RESENT_CNT = "RESENT_CNT";
	
	// user info
	public static String PREFERENCE_IS_LOGIN = "IS_LOGIN";
	public static String PREFERENCE_AUTO_LOGIN = "AUTO_LOGIN";
	public static String PREFERENCE_USER_ID = "USER_ID";
	public static String PREFERENCE_USER_PWD = "USER_PWD";
	
	// hotel info
	public static String PREFERENCE_HOTEL_NAME = "HOTEL_NAME";
	public static String PREFERENCE_HOTEL_IDX = "HOTEL_IDX";
	public static String PREFERENCE_HOTEL_YEAR = "HOTEL_YEAR";
	public static String PREFERENCE_HOTEL_DAY = "HOTEL_DAY";
	public static String PREFERENCE_HOTEL_MONTH = "HOTEL_MONTH";
	public static String PREFERENCE_HOTEL_LNG = "HOTEL_LNG";
	public static String PREFERENCE_HOTEL_LAT = "HOTEL_LAT";
	
	
	public static String PREFERENCE_BOOKING_KCPNO = "BOOKING_KCPNO";
	public static String PREFERENCE_BOOKING_RSV_IDX = "BOOKING_RSV_IDX";
	
	// version
	public static String PREFERENCE_CURRENT_VERSION_NAME = "CURRENT_VERSION_NAME";
	public static String PREFERENCE_CURRENT_VERSION_CODE = "CURRENT_VERSION_CODE";
	public static String PREFERENCE_MAX_VERSION_NAME = "MAX_VERSION_NAME";
	public static String PREFERENCE_MAX_VERSION_CODE = "MAX_VERSION_CODE";
	public static String PREFERENCE_MIN_VERSION_NAME = "MIN_VERSION_NAME";
	public static String PREFERENCE_MIN_VERSION_CODE = "MIN_VERSION_CODE";
	
	// GCM
	public static String PREFERENCE_GCM = "GCM";
	
	// region
	public static String PREFERENCE_REGION_SELECT = "REGION_SELECT";
	public static String PREFERENCE_REGION_DEFALUT = "REGION_DEFALUT";
	public static String PREFERENCE_REGION_INDEX = "REGION_INDEX";
	
	// event
	public static String PREFERENCE_NEW_EVENT = "NEW_EVENT";
	
	// region list click
	public static Boolean clickState[];
	
	// selected menu
	public static String PREFERENCE_SELECTED_MENU = "SELECTED_MENU";
}
