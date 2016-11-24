package com.twoheart.dailyhotel.util;

import android.app.Activity;

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
        T_STORE("Tstore");

        private String mName;

        Stores(String name)
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
        SATISFACTION
    }

    public enum PlaceType
    {
        HOTEL,
        FNB // 절대로 바꾸면 안됨 서버에서 fnb로 내려옴
    }

    public enum ViewType
    {
        LIST,
        MAP,
        GONE // 목록이 비어있는 경우.
    }

    public enum UserInformationType
    {
        NAME,
        PHONE,
        EMAIL
    }

    public enum ANIMATION_STATE
    {
        START,
        END,
        CANCEL
    }

    public enum ANIMATION_STATUS
    {
        SHOW,
        HIDE,
        SHOW_END,
        HIDE_END
    }

    public enum SearchType
    {
        SEARCHES,
        AUTOCOMPLETE,
        RECENT,
        LOCATION
    }

    public static String DAILY_USER = "normal";
    public static String KAKAO_USER = "kakao_talk";
    public static String FACEBOOK_USER = "facebook";
    public static String DAILY_INTRO_DEFAULT_VERSION = "2013-07-17T12:00:00+09:00";
    public static String DAILY_INTRO_CURRENT_VERSION = "2016-11-22T10:00:00+09:00";

    public static final String GCM_PROJECT_NUMBER = "1025681158000";
    public static final String GOOGLE_MAP_KEY = UNENCRYPTED_URL ? "AIzaSyBEynLg8WjW7YKtmc2B6aOCn7PQtGig-6I" : "MzYkMTE1JDMyJDMzJDYxJDc5JDgwJDEzMCQxMTgkNzYkNjYkMjYkNDUkMTQkODMkMTUk$OUREQTVFOTM5QjIRA5QTlCNzE4QTKI3ODRECLODg3JODRBNZEY2MjE5NTZEN0I3NjJMxN0NJGMUUzQTI0AODEYGQxRTQ5OTI3RjVBNkExQjg5QTM3NzczNDQwOUM1TMzHY1MDI2NUQX0NDIx$";
    public static final String KAKAO_NAVI_KEY = UNENCRYPTED_URL ? "244794bd54c145beabfaa69c057b8b73" : "NjUkNjEkNjYkMTckOTckOTYkMSQ2OSQ4MyQxMTUkNzAkMTE0JDE4JDU2JDE0MCQ4NCQ=$MLjM4MDkzQkE4QUJCMMCDlBRURENUM5Q0E4MzFGRDlEMEM5OEQwNjRCQDjY0NkNDOCUUwMFFNAzcyOEQ0QjYC5RSTIwMDYxNTQzRThCYMHjZFQjgyRTRDKNEMY4RTQwOTM3ODRFRUVGQzCI1$";
    public static final String TMAP_NAVI_KEY = UNENCRYPTED_URL ? "0854b91f-6520-3d72-9ac4-e2623084fc48" : "NTUkMjIkNDUkNzUkNjMkMTE4JDI0JDI3JDc3JDExMiQ4NyQ2NCQzMyQyMCQ5MSQyNCQ=$MDA5RkE5NUFCMDMwMjRBEQjHHVCCNUTRCNDHJFQzgwNzI0MThCAODgyMjdBRkYYyMTMZyJODMzMDc0NTlVGVNTBDMkMOJ3MDNGMTQxNTcwOTY1N0IxQzFDJMERFQjU4OITY5REYyNDFCRTE1$";

    // 웹서버 호스트
    //    public static final String URL_DAILYHOTEL_SERVER_DEFAULT = UNENCRYPTED_URL ? "https://mobileapi.dailyhotel.kr/goodnight/" : "ODQkNCQ4JDEwMCQ2NCQzNSQxMDckMTE2JDEyMyQyNCQxMDgkODckMCQxMDYkODAkMTIxJA==$TNEExAN0JQFRjMyRUNCRkI2RDHIyRkEyMTY5MAEU3M0JEM0FCNDlEODMyOEZGRjZFQjZhCM0RCMkEyOUIMyNjE5NEGQ3GNEZCNTgwMEEwNORjEyOQXThBRDczJOTDUxMjAKxRTEyMDAxNDg3$";

    public static final String URL_DAILYHOTEL_SERVER_DEFAULT = UNENCRYPTED_URL ? "http://dev-mobileapi.dailyhotel.me/goodnight/" : "OTAkMzQkMTckNDYkNjkkMzkkNyQxMjEkMTI5JDY5JDQ0JDk3JDE2JDg5JDE0MSQ5MSQ=$NjBBMzJMCMTJEQzICzRBTEzMEREMjlGRjQ2QjBBGQUTI2CNzcxCRjZDNzBEMEE1MTU1OUVETN0WIwRkMxQUZDMTk5INNjVCMzEwRDTRNFQzEzQjg4Q0EwQjk4RUEyQjJA4QjFCMA0Q2MzIAw$";

    // 회사 대표번호
    public static final String PHONE_NUMBER_DAILYHOTEL = "1800-9120";

    //
    public static final String URL_STORE_GOOGLE_DAILYHOTEL = "market://details?id=com.twoheart.dailyhotel";
    public static final String URL_STORE_GOOGLE_KAKAOTALK = "market://details?id=com.kakao.talk";
    public static final String URL_STORE_T_DAILYHOTEL = "http://tsto.re/0000412421";
    public static final String URL_STORE_GOOGLE_DAILYHOTEL_WEB = "https://play.google.com/store/apps/details?id=com.twoheart.dailyhotel";
    public static final String URL_STORE_GOOGLE_KAKAOTALK_WEB = "https://play.google.com/store/apps/details?id=com.kakao.talk";
    //
    public static final String URL_WEB_PRIVACY = UNENCRYPTED_URL ? "http://policies.dailyhotel.co.kr/privacy/" : "OTYkNzIkNDAkMTAyJDYkMCQxMDgkNzQkMTMwJDU3JDEzMSQzMiQ2NyQyMyQxMDAkODkk$EOEFEOUCY3RTgwQkU4NDA3RCjY0NTRDRTNZGQTU5MzQ5YN0U5RUY0MjQyMTXAyNEY5OEZQ1QUVGQ0ZVDJQUU5MzhCVMzJBQzI4QzMX1NjNCKQjgMwQjVRCOEVGNjBBOThDOTVBMDJSgxMjk2$";
    public static final String URL_WEB_TERMS = UNENCRYPTED_URL ? "http://policies.dailyhotel.co.kr/terms/" : "ODckNTEkNDQkNzQkNzckNDQkNjkkMCQ3MyQxMjkkMTEwJDM4JDEyMCQxMTAkMTAkODAk$RQUJGODY2RZjUwNTQ1NUQ3NTc2MUIyOTNGOThFNF0U2MkIzMZNzIxM0QM2OTFDMjE2MUQyQjYgzIQzM5UCRjLQ4MTIzNUNEQTQWxMkZGMjQ3RjUxVQPzJFNDFGRRTA1NTFGODdBDMkU5MzYx$";
    public static final String URL_WEB_ABOUT = UNENCRYPTED_URL ? "http://policies.dailyhotel.co.kr/about/" : "NTIkMjkkNzYkMTExJDQxJDE2JDEzMiQ5NiQ2NiQzOSQyOCQxMTgkMTA4JDg0JDExNiQxMzkk$MTNGRjIzQjRCQjQ5CQjc5NzhDNzdYDNCDQyRjYzQOTU3WMzFEM0MzQ0M1GRTlBMzI1MjFJBQzBBMDNCREJQxSMjU4RUFBM0JBNzkU3MDM0MDENzRDcyQHTA1CLNDRFM0QxOUQ0RDhDQCTJU5$";
    public static final String URL_WEB_LOCATION_TERMS = UNENCRYPTED_URL ? "http://policies.dailyhotel.co.kr/location/" : "MTA4JDEwNCQ5OSQxMSQzNCQxMDUkMyQ4OSQ3OSQ0NSQxMzUkMjYkODckMzAkMTQwJDUxJA==$M0YOzN0Q3M0EJ1MEE2MTA3QkYzYMTQVyMzc1OHUM5NDY1MzZVBMUzY4MDlBQTQzNkFBQUY2NDE4NEQ3QkU2XOUZDNQDk2RTTZFOTUyMUQ1MDkA0OEMJDLMUFEXRTE4REQ1QTA4MjY3MZTZY1$";
    public static final String URL_WEB_CHILD_PROTECT_TERMS = UNENCRYPTED_URL ? "http://policies.dailyhotel.co.kr/child_protect_160404/" : "MTM0JDI1JDE4JDE2MCQxNDYkOTUkMTY1JDE4JDE3NCQ5OCQ5OSQxNzckMTUzJDQkMTA2JDMxJA==$MTI2TQ0Y2OTM5QkY4MUJQM1NTlCMBUMK1MURBRjYzMTY3ODY0RUY2OUJGRTYzRTgwRUU5QzUxMzczNzIwQzA2Q0RGOUE2REJCMXEQWMyRUUVwNTQxQ0ZGMTA2OTNEOEE2REIyRTZEODkyOEAY0QzM4MjhRGOKTlDQUJEMkE4NOzNBCMTMyNkITY2QUE=$";
    public static final String URL_WEB_BONUS_TERMS = UNENCRYPTED_URL ? "http://dailyhotel.kr/webview_cnote/bonus" : "NjMkODIkMTIzJDQ2JDEyMSQxMzEkODQkMjYkNiQ2MCQ4OCQxMTckMjIkNDEkODIkNTkk$NjU0REYJCMUEwQjBCQkZDRZkI1NkJQwMTJENzZENEBM1RDI4QjQRBNTdFQzCQ1NWzEyMkJDFMUM3NkQyN0UK4ODU3RFAXDFDNkU0NTlGQTc0REYxODAzRDIzNV0Y2MzM5NzJNDNNTU5MTVI5$";
    public static final String URL_WEB_COMMON_COUPON_TERMS = UNENCRYPTED_URL ? "http://dailyhotel.kr/webview_cnote/coupon" : "NzgkNjIkNSQ3NCQxMTkkMjAkMTIxJDU4JDM1JDE4JDM0JDk3JDExOCQxMTckMzUkMTM0JA==$RTNFOTDlCM0Q5RTkzRZUEJ3RTE0RDMzRjMTD3RZjE1Q0MxNTc1MTdGRDc0NTA3OQjAzNDPIyNTAxN0NFLMTZDNTIcwQTI1QUMzFRkZBNzU4QTQ0NDQyQTENwBRUVFMjISJxQjgG4MDVBMzdF$";
    public static final String URL_WEB_EACH_COUPON_TERMS = UNENCRYPTED_URL ? "http://dailyhotel.kr/webview_coupon_note/" : "ODYkMTckMTIxJDI5JDEwNSQ3MSQ0MCQyMiQ0JDkyJDgxJDg4JDQwJDYxJDk3JDMwJA==$QzFCKQzI3NzRDODI5RHEM5OIERDQkIR3GREY3Q0I3RNkWQ0NjgyOThBNEVDMTgW5N0JDRjNEQzFBMW0U2M0EIwMzdCRVTE2MzHYPkxNTAwMUI1NjlFREAZEMkREQjFCQjVBREYG5MEU0QjRB$";
    public static final String URL_WEB_FAQ = UNENCRYPTED_URL ? "http://dailyhotel.co.kr/wp/webview/faq.html" : "OTUkMjYkMiQyMSQxMSQyOSQ1NCQxOSQ1NyQzJDE0JDc1JDExNSQ5JDgxJDU0JA==$MUFBQxNkQDyQ0IJVDMjA3MJ0ZBFQjMyOELQNGMzVGNzc2RkY1Q0NDNUDkzMFjRA2NDE2NDc1N0JCOMDhFMGjY5NDFBOUU2QUE3QjEyNURFMTYAwRjBCMDNOFRjU0RjY1NUI4OUI0MjI5NjRE$";
    public static final String URL_WEB_LICNESE = UNENCRYPTED_URL ? "http://wp.me/P7uuuR-4Z1" : "NzEkMTIkODAkMzIkOSQ2OCQ3MiQ0MiQ0JDkzJDE3JDcyJDQ3JDcwJDEkMTIk$OFUVBINkUzRVSkQ5QRTOg0QjhEM0FDOTlEQTNBFMzE2QzFRFNTDQxNUI1NzZFMDc2RkMzOEFHCMPDTI4WQTkAyN0I2NjUE5NkZDQNA==$";

    // 테스트 서버
    public static final String URL_WEB_EACH_COUPON_TERMS_DEV = UNENCRYPTED_URL ? "http://dev-extranet-hotel.dailyhotel.me/webview_coupon_note/" : "NjMkMjkkOTAkMTYwJDEyNiQ0MSQxMDAkMTQyJDEwMCQxNDMkOTUkMTY3JDEwNyQzNSQ5MCQ5NCQ=$Q0VEQUNEOTlEOUM4RTdBQTk5NEQxNCUE4RUNRDMzY1DNTRERDY3QjJDRjJCNTIwQzMYzQ0YzNzY0MzA2NzMzMkZCMUCQ0QROEUXxNUVFJTRjAxZMTE4OTdGREM1NEI3NUJDODIJ3ODU4NkUyQzk1KMQUI2RUUwQ0M5MTEzRjI1NTOUE1OEE0M0U4RUI=$";

    // Payment App GoogleStore URL
    public static final String URL_STORE_PAYMENT_ISP = "market://details?id=kvp.jjy.MispAndroid320";
    public static final String URL_STORE_PAYMENT_KFTC = "market://details?id=com.kftc.bankpay.android&hl=ko";
    public static final String URL_STORE_PAYMENT_MPOCKET = "market://details?id=kr.co.samsungcard.mpocket";

    // Payment App PackageName
    public static final String PACKAGE_NAME_ISP = "kvp.jjy.MispAndroid";
    public static final String PACKAGE_NAME_KFTC = "com.kftc.bankpay.android";
    public static final String PACKAGE_NAME_MPOCKET = "kr.co.samsungcard.mpocket";

    // Activity Result
    public static final int RESULT_CHANGED_DATE = Activity.RESULT_FIRST_USER + 1;
    public static final int RESULT_ARROUND_SEARCH_LIST = RESULT_CHANGED_DATE + 1;

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
    public static final String NAME_INTENT_EXTRA_DATA_ISOVERSEAS = "isOverseas";
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
    public static final String NAME_INTENT_EXTRA_DATA_TICKETINDEX = "ticketIndex";
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
    public static final String NAME_INTENT_EXTRA_DATA_PAYMENTINFORMATION = "paymentInformation";
    public static final String NAME_INTENT_EXTRA_DATA_PRICE = "price";
    public static final String NAME_INTENT_EXTRA_DATA_DISCOUNTPRICE = "discountPrice";
    public static final String NAME_INTENT_EXTRA_DATA_CALENDAR_FLAG = "calendarFlag";
    public static final String NAME_INTENT_EXTRA_DATA_ADDRESS = "address";
    public static final String NAME_INTENT_EXTRA_DATA_PLACECURATION = "placeCuration";
    public static final String NAME_INTENT_EXTRA_DATA_ENTRY_INDEX = "entryIndex";
    public static final String NAME_INTENT_EXTRA_DATA_IS_SHOW_ORIGINALPRICE = "isShowOriginalPrice";
    public static final String NAME_INTENT_EXTRA_DATA_LOCATION = "location";
    public static final String NAME_INTENT_EXTRA_DATA_LIST_COUNT = "listCount";
    public static final String NAME_INTENT_EXTRA_DATA_CALL_BY_SCREEN = "callByScreen";
    public static final String NAME_INTENT_EXTRA_DATA_IS_DAILYCHOICE = "dailychoice";
    public static final String NAME_INTENT_EXTRA_DATA_RATING_VALUE = "ratingValue";
    public static final String NAME_INTENT_EXTRA_DATA_GRADE = "grade";
    public static final String NAME_INTENT_EXTRA_DATA_FROM_MAP = "fromMap";
    public static final String NAME_INTENT_EXTRA_DATA_BIRTHDAY = "birthday";
    public static final String NAME_INTENT_EXTRA_DATA_IS_CHANGE_WISHLIST = "isChangeWishList";

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
    public static final int CODE_REQUEST_ACTIVITY_REGISTERCREDITCARD_AND_PAYMENT = 12;
    public static final int CODE_REQUEST_ACTIVITY_CREDITCARD_MANAGER = 13;
    public static final int CODE_REQUEST_ACTIVITY_REGIONLIST = 14;
    public static final int CODE_REQUEST_ACTIVITY_USERINFO_UPDATE = 15;
    public static final int CODE_REQUEST_ACTIVITY_VIRTUAL_BOOKING_DETAIL = 16;
    public static final int CODE_REQUEST_ACTIVITY_PLACE_DETAIL = 20;
    public static final int CODE_REQUEST_ACTIVITY_SATISFACTION_HOTEL = 21;
    public static final int CODE_REQUEST_ACTIVITY_SATISFACTION_GOURMET = 22;
    public static final int CODE_REQUEST_ACTIVITY_CALENDAR = 30;
    public static final int CODE_REQUEST_ACTIVITY_STAYCURATION = 31;
    public static final int CODE_REQUEST_ACTIVITY_EVENTWEB = 32;
    public static final int CODE_REQUEST_ACTIVITY_SEARCH = 33;
    public static final int CODE_REQUEST_ACTIVITY_IMAGELIST = 34;
    public static final int CODE_REQUEST_ACTIVITY_ZOOMMAP = 35;
    public static final int CODE_REQUEST_ACTIVITY_SHAREKAKAO = 36;
    public static final int CODE_REQUEST_ACTIVITY_GOURMETCURATION = 37;
    public static final int CODE_REQUEST_ACTIVITY_PERMISSION_MANAGER = 38;
    public static final int CODE_REQUEST_ACTIVITY_EXTERNAL_MAP = 39;
    public static final int CODE_REQUEST_ACTIVITY_SEARCH_RESULT = 40;
    public static final int CODE_REQUEST_ACTIVITY_LOGIN_BY_COUPON = 41;
    public static final int CODE_REQUEST_ACTIVITY_DOWNLOAD_COUPON = 42;
    public static final int CODE_REQUEST_ACTIVITY_COUPONLIST = 43;
    public static final int CODE_REQUEST_ACTIVITY_NOTICELIST = 44;
    public static final int CODE_REQUEST_ACTIVITY_REGISTER_COUPON = 45;
    public static final int CODE_REQUEST_ACTIVITY_REGISTERCREDITCARD = 46;
    public static final int CODE_REQUEST_ACTIVITY_TERMS_AND_POLICY = 47;
    public static final int CODE_REQUEST_ACTIVITY_FAQ = 48;
    public static final int CODE_REQUEST_ACTIVITY_FEEDBACK = 49;
    public static final int CODE_REQUEST_ACTIVITY_RECENTPLACE = 50;
    public static final int CODE_REQUEST_ACTIVITY_CONTACT_US = 51;
    public static final int CODE_REQUEST_ACTIVITY_LOGIN_BY_DETAIL_WISHLIST = 52;
    public static final int CODE_REQUEST_ACTIVITY_COLLECTION = 53;

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
    public static final int CODE_RESULT_ACTIVITY_HOME = 303;
    public static final int CODE_RESULT_ACTIVITY_SEARCHRESULT_KEYWORD = 304;
    public static final int CODE_RESULT_ACTIVITY_REFRESH = 305;
    public static final int CODE_RESULT_ACTIVITY_STAY_LIST = 306;
    public static final int CODE_RESULT_ACTIVITY_GOURMET_LIST = 307;
    public static final int CODE_RESULT_ACTIVITY_STAY_AUTOREFUND = 308;

    // 예약 리스트에서
    public static final int CODE_PAY_TYPE_CARD_COMPLETE = 10;
    public static final int CODE_PAY_TYPE_ACCOUNT_WAIT = 20;
    public static final int CODE_PAY_TYPE_ACCOUNT_COMPLETE = 21;

    // 퍼미션 관련
    public static final int REQUEST_CODE_PERMISSIONS_ACCESS_FINE_LOCATION = 10;
    public static final int REQUEST_CODE_PERMISSIONS_READ_PHONE_STATE = 11;

    public static final int REQUEST_CODE_APPLICATION_DETAILS_SETTINGS = 1000;


    // 리스트 페이지 사이즈
    public static final int PAGENATION_LIST_SIZE = 200;
}
