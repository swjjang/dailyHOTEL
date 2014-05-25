/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * Constants (어플리케이션 전역 상수)
 * 
 * 어플리케이션에서 사용되는 전역 상수들을 정리해놓은 인터페이스이다. 어플리
 * 케이션에서 사용되는 전역 상수들은 거의 고정된 값들이며 여러 부분에서 일
 * 률적으로 사용되므로 상수로서 선언됐다. 이 인터페이스는 각 클래스에서 상속
 * 받아서 바로 사용될 수 있다.
 *
 * @since 2014-02-24
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 */
package com.twoheart.dailyhotel.util;

import com.twoheart.dailyhotel.BuildConfig;

public interface Constants {

	// 디버그 빌드 여부
	public static final boolean DEBUG = BuildConfig.DEBUG;
	
	// 플레이 스토어용 릴리즈 여부
	public static final boolean IS_GOOGLE_RELEASE = true;
	
	// 웹서버 호스트 
	public static final String URL_DAILYHOTEL_SERVER = "http://dailyhotel.kr/goodnight/";				// 서비스 서버
//	public static final String URL_DAILYHOTEL_SERVER = "http://dailyhotel.cafe24.com/goodnight/";		// 개발 서버
	 
	// 회사 대표번호
	public static final String PHONE_NUMBER_DAILYHOTEL = "1800-9120";
	
	// 호텔 평가를 표시할 최대 날짜
	public static final int DAYS_DISPLAY_RATING_HOTEL_DIALOG = 7;

	// DailyHOTEL User Controller WebAPI URL
	public static final String URL_WEBAPI_USER = "user/";
	public static final String URL_WEBAPI_USER_LOGIN = "user/login/mobile";
	public static final String URL_WEBAPI_USER_LOGOUT = "user/logout/mobile";
	public static final String URL_WEBAPI_USER_INFO = "user/session/myinfo";
	public static final String URL_WEBAPI_USER_BONUS_ALL = "user/session/bonus/all";
	public static final String URL_WEBAPI_USER_BONUS_VAILD = "user/session/bonus/vaild";
	public static final String URL_WEBAPI_USER_LOGIN_FACEBOOK = "user/login/sns/facebook";
	public static final String URL_WEBAPI_USER_SIGNUP = "user/join";
	public static final String URL_WEBAPI_USER_ALIVE = "user/alive";
	public static final String URL_WEBAPI_USER_FORGOTPWD = "user/sendpw/";
	public static final String URL_WEBAPI_USER_FINDRND = "user/findrnd/";
	public static final String URL_WEBAPI_USER_UPDATE = "user/update";
	
	// DailyHOTEL Reservation Controller WebAPI URL
	public static final String URL_WEBAPI_RESERVE_PAYMENT = "reserv/session/req/";
	public static final String URL_WEBAPI_RESERVE_PAYMENT_DISCOUNT = "reserv/session/bonus/";
	public static final String URL_WEBAPI_RESERVE_MINE = "reserv/mine";
	public static final String URL_WEBAPI_RESERVE_SAVED_MONEY = "reserv/bonus";
	public static final String URL_WEBAPI_RESERVE_CHECKIN = "reserv/checkinout/";
	public static final String URL_WEBAPI_RESERVE_REVIEW = "reserv/review/";
	
	// DailyHOTEL App Management Controller WebAPI URL
	public static final String URL_WEBAPI_APP_VERSION = "common/ver_dual";
	public static final String URL_WEBAPI_APP_LEGAL = "common/regal";
	public static final String URL_WEBAPI_APP_TIME = "time";
	public static final String URL_WEBAPI_APP_SALE_TIME = "common/sale_time";
	
	// DailyHOTEL Hotel Controller WebAPI URL
	public static final String URL_WEBAPI_HOTEL = "hotel/";
	public static final String URL_WEBAPI_HOTEL_DETAIL = "hotel/detail/";
	public static final String URL_WEBAPI_HOTEL_MAP = "hotel/all";
	
	// DailyHOTEL Board Controller WebAPI URL
	public static final String URL_WEBAPI_BOARD_FAQ = "board/json/faq";
	public static final String URL_WEBAPI_BOARD_NOTICE = "board/json/notice";
	
	// DailyHOTEL Site Controller WebAPI URL
	public static final String URL_WEBAPI_SITE_LOCATION_LIST = "site/get";
	
	public static final String URL_STORE_GOOGLE_DAILYHOTEL = "market://details?id=com.twoheart.dailyhotel";
	public static final String URL_STORE_T_DAILYHOTEL = "http://tsto.re/0000412421";
	
