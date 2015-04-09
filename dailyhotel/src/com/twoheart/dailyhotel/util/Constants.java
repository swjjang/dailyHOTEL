/**
\ * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
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

import com.google.analytics.tracking.android.Logger.LogLevel;
import com.twoheart.dailyhotel.BuildConfig;

public interface Constants
{

	// 디버그 빌드 여부 BuildConfig는 배포시에 자동으로 false가 된다고 한다. 테스트 해보고 싶음.
	public static final boolean DEBUG = BuildConfig.DEBUG;
	public static final boolean UNENCRYPTED_URL = false;

	// 스토어 선택.
	public enum Stores
	{
		PLAY_STORE("PlayStore"), T_STORE("Tstore"), N_STORE("Nstore");

		private String mName;

		private Stores(String name)
		{
			mName = name;
		}

		public String getName()
		{
			return mName;
		}
	};

	public static final Stores RELEASE_STORE = Stores.PLAY_STORE;

	// 항상 열리게 셋팅 여부
	public static final boolean ALWAYS_OPEN = false;

	public static final String GCM_PROJECT_NUMBER = "1025681158000";
	public static final String GCM_DEVICE_TYPE_ANDROID = "0"; // GCM 등록을 할 때 API의 deviceType, 0은 안드로이드 1은 아이폰.

	// 웹서버 호스트  
	//"http://restful.dailyhotel.kr/goodnight/"; //  서비스 서버 
//	public static final String URL_DAILYHOTEL_SERVER = UNENCRYPTED_URL ? "http://restful.dailyhotel.kr/goodnight/" : "MzkkNTQkNjEkNTYkNDck$RTA3Q0MwQTlGOTVCNUM4RkQ1OTM5MzMxRDVDNkUK0MTBBRjIUyMjU4OIEDE4NzQWwRDI4QTNGRUFERUNDMDMzMjk4QzIyODlFMDkwMjNDMDMyRUUxMjBDRUU1MTlBMDQ3MzRB$";
	//http://ec2restful.dailyhotel.kr    http://restful.dailyhotel.kr/goodnight/
	//    public static final String URL_DAILYHOTEL_SERVER = "http://192.168.0.39:8080/goodnight/"; //  서비스 서버

	//"http://ec2.global.dailyhotel.kr/goodnight/"; //  서비스 서버
	//    public static final String 5URL_DAILYHOTEL_SERVER = "MTExJDEwNCQyOCQ3NyQyMiQ=$MzEzODJBMDEwMjk4NjhBNENE2MjQ3ZMjE5NkRCM0Q2MzJGQ0UwODQ3NjUzOUQwRjY5NEI1Qzg2NzY4NNDVEOTQ1NEY5OERCQ0M0RUNEOEJBOMUZGNjdADMTEzNTlDRTNBNkQz$";

	//	public static final String URL_DAILYHOTEL_SERVER = "http://ec2.test.dailyhotel.kr/goodnight/";

	//"http://tcwas.dailyhotel.co.kr/goodnight/";
	public static final String URL_DAILYHOTEL_SERVER = UNENCRYPTED_URL ? "http://tcwas.dailyhotel.co.kr/goodnight/" : "NzYkNTkkMzIkMTI0JDg2JA==$OUQ3NTdBMUQ5RjFENEQyNTU3NUY5QjA4GMkM4QzRFRDNGQjQ2MTRDQTlEQzJKFNjRCRDQ5RUE5RUM4HRUYwNjAQ2ODk2RTg5RDQ3OThGRTVGODg4REMzRTUzRDRFMAkVBMzJB$";

	// 회사 대표번호
	public static final String PHONE_NUMBER_DAILYHOTEL = "1800-9120";

	// uiLock을 띄우고 API를 콜하였는데 제한 시간 안에 리턴을 받지 못한경우. error 발생.
	/**
	 * TODO :TEST JUDGE
	 */
	public static final int REQUEST_EXPIRE_JUDGE = 15000;

	// 구글플레이 서비스 상태 확인 타임아웃
	public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 15000;

	// Volley의 최대 retry 횟수,  여기서 0은 리퀘스트를 리트라이 하지 않음을 말함.
	public static final int REQUEST_MAX_RETRY = 0;

	// 호텔 평가를 표시할 최대 날짜
	public static final int DAYS_DISPLAY_RATING_HOTEL_DIALOG = 7;

	// DailyHOTEL User Controller WebAPI URL
	//"user";
	public static final String URL_WEBAPI_USER = UNENCRYPTED_URL ? "user" : "NSQyOSQ0JDEwJDIxJA==$NTk4LQYzkwKRDQzMEVFOUCI5MEQ0OTRGEMjdFRERGRjdEQkQ=$";

	//"user/login/mobile";
	public static final String URL_WEBAPI_USER_LOGIN = UNENCRYPTED_URL ? "user/login/mobile" : "NTMkMTUkMzUkODEkMzMk$MUVFQTg3NkUxRjVAEREU2QTMzRjUyNThELMDKIxQUZGMkUxMEJCMDBENFzA5N0RBRjQ3NTRGQTdCMjk0QzEA0MkU1RA==$";

	//"user/logout/mobile";
	public static final String URL_WEBAPI_USER_LOGOUT = UNENCRYPTED_URL ? "user/logout/mobile" : "MjgkNjIkMiQ4MSQzMyQ=$NjIU3RDBBQUEyRjIxMTZEQkFCQjY2NRkZCBRTZCM0RDOTM2M0EwNDhBNURBOTg1MQjYwODAzNkM4NjYwM0CRGRENEOQ==$";

	//"user/session/myinfo";
	public static final String URL_WEBAPI_USER_INFO = UNENCRYPTED_URL ? "user/session/myinfo" : "MjQkNjgkNjUkOTAkNCQ=$RDUxBMkY5MUI3MTU5OTY1RUUyGRDE1QjgzQjI0OEY0REY5Q0JFNzgxRDBFQjdEMURGCQ0MNzN0U0RjRGM0RFQjVCMA=D=$";

	//"user/session/bonus/all";
	public static final String URL_WEBAPI_USER_BONUS_ALL = UNENCRYPTED_URL ? "user/session/bonus/all" : "NjEkODIkNTQkNjYkNjQk$NkE0NzIwMzJGNUIxNEM1MTYzODAxNkFCMkEzMkY1RDMzQzRFMjNBQTDk0RDNGMCTIIxHNDA2MUI0QkNEMDYxOUDJBRQ==$";

	//"user/session/bonus/vaild";
	public static final String URL_WEBAPI_USER_BONUS_VAILD = UNENCRYPTED_URL ? "user/session/bonus/vaild" : "NjQkODQkMzckNzMkNDgk$NDQ0QUU4REU3NzZGNTVBMjRGNjU4MkQ1RkNCRFDA3QjFGMzgQ1RTE2QjYyMkZDMTBBCRDUxNjcL2NUU1ODE5OTcZyRQ==$";

	//"user/login/sns/facebook";
	public static final String URL_WEBAPI_USER_LOGIN_FACEBOOK = UNENCRYPTED_URL ? "user/login/sns/facebook" : "OSQ2JDQkNzckNjgk$NkJEROTXVFRKDcxODA4MzBBODZGMDI3NkQ5OTE5MjdDOTQzRUI0OTk1OERFNTVDNUY3RLkIwN0MyNzCgzRkM5Q0JCNQ==$";

	//"user/session/facebook/update";
	public static final String URL_WEBAPI_USER_UPDATE_FACEBOOK = UNENCRYPTED_URL ? "user/session/facebook/update" : "MzgkNDYkMTQkNTckODQk$NjAzNjU1REI1ODGBERTlEMjE5REI1QTY3RTU5MTUFEQjk2NHUUyOTUwNTMBBQ0UyMDBENkIzREMzNzI4ODEzCREY3RQ==$";

	//"user/join";
	public static final String URL_WEBAPI_USER_SIGNUP = UNENCRYPTED_URL ? "user/join" : "MjAkMTAkNSQzOSQyNCQ=$NzI0MQjdDMTFNENDgyMzE5YRNUMwRUMyQ0REQTYxBM0M1QTE=$";

	//"user/alive";
	public static final String URL_WEBAPI_USER_ALIVE = UNENCRYPTED_URL ? "user/alive" : "MzAkMTQkMzckNDMkMTck$QzNENDQ1NTk0MzEg1GOURFOUYxQkM4MEIZFRjVBEQTU3BQjE=$";

	//"user/sendpw";
	public static final String URL_WEBAPI_USER_FORGOTPWD = UNENCRYPTED_URL ? "user/sendpw" : "MTgkMzkkNSQyNCQxOSQ=$QkExMH0RCNEQwMDYyRTDZFCNDKE3Qzg1M0MyREZCQTHgyNTg=$";

	//"user/findrnd";
	public static final String URL_WEBAPI_USER_FINDRND = UNENCRYPTED_URL ? "user/findrnd" : "MTMkMTkkMTckMzIkMSQ=$QYzJDQzJGODQ4RHjg3LQjRM1NzY4MzlBOZDRFQTRFQkQ4OUU=$";

	//"user/update";
	public static final String URL_WEBAPI_USER_UPDATE = UNENCRYPTED_URL ? "user/update" : "MjQkMjkkMzAkMiQ4JA==$QTCNBRjRIFMUZGRUQwMEQzRDM2ZMjM2FCRTIzRjZDREZCRDA=$";

	// user/check/email_auth
	public static final String URL_WEBAPI_USER_CHECK_EMAIL = UNENCRYPTED_URL ? "user/check/email_auth" : "NCQ4MCQ0MyQyNiQ4OCQ=$MzY0XM0YwNTgwNzYwMjJBOEQyMQEIxMDM3MDQ1RjBGMjUZGQzJERjU5OTAwQzQ2OEM2REJGMzc3RTI3REIUwNEYzGMQ==$";

	// user/change_pw
	public static final String URL_WEBAPI_USER_CHANGE_PW = UNENCRYPTED_URL ? "user/change_pw" : "MjMkMzAkMyQxNCQzMiQ=$QzcB0MkU1RjExOATlDMjU5OTBKCMkU4NIK0IwMUVCNTZENjI=$";

	// DailyHOTEL Reservation Controller WebAPI URL
	//"reserv/session/req";
	public static final String URL_WEBAPI_RESERVE_PAYMENT = UNENCRYPTED_URL ? "reserv/session/req" : "MTkkMzgkMTYkMzUkNDAk$NjgwRkNCOUEwRUVBQNERADRDc5QjJGRDk1NMTdDRNATQ4MkYzRjRGQURGOTZDODE2NzU1Nzg5Qjc3RDhBQ0Q4RjY0Qw==$";

	//"reserv/session/bonus";
	public static final String URL_WEBAPI_RESERVE_PAYMENT_DISCOUNT = UNENCRYPTED_URL ? "reserv/session/bonus" : "MTIkNyQxNSQ3MyQxOCQ=$RjFBMTEFyQjExHOCDRUCMTg4MkQyREU0NkVDNDI5Q0NENzU5QUQxREVCRkJBQzVDNzJDNDRCQkCM2NEJCODJEMTVGRg==$";

	//"reserv/mine";
	public static final String URL_WEBAPI_RESERVE_MINE = UNENCRYPTED_URL ? "reserv/mine" : "MSQyNCQzMiQzJDE0JA==$RXTIcyQzM2OTY1DNDg2MDk5REQEzQTFGMjGAyREZCNzhGMDc=$";

	//"reserv/mine/detail";
	public static final String URL_WEBAPI_RESERVE_MINE_DETAIL = UNENCRYPTED_URL ? "reserv/mine/detail" : "NjQkNTIkNjAkNjEkODUk$NzdCMkYzMzAzRUIzRjg4MkM3ODBGRjIzQTkxNzYzMTFEMTZFQ0NDMN0RFMzgMR5M0FBHRjBCMjA5QjUxRkE1RUEI2Qg==$";

	//"reserv/bonus";
	public static final String URL_WEBAPI_RESERVE_SAVED_MONEY = UNENCRYPTED_URL ? "reserv/bonus" : "MjYkMzIkMTYkMjckMjMk$MkM1NTUyNzJERjg5IQzgxOTLg1RUDVY0MTcJ3OUMzQ0U3NjI=$";

	//"reserv/checkinout";
	public static final String URL_WEBAPI_RESERVE_CHECKIN = UNENCRYPTED_URL ? "reserv/checkinout" : "MTQkNDUkNTkkMzAkODQk$Mjg2NUExMDcyQjTY0MDAwQzlBQTY4RTDE4NzhENjVFQjYwHMzREODc5NEEyRRUQ4RjI1OTE5RDdEQzRDOENCPQjU3NA==$";

	//"reserv/review";
	public static final String URL_WEBAPI_RESERVE_REVIEW = UNENCRYPTED_URL ? "reserv/review" : "NDEkMTMkMiQxNSQ0MSQ=$RUTExODg5NTA5MEJTJEQkI2RTdDQUUwODFEMDgwNzIUyQCUU=$";

	// DailyHOTEL App Management Controller WebAPI URL
	//"common/ver_dual";
	public static final String URL_WEBAPI_APP_VERSION = UNENCRYPTED_URL ? "common/ver_dual" : "MjIkMzckNDMkMTgkMTYk$QjlDRjI3N0NBNUM1UNjMZBOTNBFMTZGNUY0RTdEXNTY1RUjA=$";

	//"common/regal";
	public static final String URL_WEBAPI_APP_LEGAL = UNENCRYPTED_URL ? "common/regal" : "MTgkMCQxMyQ4JDMwJA==$GMkEwQzYLwQzIxFNUEwMUNE0OTFFMEPQzN0UwQUE1Nzg3RDg=$";

	//"time";
	public static final String URL_WEBAPI_APP_TIME = UNENCRYPTED_URL ? "time" : "MzEkMzEkMTckMjMkMTEk$QzkxQzM3NTgP0MEVEQDTlDMUIQ2Mjc5MDFJEENjk1NTk3ODg=$";

	//"common/sale_time";
	public static final String URL_WEBAPI_APP_SALE_TIME = UNENCRYPTED_URL ? "common/sale_time" : "NjckMzAkNTUkOCQ4MiQ=$ODlFQTJDEMTVFNjkyQTM3MDlFQUZDNzQRDQzhDNjJFN0FGMTNFQ0QyM0FJFQjE0MDMxOEJECRDIwMzQxRUSU5MEYyNw==$";

	// DailyHOTEL Hotel Controller WebAPI URL
	//"hotel";
	public static final String URL_WEBAPI_HOTEL = UNENCRYPTED_URL ? "hotel" : "MzUkMjgkMzkkMTckMjMk$RkExNEREQjU3RjkyNAURDOETFCM0ZCCRTAyQTcEyNDUJGQjk=$";

	//"hotel/detail";
	public static final String URL_WEBAPI_HOTEL_DETAIL = UNENCRYPTED_URL ? "hotel/detail" : "MzkkMjgkMzMkNSQyNiQ=$MTRDOBEEyQzJENzhCNDY3OTY0OQDJFPMzFGHNjBBRjdFGNzE=$";

	//"hotel/all";
	public static final String URL_WEBAPI_HOTEL_MAP = UNENCRYPTED_URL ? "hotel/all" : "MzEkNCQ3JDYkMjUk$QTlDKNJUAJEOEUzNzJENjY5METM2MTgxMTUVzMkIyQkNEREY=$";

	// DailyHOTEL Board Controller WebAPI URL
	//"board/json/faq";
	public static final String URL_WEBAPI_BOARD_FAQ = UNENCRYPTED_URL ? "board/json/faq" : "MjgkMTkkMTckMjMkMzgk$RjdDNURGRTkwOEY4NXDUV5MNjQ3MEE0YMjZFOTVRDNTJGRkM=$";

	//"board/json/notice";
	public static final String URL_WEBAPI_BOARD_NOTICE = UNENCRYPTED_URL ? "board/json/notice" : "MjYkMzMkNzAkNzQkNyQ=$MzkwMDcUyNDcwMjI5NUY3MzdGQjMU3NDFEWQUVCQURCQUMwODY2NERDNjNFNEJDMzczOUM0JNDkS0QUI0Qjk2NDYxMQ==$";

	// DailyHOTEL Site Controller WebAPI URL
	//"site/get";
	public static final String URL_WEBAPI_SITE_LOCATION_LIST = UNENCRYPTED_URL ? "site/get" : "MCQ1JDQzJDI4JDI3JA==$SMENFBRDREQjY4Rjk3MzAyNjc0MK0HM0MzJEQ0UxMjU4NOzc=$";

	//"site/get/country";
	public static final String URL_WEBAPI_SITE_COUNTRY_LOCATION_LIST = UNENCRYPTED_URL ? "site/get/country" : "MjMkNjQkNjYkMTYkNzgk$QTE5MzIwNjU2MkIwIREUyODhODODI4Q0IyN0NFQjA4QjJCMDU1MDI2QUQ5RTU4QUMCyNQUExREM4NTMA5QUM0RDc2Mg==$";

	public static final String URL_STORE_GOOGLE_DAILYHOTEL = "market://details?id=com.twoheart.dailyhotel";
	public static final String URL_STORE_T_DAILYHOTEL = "http://tsto.re/0000412421";
	public static final String URL_STORE_N_DAILYHOTEL = "market://details?id=com.twoheart.dailyhotel";

	//"http://policies.dailyhotel.co.kr/privacy/";
	public static final String URL_WEB_PRIVACY = UNENCRYPTED_URL ? "http://policies.dailyhotel.co.kr/privacy/" : "NzgkNDYkNDkkMzckMTgk$QzkwQkRGODU1ODQ3OTHQ1RUE2NkRENzczRDVEMY0QyOTU2MDQAyHMDJBRDlCMTQzRjhEMzhCNTdGNTUxQjAEyQ0JCQzFDMzQ5RUIyQkJCOEQzQzlGMzhDMjQ1Qjg5MkZGRDBE$";

	//"http://policies.dailyhotel.co.kr/terms/";
	public static final String URL_WEB_TERMS = UNENCRYPTED_URL ? "http://policies.dailyhotel.co.kr/terms/" : "MTA3JDk5JDk5JDExNiQ4MCQ=$QjVFNzE2QUQ1QUU4MEEyMEU5MTlBN0NDOUI5MEVBNDIyNjkwNjFENDgxNDQyMkMxNTE5MEYzMjY1RURGYNjQ2NjNEMjQ5MkU2NjFQODQ0YxQkEM2MUFEMZzc5MjI2NjBGMzgy$";

	//"http://policies.dailyhotel.co.kr/about/";
	public static final String URL_WEB_ABOUT = UNENCRYPTED_URL ? "http://policies.dailyhotel.co.kr/about/" : "OTkkMjQkMjQkNjAkNjgk$Mjc3NTgwNDUzNTEwQzdERkM1WYQzMyQkIxNzhENTk0N0M3RjdFMzYyMDJBNDKc2MjM3NJkU1NzhBOERDMzUyRUZDNzZDODUxNDQ5QkMUzQzMwQkJERTZFNDY4NTU1RDJEODFD$";

	// Payment App GoogleStore URL
	public static final String URL_STORE_PAYMENT_ISP = "market://details?id=kvp.jjy.MispAndroid320";
	public static final String URL_STORE_PAYMENT_KFTC = "market://details?id=com.kftc.bankpay.android&hl=ko";
	public static final String URL_STORE_PAYMENT_MPOCKET = "market://details?id=kr.co.samsungcard.mpocket";

	// Gcm Server URL
	//"user/notification/register";
	public static final String URL_GCM_REGISTER = UNENCRYPTED_URL ? "user/notification/register" : "MjkkMzAkMjAkMyQzMiQ=$MkUSwQkZDOUNGMDg5RTMwAQUEwODEwNDJOzIzQjI0NjNEODNCMEQ4MUVGNUMwNzlBMjY2OEJFMUVCMjdGN0ZCNUMzNg==$";

	// Payment App PackageName
	public static final String PACKAGE_NAME_ISP = "kvp.jjy.MispAndroid";
	public static final String PACKAGE_NAME_KFTC = "com.kftc.bankpay.android";
	public static final String PACKAGE_NAME_MPOCKET = "kr.co.samsungcard.mpocket";

	// Preference
	public static final String NAME_DAILYHOTEL_SHARED_PREFERENCE = "GOOD_NIGHT";
	public static final String RESULT_ACTIVITY_SPLASH_NEW_EVENT = "NEW_EVENT";
	public static final String KEY_PREFERENCE_RESENT_CNT = "RESENT_CNT";

	// user info
	public static final String KEY_PREFERENCE_AUTO_LOGIN = "AUTO_LOGIN";
	public static final String KEY_PREFERENCE_USER_ID = "USER_ID";
	public static final String KEY_PREFERENCE_USER_PWD = "USER_PWD";
	public static final String KEY_PREFERENCE_USER_ACCESS_TOKEN = "USER_ACCESSTOKEN";
	public static final String KEY_PREFERENCE_GCM_ID = "PUSH_ID";

	// version
	public static final String KEY_PREFERENCE_CURRENT_VERSION_NAME = "CURRENT_VERSION_NAME";
	public static final String KEY_PREFERENCE_MIN_VERSION_NAME = "MIN_VERSION_NAME";
	public static final String KEY_PREFERENCE_MAX_VERSION_NAME = "MAX_VERSION_NAME";
	public static final String KEY_PREFERENCE_SKIP_MAX_VERSION = "SKIP_MAX_VERSION";

	// region
	public static final String KEY_PREFERENCE_REGION_SELECT = "REGION_SELECT";
	public static final String KEY_PREFERENCE_REGION_SELECT_BEFORE = "REGION_SELECT_BEFORE";
	public static final String KEY_PREFERENCE_REGION_INDEX = "REGION_INDEX";

	// ga
	public static final String KEY_PREFERENCE_REGION_SELECT_GA = "REGION_SELECT_GA";
	public static final String KEY_PREFERENCE_HOTEL_NAME_GA = "HOTEL_NAME_GA";

	public static final String KEY_PREFERENCE_SHOW_GUIDE = "SHOW_GUIDE";

	public static final String KEY_PREFERENCE_HOTEL_NAME = "HOTEL_NAME";
	public static final String KEY_PREFERENCE_HOTEL_SALE_IDX = "HOTEL_SALE_IDX";
	public static final String KEY_PREFERENCE_HOTEL_CHECKOUT = "HOTEL_CHECKOUT";
	public static final String VALUE_PREFERENCE_HOTEL_NAME_DEFAULT = "none";
	public static final int VALUE_PREFERENCE_HOTEL_SALE_IDX_DEFAULT = 1;
	public static final String VALUE_PREFERENCE_HOTEL_CHECKOUT_DEFAULT = "14-04-30-20";
	public static final String KEY_PREFERENCE_USER_IDX = "USER_IDX"; // 예약 성공했을때 예약 사용함, 이름과 용도가 맞지 않음 -> 기존 코드

	public static final String KEY_PREFERENCE_ACCOUNT_READY_FLAG = "ACCOUNT_READY_FLAG"; //

	public static final String KEY_PREFERENCE_LOCALE = "LOCALE"; //

	// Android 컴포넌트 간에 데이터를 주고받을 때 사용되는 인텐트 이름(키)을 정의한 상수이다.
	public static final String NAME_INTENT_EXTRA_DATA_HOTEL = "hotel";
	public static final String NAME_INTENT_EXTRA_DATA_HOTELDETAIL = "hoteldetail";
	public static final String NAME_INTENT_EXTRA_DATA_SALETIME = "saletime";
	public static final String NAME_INTENT_EXTRA_DATA_REGION = "region";
	public static final String NAME_INTENT_EXTRA_DATA_HOTELIDX = "hotelIdx";
	public static final String NAME_INTENT_EXTRA_DATA_BOOKING = "booking";
	public static final String NAME_INTENT_EXTRA_DATA_PAY = "pay";
	public static final String NAME_INTENT_EXTRA_DATA_SELECTED_IMAGE_URL = "sel_image_url";
	//	public static final String NAME_INTENT_EXTRA_DATA_IS_INTENT_FROM_PUSH = "is_intent_from_push";
	public static final String NAME_INTENT_EXTRA_DATA_PUSH_TYPE = "push_type";
	public static final String NAME_INTENT_EXTRA_DATA_PUSH_MSG = "push_msg";

	// Push Type
	public static final int PUSH_TYPE_NOTICE = 0;
	public static final int PUSH_TYPE_ACCOUNT_COMPLETE = 1;

	// Android Activity의 Request Code들이다.
	public static final int CODE_REQUEST_ACTIVITY_HOTELTAB = 1;
	public static final int CODE_REQUEST_FRAGMENT_BOOKINGLIST = 2;
	public static final int CODE_REQUEST_ACTIVITY_LOGIN = 3;
	public static final int CODE_REQUEST_ACTIVITY_PAYMENT = 4;
	public static final int CODE_REQUEST_ACTIVITY_SPLASH = 5;
	public static final int CODE_REQEUST_ACTIVITY_SIGNUP = 6;
	public static final int CODE_REQUEST_ACTIVITY_BOOKING = 7;
	public static final int CODE_REQUEST_ACTIVITY_INTRO = 8;
	public static final int CODE_REQUEST_ISPMOBILE = 9;
	public static final int CODE_REQUEST_KFTC_BANKPAY = 10;
	public static final int CODE_REQUEST_ACTIVITY_BOOKING_DETAIL = 11;

	// Android Activity의 Result Code들이다.
	public static final int CODE_RESULT_ACTIVITY_PAYMENT_FAIL = 100;
	public static final int CODE_RESULT_ACTIVITY_PAYMENT_SUCCESS = 101;
	public static final int CODE_RESULT_ACTIVITY_PAYMENT_INVALID_SESSION = 102; // 
	public static final int CODE_RESULT_ACTIVITY_PAYMENT_SOLD_OUT = 103; // 완판되었을때.
	public static final int CODE_RESULT_ACTIVITY_PAYMENT_COMPLETE = 104;
	public static final int CODE_RESULT_ACTIVITY_PAYMENT_INVALID_DATE = 105;
	public static final int CODE_RESULT_ACTIVITY_PAYMENT_NOT_AVAILABLE = 106;
	public static final int CODE_RESULT_ACTIVITY_PAYMENT_NETWORK_ERROR = 107;
	public static final int CODE_RESULT_ACTIVITY_SPLASH_NEW_EVENT = 108;
	public static final int CODE_RESULT_ACTIVITY_PAYMENT_CANCELED = 109;
	public static final int CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY = 110;
	public static final int CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_TIME_ERROR = 111;
	public static final int CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_DUPLICATE = 112;
	public static final int CODE_RESULT_ACTIVITY_PAYMENT_TIMEOVER = 113;
	public static final int CODE_RESULT_ACTIVITY_PAYMENT_SALES_CLOSED = 114; // 예약을 하려 버튼을 눌렀는데 주문 시간이 지난경우.
	public static final int CODE_RESULT_ACTIVITY_EXPIRED_PAYMENT_WAIT = 201;

	// 예약 리스트에서 
	public static final int CODE_PAY_TYPE_CARD_COMPLETE = 10;
	public static final int CODE_PAY_TYPE_ACCOUNT_WAIT = 20;
	public static final int CODE_PAY_TYPE_ACCOUNT_COMPLETE = 21;

	// Android Google Analytics 정보들.
	public static final String GA_PROPERTY_ID = "UA-43721645-6";

	// Dispatch period in seconds.
	public static final int GA_DISPATCH_PERIOD = 60;

	// Prevent hits from being sent to reports, i.e. during testing.
	public static final boolean GA_IS_DRY_RUN = false;

	// GA Logger verbosity.
	public static final LogLevel GA_LOG_VERBOSITY = LogLevel.INFO;

	// Key used to store a user's tracking preferences in SharedPreferences.
	public static final String TRACKING_PREF_KEY = "trackingPreference";

	// GA E-Commerce Constants
	public static final String GA_COMMERCE_DEFAULT_AFFILIATION = "DailyHOTEL";
	public static final Double GA_COMMERCE_DEFAULT_TAX = 0d;
	public static final Double GA_COMMERCE_DEFAULT_SHIPPING = 0d;
	public static final String GA_COMMERCE_DEFAULT_CURRENCY_CODE = "KRW";
	public static final String GA_COMMERCE_DEFAULT_SKU = "1";
	public static final Long GA_COMMERCE_DEFAULT_QUANTITY = 1L;

	// GA Event Constants
	public static final String GA_SIGNUP_EVENT_CATEGORY = "Signup";
	public static final String GA_SIGNUP_EVENT_ACTION = "SignupComplete";
	public static final String GA_SIGNUP_EVENT_LABEL = "SignupComplete";
	public static final Long GA_SIGNUP_EVENT_VALUE = 1L;

	public static final String GA_PURCHASE_EVENT_CATEGORY = "Purchase";
	public static final String GA_PURCHASE_EVENT_ACTION = "PurchaseComplete";
	public static final String GA_PURCHASE_EVENT_LABEL = "PurchaseComplete";
	public static final Long GA_PURCHASE_EVENT_VALUE = 1L;

}
