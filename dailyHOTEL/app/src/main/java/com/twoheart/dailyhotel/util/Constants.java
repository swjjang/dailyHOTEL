/**
 * \ * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * Constants (어플리케이션 전역 상수)
 * <p>
 * 어플리케이션에서 사용되는 전역 상수들을 정리해놓은 인터페이스이다. 어플리
 * 케이션에서 사용되는 전역 상수들은 거의 고정된 값들이며 여러 부분에서 일
 * 률적으로 사용되므로 상수로서 선언됐다. 이 인터페이스는 각 클래스에서 상속
 * 받아서 바로 사용될 수 있다.
 *
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 * @since 2014-02-24
 */
package com.twoheart.dailyhotel.util;

import com.twoheart.dailyhotel.BuildConfig;

public interface Constants
{
    // 디버그 빌드 여부 BuildConfig는 배포시에 자동으로 false가 된다고 한다. 테스트 해보고 싶음.
    public static final boolean DEBUG = BuildConfig.DEBUG;
    public static final boolean UNENCRYPTED_URL = false;
    public static final Stores RELEASE_STORE = Stores.PLAY_STORE;

    // 스토어 선택.
    public enum Stores
    {
        PLAY_STORE("PlayStore"),
        T_STORE("Tstore"),
        N_STORE("Nstore");

        private String mName;

        private Stores(String name)
        {
            mName = name;
        }

        public String getName()
        {
            return mName;
        }
    }

    // 항상 열리게 셋팅 여부
    public static final String GCM_PROJECT_NUMBER = "1025681158000";
    public static final String GCM_DEVICE_TYPE_ANDROID = "0"; // GCM 등록을 할 때 API의 deviceType, 0은 안드로이드 1은 아이폰.

    // 웹서버 호스트
    //"http://restful.dailyhotel.kr/goodnight/"; //  서비스 서버
    public static final String URL_DAILYHOTEL_SERVER_DEFAULT = UNENCRYPTED_URL ? "https://restful.dailyhotel.kr/goodnight/" : "NjgkMTIyJDQxJDQkNjAk$MDQxKMEUxQkZBNzMwNUMzRTg4NDA2RjVEMzgzNTg2OOEE2OEE2RDc5M0E0NjLRENDc2NUREDOUMzOTQ3RjczNzE2MEE1QTRDMkU3OTFDOUU5Mjc1NUNFQjhDRTBGMDzYwMUQ3$";
    public static final String URL_DAILYHOTEL_SERVER_8080 = UNENCRYPTED_URL ? "http://restful.dailyhotel.kr:8080/goodnight/" : "MjEkNzYkMTI5JDk1JDUzJA==$QTY2MTMyRUQ5RURFRkZBQHzU1RUIwNkI0Qjk5MTVFMTk5MEFDQkNFLMzQwQzQzNTNDQzJGMDk0N0EJwRjIwRUYzMEJEM0U5NNzFDNUE4NTMzREMzM0ZCQzc4M0VCQjMyMTQQ1$";
    public static final String URL_DAILYHOTEL_SERVER_8081 = UNENCRYPTED_URL ? "http://restful.dailyhotel.kr:8081/goodnight/" : "MjgkMTA1JDEyMSQ4MiQxMjIk$NTU0NTQwMTExRDVDNUVFMzJEMTc3BNTA3QTQ0QTVDMDM1OTNBNEFGRjlDRTZEQTU0NzUyMTNFRTY1NjIxNTjZFQjM2RDVDNjAxRjI4NzJBNMTdFMTk1RUJFQzAQNwNDdFQTg4$";
    //    public static final String URL_DAILYHOTEL_SERVER_DEFAULT = UNENCRYPTED_URL ? "http://test-api.dailyhotel.me/goodnight/" : "NjQkMTA0JDkxJDEyNiQyMiQ=$M0VEQThENzgyRUVGM0QzQkJMyQ0I4QzNBOTlBNEUzMTE3OEUwOUZBNkQ2RjE5QUY2MQzU3OUQ5ODAwRTlBNTVCOTRDM0JFBNzBGMzk3NkZNGNzdFQjg5ODAxODZDRDcL1QUI2$";

