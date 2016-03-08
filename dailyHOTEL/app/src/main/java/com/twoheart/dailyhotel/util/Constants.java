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
    // 디버그 빌드 여부 BuildConfig는 배포시에 자동으로 false가 된다고 한다.
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

    public enum SortType
    {
        DEFAULT,
        DISTANCE,
        LOW_PRICE,
        HIGH_PRICE,
        SATISFACTION;
    }

    public enum PlaceType
    {
        HOTEL,
        FNB; // 절대로 바꾸면 안됨 서버에서 fnb로 내려옴
    }

    public enum ViewType
    {
        LIST,
        MAP,
        GONE; // 목록이 비어있는 경우.
    }

    public static final String GCM_PROJECT_NUMBER = "1025681158000";

    // 웹서버 호스트
//    public static final String URL_DAILYHOTEL_SERVER_DEFAULT = UNENCRYPTED_URL ? "https://lb2.dailyhotel.kr/goodnight/" : "MTE2JDEzJDEwMiQ1OCQxMTEk$MDQzNkMwNTRCOBDgzNjM1MTM4NDY1M0VFMkIwRkM3QzMzQUE0NzU4OEI1MFEEzN0IwNkE3NUExRkM2QjNDNUFBNUE1MzI2RDNDQThGNJEFFQkJFGNzk4QTc0FRkFDMDgzOEQw$";
//    public static final String URL_DAILYHOTEL_SESSION_SERVER_DEFAULT = UNENCRYPTED_URL ? "https://api.dailyhotel.kr/goodnight/" : "MCQ4MyQxMjckMzYkMTck$CQTgwMkY4MTZFRTM2BODI4NDAyRDY1NDAzQjMBzMTg4REQ4RjUyNTJBQzNGMkI1RDk1QUE1OTU4N0RCOTREMEGQ0MjJGREQwNzYwNUI4QTM5NTcxNzk4NDJGQTFGNEQxMOEU4$";
    public static final String URL_DAILYHOTEL_SERVER_DEFAULT = UNENCRYPTED_URL ? "http://dev.dailyhotel.me/goodnight/" : "MTE1JDkwJDk1JDQ1JDExMiQ=$MDQyREYzODFGRTE2QUVGMTNDQzY0QURCRDJEN0JBNTdFMY0M2RkM5OUZBNDY4NjREQTNEQzk0NzA4RUJCMUJBMjJCN0EY4RjOVCQkMzQ0YzNzFFRZjk4NEYRwMEQ3NjQzNEMz$";
    public static final String URL_DAILYHOTEL_SESSION_SERVER_DEFAULT = UNENCRYPTED_URL ? "http://dev.dailyhotel.me/goodnight/" : "MTE1JDkwJDk1JDQ1JDExMiQ=$MDQyREYzODFGRTE2QUVGMTNDQzY0QURCRDJEN0JBNTdFMY0M2RkM5OUZBNDY4NjREQTNEQzk0NzA4RUJCMUJBMjJCN0EY4RjOVCQkMzQ0YzNzFFRZjk4NEYRwMEQ3NjQzNEMz$";

    // 회사 대표번호
    public static final String PHONE_NUMBER_DAILYHOTEL = "1800-9120";

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

    // Payment App PackageName
    public static final String PACKAGE_NAME_ISP = "kvp.jjy.MispAndroid";
    public static final String PACKAGE_NAME_KFTC = "com.kftc.bankpay.android";
    public static final String PACKAGE_NAME_MPOCKET = "kr.co.samsungcard.mpocket";

    // Event
    // Android 컴포넌트 간에 데이터를 주고받을 때 사용되는 인텐트 이름(키)을 정의한 상수이다.
    public static final String NAME_INTENT_EXTRA_DATA_HOTEL = "hotel";
    public static final String NAME_INTENT_EXTRA_DATA_HOTELLIST = "hotellist";
    public static final String NAME_INTENT_EXTRA_DATA_HOTELDETAIL = "hoteldetail";
    public static final String NAME_INTENT_EXTRA_DATA_SALETIME = "saletime";
    public static final String NAME_INTENT_EXTRA_DATA_REGION = "region";
    public static final String NAME_INTENT_EXTRA_DATA_HOTELIDX = "hotelIndex";
    public static final String NAME_INTENT_EXTRA_DATA_HOTELGRADE = "hotelGrade";
    public static final String NAME_INTENT_EXTRA_DATA_BOOKING = "booking";
    public static final String NAME_INTENT_EXTRA_DATA_BOOKINGIDX = "bookingIndex";
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
    public static final String NAME_INTENT_EXTRA_DATA_CATEGORY = "category";
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
    public static final String NAME_INTENT_EXTRA_DATA_GOURMETIDX = "gourmetIndex";
    public static final String NAME_INTENT_EXTRA_DATA_DBENEFIT = "dBenefit";

    // Push Type
    public static final int PUSH_TYPE_NOTICE = 0;
    public static final int PUSH_TYPE_ACCOUNT_COMPLETE = 1;

    // Android Activity의 Request Code들이다.
    public static final int CODE_REQUEST_ACTIVITY_HOTEL_DETAIL = 1;
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
    public static final int CODE_REQUEST_ACTIVITY_REGIONLIST = 14;
    public static final int CODE_REQUEST_ACTIVITY_USERINFO_UPDATE = 15;
    public static final int CODE_REQUEST_ACTIVITY_PLACE_DETAIL = 20;
    public static final int CODE_REQUEST_ACTIVITY_SATISFACTION_HOTEL = 21;
    public static final int CODE_REQUEST_ACTIVITY_SATISFACTION_GOURMET = 22;
    public static final int CODE_REQUEST_ACTIVITY_CALENDAR = 30;
    public static final int CODE_REQUEST_ACTIVITY_CURATION = 31;
    public static final int CODE_REQUEST_ACTIVITY_EVENTWEB = 32;

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
}