	public static final String URL_WEB_PRIVACY = "http://policies.dailyhotel.co.kr/privacy/";
	public static final String URL_WEB_TERMS = "http://policies.dailyhotel.co.kr/terms/";
	public static final String URL_WEB_ABOUT = "http://policies.dailyhotel.co.kr/about/";
	
	// Preference
	public static final String NAME_DAILYHOTEL_SHARED_PREFERENCE = "GOOD_NIGHT";

	public static final String KEY_PREFERENCE_RESENT_CNT = "RESENT_CNT";

	// user info
	public static final String KEY_PREFERENCE_AUTO_LOGIN = "AUTO_LOGIN";
	public static final String KEY_PREFERENCE_USER_ID = "USER_ID";
	public static final String KEY_PREFERENCE_USER_PWD = "USER_PWD";
	public static final String KEY_PREFERENCE_USER_ACCESS_TOKEN = "USER_ACCESSTOKEN";

	// version
	public static final String KEY_PREFERENCE_CURRENT_VERSION_NAME = "CURRENT_VERSION_NAME";
	public static final String KEY_PREFERENCE_MIN_VERSION_NAME = "MIN_VERSION_NAME";
	public static final String KEY_PREFERENCE_MAX_VERSION_NAME = "MAX_VERSION_NAME";
	public static final String KEY_PREFERENCE_SKIP_MAX_VERSION = "SKIP_MAX_VERSION";
	
	// region
	public static final String KEY_PREFERENCE_REGION_SELECT = "REGION_SELECT";
	public static final String KEY_PREFERENCE_REGION_INDEX = "REGION_INDEX";
	
	public static final String KEY_PREFERENCE_SHOW_GUIDE = "SHOW_GUIDE";
	
	public static final String KEY_PREFERENCE_HOTEL_NAME = "HOTEL_NAME";
	public static final String KEY_PREFERENCE_HOTEL_SALE_IDX = "HOTEL_SALE_IDX";
	public static final String KEY_PREFERENCE_HOTEL_CHECKOUT = "HOTEL_CHECKOUT";
	public static final String VALUE_PREFERENCE_HOTEL_NAME_DEFAULT = "none";
	public static final int VALUE_PREFERENCE_HOTEL_SALE_IDX_DEFAULT = 1;
	public static final String VALUE_PREFERENCE_HOTEL_CHECKOUT_DEFAULT = "14-04-30-20";
	public static final String KEY_PREFERENCE_USER_IDX = "USER_IDX";

	// Android 컴포넌트 간에 데이터를 주고받을 때 사용되는 인텐트 이름(키)을 정의한 상수이다.
	public static final String NAME_INTENT_EXTRA_DATA_HOTEL = "hotel";
	public static final String NAME_INTENT_EXTRA_DATA_HOTELDETAIL = "hoteldetail";
	public static final String NAME_INTENT_EXTRA_DATA_SALETIME = "saletime";
	public static final String NAME_INTENT_EXTRA_DATA_BOOKING = "booking";
	public static final String NAME_INTENT_EXTRA_DATA_PAY = "pay";
	public static final String NAME_INTENT_EXTRA_DATA_SELECTED_IMAGE_URL = "sel_image_url";
	
	// Android Activity의 Request Code들이다.
	public static final int CODE_REQUEST_ACTIVITY_HOTELTAB = 1;
	public static final int CODE_REQUEST_FRAGMENT_BOOKINGLIST = 2;
	public static final int CODE_REQUEST_ACTIVITY_LOGIN = 3;
	public static final int CODE_REQUEST_ACTIVITY_PAYMENT = 4;
	public static final int CODE_REQUEST_ACTIVITY_SPLASH = 5;
	public static final int CODE_REQEUST_ACTIVITY_SIGNUP = 6;
	public static final int CODE_REQUEST_ACTIVITY_BOOKING = 7;
	
	// Android Activity의 Result Code들이다.
	public static final int CODE_RESULT_ACTIVITY_PAYMENT_FAIL = 100;
	public static final int CODE_RESULT_ACTIVITY_PAYMENT_SUCCESS = 101;
	public static final int CODE_RESULT_ACTIVITY_PAYMENT_INVALID_SESSION = 102;
	public static final int CODE_RESULT_ACTIVITY_PAYMENT_SOLD_OUT = 103;
	public static final int CODE_RESULT_ACTIVITY_PAYMENT_COMPLETE = 104;
	public static final int CODE_RESULT_ACTIVITY_PAYMENT_INVALID_DATE = 105;
	public static final int CODE_RESULT_ACTIVITY_PAYMENT_NOT_AVAILABLE = 106;
	public static final int CODE_RESULT_ACTIVITY_PAYMENT_NETWORK_ERROR = 107;
	public static final int CODE_RESULT_ACTIVITY_SPLASH_NEW_EVENT = 108;
	
}