    public static final String URL_DAILYHOTEL_LB_SERVER_DEFAULT = UNENCRYPTED_URL ? "http://lb.dailyhotel.kr/goodnight/" : "MTEyJDEwMyQ0NCQzNSQ5MCQ=$RkE1MTczODQ3QzdBQUEwMUQ0MDExMEFGNTQC5NjRBMDQ3ARURFRDBGMDUwMzEwOTU5M0VDNEFERjJCMDc5MzQwNjdEWOUZENzc5MUM0RDADxNDlEODk0RNzBERkVBOTY3NDg1$";
    //    public static final String URL_DAILYHOTEL_LB_SERVER_DEFAULT = UNENCRYPTED_URL ? "http://test-api.dailyhotel.me/goodnight/" : "NjQkMTA0JDkxJDEyNiQyMiQ=$M0VEQThENzgyRUVGM0QzQkJMyQ0I4QzNBOTlBNEUzMTE3OEUwOUZBNkQ2RjE5QUY2MQzU3OUQ5ODAwRTlBNTVCOTRDM0JFBNzBGMzk3NkZNGNzdFQjg5ODAxODZDRDcL1QUI2$";

    // 회사 대표번호
    public static final String PHONE_NUMBER_DAILYHOTEL = "1800-9120";

    // uiLock을 띄우고 API를 콜하였는데 제한 시간 안에 리턴을 받지 못한경우. error 발생.
    public static final int REQUEST_EXPIRE_JUDGE = 60000;

    // 구글플레이 서비스 상태 확인 타임아웃
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 15000;

    // Volley의 최대 retry 횟수,  여기서 0은 리퀘스트를 리트라이 하지 않음을 말함.
    public static final int REQUEST_MAX_RETRY = 0;

    // DailyHOTEL Reservation Controller WebAPI URL
    // api/hotel/v1/payment/session/common
    public static final String URL_WEBAPI_HOTEL_V1_PAYMENT_SESSION_COMMON = UNENCRYPTED_URL ? "api/hotel/v1/payment/session/common" : "MjAkNiQ0NiQ0NSQ0MyQ=$RTQ2MEIMxRTFGRjUyMUIxXMzVDRERBQzBBOThFNkE5NYUJWBINzJCRjMxMTM2MUYwNEEzMkU0OURBNzVBRTg4NDU0NkYzRkI2REJFQThERjAxNUJBNTUxNURBOENGQ0IyQjVG$";

    // api/fnb/payment/session/common
    public static final String URL_WEBAPI_FNB_PAYMENT_SESSION_COMMON = UNENCRYPTED_URL ? "api/fnb/payment/session/common" : "NzIkMTAkMzMkNDEkNTYk$NjlERDU2NDLQyQUZEQzczMEU0NUNDNjBCIRDdCNzYD0OTE5QkMwQTUxQUTVGNEE1RjE1REI2RUJBANzk2QjVFNDM1Ng==$";

    // DailyHOTEL Site Controller WebAPI URL
    // A/B Test
    // api/abtest/testcase
    public static final String URL_WEBAPI_ABTEST_TESTCASE = UNENCRYPTED_URL ? "api/abtest/testcase" : "NTYkMzgkOSQ3NyQ4MSQ=$QTc1QzU3QP0VBMkUyQ0RDMjA4RUZFQUEwRjBCOEOY1MkYwNzg4OEI4MEZDBMzAwRjExRkM4N0VBRUFRGMHDYxMkM3QQ==$";

    // api/abtest/kakao/consult/feedback
    public static final String URL_WEBAPI_ABTEST_KAKAO_CONSULT_FEEDBACK = UNENCRYPTED_URL ? "api/abtest/kakao/consult/feedback" : "NTEkMjgkMTEwJDQ3JDQ0JA==$QTUxRjgwNzIyNDY1MjQ2ODJGMTdDIMUU4QTRCOTc3QTEP3MDTc5OTMG4RTc1M0NGRUIzNkNBOUJBQUJCOTg4OTU5MjBCNzg4MEZFODk5M0VFRTgxZODMyMDU3NjlGQUYxMzkw$";

    //
    public static final String URL_STORE_GOOGLE_DAILYHOTEL = "market://details?id=com.twoheart.dailyhotel";
    public static final String URL_STORE_GOOGLE_KAKAOTALK = "market://details?id=com.kakao.talk";
    public static final String URL_STORE_T_DAILYHOTEL = "http://tsto.re/0000412421";
    public static final String URL_STORE_GOOGLE_DAILYHOTEL_WEB = "https://play.google.com/store/apps/details?id=com.twoheart.dailyhotel";
    public static final String URL_STORE_GOOGLE_KAKAOTALK_WEB = "https://play.google.com/store/apps/details?id=com.kakao.talk";

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

    // Register Credit Card URL
    public static final String URL_REGISTER_CREDIT_CARD = UNENCRYPTED_URL ? "api/user/session/billing/card/register" : "MTA4JDgyJDY3JDM1JDgk$NjE5NTkxFODMxQTRCM0RFNzIzNjRCQjc2RThJGQzQxRDRCQkNEQjk5N0U4ODhBMUM5MUYU3RTlGMzY3ODA3NEVUzREQyQjM2MzEwN0VFNzA5ODQ2GMTgwNTVFODA5NzE2MzRE$";

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
    public static final String KEY_PREFERENCE_USER_TYPE = "USER_TYPE";
    public static final String KEY_PREFERENCE_USER_ACCESS_TOKEN = "USER_ACCESSTOKEN";
    public static final String KEY_PREFERENCE_GCM_ID = "PUSH_ID";
    public static final String KEY_PREFERENCE_OVERSEAS_NAME = "OVERSEAS_NAME";
    public static final String KEY_PREFERENCE_OVERSEAS_PHONE = "OVERSEAS_PHONE";
    public static final String KEY_PREFERENCE_OVERSEAS_EMAIL = "OVERSEAS_EMAIL";
    // version
    public static final String KEY_PREFERENCE_CURRENT_VERSION_NAME = "CURRENT_VERSION_NAME";
    public static final String KEY_PREFERENCE_MIN_VERSION_NAME = "MIN_VERSION_NAME";
    public static final String KEY_PREFERENCE_MAX_VERSION_NAME = "MAX_VERSION_NAME";
    public static final String KEY_PREFERENCE_SKIP_MAX_VERSION = "SKIP_MAX_VERSION";
    // region
    public static final String KEY_PREFERENCE_REGION_SELECT = "REGION_SELECT";
    public static final String KEY_PREFERENCE_REGION_SELECT_BEFORE = "REGION_SELECT_BEFORE";
    public static final String KEY_PREFERENCE_REGION_INDEX = "REGION_INDEX";
    public static final String KEY_PREFERENCE_REGION_SETTING = "REGION_SETTING";
    public static final String KEY_PREFERENCE_FNB_REGION_SETTING = "FNB_REGION_SETTING";
    public static final String KEY_PREFERENCE_FNB_REGION_SELECT = "FNB_REGION_SELECT";
    public static final String KEY_PREFERENCE_FNB_REGION_SELECT_BEFORE = "FNB_REGION_SELECT_BEFORE";
    // ga
    public static final String KEY_PREFERENCE_REGION_SELECT_GA = "REGION_SELECT_GA";
    public static final String KEY_PREFERENCE_HOTEL_NAME_GA = "HOTEL_NAME_GA";
    public static final String KEY_PREFERENCE_PLACE_REGION_SELECT_GA = "PLACE_REGION_SELECT_GA";
    public static final String KEY_PREFERENCE_PLACE_NAME_GA = "PLACE_NAME_GA";
    public static final String KEY_PREFERENCE_SHOW_GUIDE = "SHOW_GUIDE";
    public static final String KEY_PREFERENCE_HOTEL_NAME = "HOTEL_NAME";
    public static final String KEY_PREFERENCE_PLACE_NAME = "PLACE_NAME";
    public static final String KEY_PREFERENCE_HOTEL_CHECKOUT = "HOTEL_CHECKOUT";
    public static final String KEY_PREFERENCE_HOTEL_CHECKIN = "HOTEL_CHECKIN";
    public static final String KEY_PREFERENCE_PLACE_TICKET_CHECKOUT = "PLACE_TICKET_CHECKOUT";
    public static final String KEY_PREFERENCE_PLACE_TICKET_CHECKIN = "PLACE_TICKET_CHECKIN";
    public static final String VALUE_PREFERENCE_HOTEL_NAME_DEFAULT = "none";
    public static final int VALUE_PREFERENCE_HOTEL_ROOM_IDX_DEFAULT = 1;
    public static final String VALUE_PREFERENCE_HOTEL_CHECKOUT_DEFAULT = "14-04-30-20";
    public static final String KEY_PREFERENCE_USER_IDX = "USER_IDX"; // 예약 성공했을때 예약 사용함, 이름과 용도가 맞지 않음 -> 기존 코드
    public static final String KEY_PREFERENCE_HOTEL_ROOM_IDX = "HOTEL_RESERVATION_IDX";
    public static final String KEY_PREFERENCE_PLACE_TICKET_IDX = "PLACE_TICKET_RESERVATION_IDX";
    public static final String KEY_PREFERENCE_ACCOUNT_READY_FLAG = "ACCOUNT_READY_FLAG"; //
    public static final String KEY_PREFERENCE_BY_SHARE = "BY_SHARE";
    // Event
    public static final String KEY_PREFERENCE_LOOKUP_EVENT_TIME = "LOOKUP_EVENT_TIME";
    public static final String KEY_PREFERENCE_NEW_EVENT_TIME = "NEW_EVENT_TIME";
    // Android 컴포넌트 간에 데이터를 주고받을 때 사용되는 인텐트 이름(키)을 정의한 상수이다.
    public static final String NAME_INTENT_EXTRA_DATA_HOTEL = "hotel";
    public static final String NAME_INTENT_EXTRA_DATA_HOTELLIST = "hotellist";
    public static final String NAME_INTENT_EXTRA_DATA_HOTELDETAIL = "hoteldetail";
    public static final String NAME_INTENT_EXTRA_DATA_SALETIME = "saletime";
    public static final String NAME_INTENT_EXTRA_DATA_REGION = "region";
    public static final String NAME_INTENT_EXTRA_DATA_HOTELIDX = "hotelIdx";
    public static final String NAME_INTENT_EXTRA_DATA_BOOKING = "booking";
    public static final String NAME_INTENT_EXTRA_DATA_BOOKINGIDX = "bookingIdx";
    public static final String NAME_INTENT_EXTRA_DATA_PAY = "pay";
    public static final String NAME_INTENT_EXTRA_DATA_TICKETPAYMENT = "ticketPayment";
    public static final String NAME_INTENT_EXTRA_DATA_SELECTED_IMAGE_URL = "sel_image_url";
    public static final String NAME_INTENT_EXTRA_DATA_SELECTED_POSOTION = "selectedPosition";
    //	public static final String NAME_INTENT_EXTRA_DATA_IS_INTENT_FROM_PUSH = "is_intent_from_push";
    public static final String NAME_INTENT_EXTRA_DATA_PUSH_TYPE = "push_type";
    public static final String NAME_INTENT_EXTRA_DATA_PUSH_MSG = "push_msg";
    public static final String NAME_INTENT_EXTRA_DATA_PUSH_TITLE = "push_title";
    public static final String NAME_INTENT_EXTRA_DATA_PUSH_LINK = "push_link";
    public static final String NAME_INTENT_EXTRA_DATA_REGIONMAP = "regionmap";
    public static final String NAME_INTENT_EXTRA_DATA_CREDITCARD = "creditcard";
    public static final String NAME_INTENT_EXTRA_DATA_MESSAGE = "message";
    public static final String NAME_INTENT_EXTRA_DATA_PROVINCE = "province";
    public static final String NAME_INTENT_EXTRA_DATA_AREA = "area";
    public static final String NAME_INTENT_EXTRA_DATA_AREAITEMLIST = "areaItemlist";
    public static final String NAME_INTENT_EXTRA_DATA_CUSTOMER = "customer";
    public static final String NAME_INTENT_EXTRA_DATA_IMAGEURLLIST = "imageUrlList";
    public static final String NAME_INTENT_EXTRA_DATA_HOTELNAME = "hotelName";
    public static final String NAME_INTENT_EXTRA_DATA_MOREINFORMATION = "moreInformation";
    public static final String NAME_INTENT_EXTRA_DATA_LATITUDE = "latitude";
    public static final String NAME_INTENT_EXTRA_DATA_LONGITUDE = "longitude";
    public static final String NAME_INTENT_EXTRA_DATA_SALEROOMINFORMATION = "saleRoomInformation";
    public static final String NAME_INTENT_EXTRA_DATA_TICKETINFORMATION = "ticketInformation";
    public static final String NAME_INTENT_EXTRA_DATA_SALEINDEX = "saleIndex";
    public static final String NAME_INTENT_EXTRA_DATA_IMAGEURL = "imageUrl";
    public static final String NAME_INTENT_EXTRA_DATA_NIGHTS = "nights";
    public static final String NAME_INTENT_EXTRA_DATA_DAILYTIME = "dailyTime";
    public static final String NAME_INTENT_EXTRA_DATA_DAYOFDAYS = "dayOfDays";
    public static final String NAME_INTENT_EXTRA_DATA_TYPE = "type";
    public static final String NAME_INTENT_EXTRA_DATA_ROOMINDEX = "roomIndex";
    public static final String NAME_INTENT_EXTRA_DATA_RESERVATIONINDEX = "reservationIndex";
    public static final String NAME_INTENT_EXTRA_DATA_CHECKINDATE = "checkInDate";
    public static final String NAME_INTENT_EXTRA_DATA_CHECKOUTDATE = "checkOutDate";
    public static final String NAME_INTENT_EXTRA_DATA_URL = "url";
    public static final String NAME_INTENT_EXTRA_DATA_PLACEIDX = "placeIdx";
    public static final String NAME_INTENT_EXTRA_DATA_PLACENAME = "placeName";
    public static final String NAME_INTENT_EXTRA_DATA_PLACETYPE = "placeType";
    public static final String NAME_INTENT_EXTRA_DATA_RESULT = "result";
    public static final String NAME_INTENT_EXTRA_DATA_RECOMMENDER = "recommender";
    public static final String NAME_INTENT_EXTRA_DATA_ISDAILYUSER = "isDailyUser";
    public static final String NAME_INTENT_EXTRA_DATA_DATE = "date";

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
    public static final int CODE_REQUEST_ACTIVITY_REGISTERCREDITCARD = 12;
    public static final int CODE_REQUEST_ACTIVITY_CREDITCARD_MANAGER = 13;
    public static final int CODE_REQUEST_ACTIVITY_SELECT_AREA = 14;
    public static final int CODE_REQUEST_ACTIVITY_USERINFO_UPDATE = 15;
    public static final int CODE_REQUEST_FRAGMENT_PLACE_MAIN = 20;
    public static final int CODE_REQUEST_ACTIVITY_SATISFACTION_HOTEL = 21;
    public static final int CODE_REQUEST_ACTIVITY_SATISFACTION_GOURMET = 22;
    public static final int CODE_REQUEST_ACTIVITY_CALENDAR = 30;

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
    public static final int CODE_RESULT_ACTIVITY_PAYMENT_UNKNOW_ERROR = 115; // 알수 없는 에러.
    public static final int CODE_RESULT_ACTIVITY_PAYMENT_NOT_ONSALE = 116;
    public static final int CODE_RESULT_ACTIVITY_PAYMENT_PRECHECK = 117;
    public static final int CODE_RESULT_ACTIVITY_EXPIRED_PAYMENT_WAIT = 201;
    public static final int CODE_RESULT_ACTIVITY_PAYMENT_CANCEL = 202;
    public static final int CODE_RESULT_ACTIVITY_SETTING_LOCATION = 210;
    public static final int CODE_RESULT_PAYMENT_BILLING_SUCCSESS = 300;
    public static final int CODE_RESULT_PAYMENT_BILLING_FAIL = 301;
    public static final int CODE_RESULT_PAYMENT_BILLING_DUPLICATE = 302;

    // 예약 리스트에서
    public static final int CODE_PAY_TYPE_CARD_COMPLETE = 10;
    public static final int CODE_PAY_TYPE_ACCOUNT_WAIT = 20;
    public static final int CODE_PAY_TYPE_ACCOUNT_COMPLETE = 21;

    // 퍼미션 관련
    public static final int REQUEST_CODE_PERMISSIONS_ACCESS_FINE_LOCATION = 10;
    public static final int REQUEST_CODE_PERMISSIONS_READ_PHONE_STATE = 11;

    // Android Google Analytics 정보들.
    public static final String GA_PROPERTY_ID = "UA-43721645-6";

    // Key used to store a user's tracking preferences in SharedPreferences.
    public static final String TRACKING_PREF_KEY = "trackingPreference";
}
