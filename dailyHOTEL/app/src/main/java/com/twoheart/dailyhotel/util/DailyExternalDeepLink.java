package com.twoheart.dailyhotel.util;

import android.net.Uri;

import com.daily.base.util.DailyTextUtils;
import com.google.android.gms.maps.model.LatLng;

import java.util.Set;

public class DailyExternalDeepLink extends DailyDeepLink
{
    public enum SearchType
    {
        NONE,
        LOCATION
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // DAILYHOTEL EXTERNAL DEEP LINK
    ///////////////////////////////////////////////////////////////////////////////////

    // View

    //    private static final String HOTEL_V2_LIST = "hotel"; // hotel
    //    private static final String GOURMET_V2_LIST = "gourmet"; // gourmet
    //    private static final String BOOKING_V2_LIST = "bookings"; // bookings
    //    private static final String EVENT_V2_LIST = "event"; // event

    private static final String HOTEL_V3_LIST = "hl"; // 호텔리스트
    private static final String HOTEL_V3_DETAIL = "hd"; // 호텔 상세
    //    private static final String HOTEL_V3_REGION_LIST = "hrl"; // 호텔 지역 리스트 (deprecated)
    //    private static final String HOTEL_V3_EVENT_BANNER_WEB = "hebw"; // 이벤트 배너 웹
    //    private static final String HOTEL_V6_SEARCH = "hs"; // 호텔 검색화면
    private static final String HOTEL_V6_SEARCH_RESULT = "hsr"; // 호텔 검색 결과 화면

    private static final String GOURMET_V3_LIST = "gl"; // 고메 리스트
    private static final String GOURMET_V3_DETAIL = "gd"; // 고메 상세
    //    private static final String GOURMET_V3_REGION_LIST = "grl"; // 고메 지역 리스트 (deprecated)
    //    private static final String GOURMET_V3_EVENT_BANNER_WEB = "gebw"; // 이벤트 배너 웹
    //    private static final String GOURMET_V6_SEARCH = "gs"; // 고메 검색화면
    private static final String GOURMET_V6_SEARCH_RESULT = "gsr"; // 고메 검색 결과 화면

    private static final String BONUS_V3 = "b"; // 적립금
    private static final String EVENT_V3_LIST = "el"; // 이벤트 리스트
    private static final String BOOKING_V3_LIST = "bl"; // 예약 리스트

    private static final String SINGUP_V4 = "su"; // 회원 가입 화면

    private static final String COUPON_V5_LIST = "cl"; // 쿠폰 리스트
    private static final String EVENT_V5_DETAIL = "ed";
    private static final String INFORMATION_V5 = "m"; // 더보기 화면

    private static final String REGISTER_COUPON_V7 = "cr"; // 쿠폰 등록 화면
    private static final String BOOKING_DETAIL_V7 = "bd"; // 예약 상세화면
    private static final String NOTICE_DETAIL_V7 = "nd"; // 공지사항 상세화면

    private static final String RECENTLY_WATCH_HOTEL_V8 = "rwh"; // 최근 본 호텔
    private static final String RECENTLY_WATCH_GOURMET_V8 = "rwg"; // 최근 본 고메
    private static final String FAQ_V8 = "faq"; // 자주 묻는 질문
    private static final String PROFILE_V8 = "pr"; // 프로필 화면
    private static final String PROFILE_BIRTHDAY_V8 = "prbd"; // 프로픨 화면 생일 정보 입력
    private static final String TERMS_N_POLICY_V8 = "tnp"; // 약관 및 정책

    private static final String WISHLIST_HOTEL_V9 = "wlh"; // 위시리스트 호텔
    private static final String WISHLIST_GOURMET_V9 = "wlg"; // 위시리스트 고메
    //    private static final String COLECTION_VIWE_V9 = "cv"; // 모아보기 화면

    private static final String MYDAILY_V12 = "md"; // 마이 데일리 화면
    private static final String HOME_V12_EVENT_DETAIL = "hed"; // 홈의 이벤트 상세화면
    private static final String HOME_V12_RECOMMENDATION_PLACE_LIST = "hrpl"; // 홈의 데일리 추천 -> 상세 리스트

    //    private static final String STAMP_V14 = "stamp"; // 스탬프.
    private static final String STAY_V16_SHORTCUT_LIST = "scl"; // 스테이 숏컷 리스트
    private static final String STAY_OUTBOUND_V19_SEARCH_RESULT_LIST = "sosrl"; // 해외 호텔 검색 결과 목록
    private static final String CAMPAIGN_TAG_LIST = "ctl"; // 캠패인 태그

    private static final String PLACE_V20_DETAIL = "pd"; // place detail

    private static final String REWARD_V21 = "reward"; // 마이데일리 데일리리워드
    private static final String SEARCH_HOME_V23 = "searchHome"; // 검색 홈
    private static final String LOGIN_V24 = "login"; // 검색 홈


    // Param

    //    private static final String PARAM_V2_VIEW = "view"; // view
    //    private static final String PARAM_V2_INDEX = "idx"; // index
    //    private static final String PARAM_V2_DATE = "date"; // date
    //    private static final String PARAM_V2_NIGHTS = "nights"; // nights

    private static final String PARAM_V3_VIEW = "v"; // view
    private static final String PARAM_V3_VERSION_CODE = "vc"; // version code
    private static final String PARAM_V3_DATE = "d"; // date
    private static final String PARAM_V3_NIGHTS = "n"; // nights
    private static final String PARAM_V3_URL = "url"; // url
    private static final String PARAM_V3_PROVINCE_INDEX = "pi"; // province index
    private static final String PARAM_V3_AREA_INDEX = "ai"; // area index
    private static final String PARAM_V3_INDEX = "i"; // hotel/gourmet index
    private static final String PARAM_V3_REGION_ISOVERSEA = "ios"; // isOverSea
    private static final String PARAM_V3_CATEGORY_CODE = "cc"; // category Code

    private static final String PARAM_V4_DATE_PLUS = "dp"; // 오늘 날짜에 더해줄 일
    private static final String PARAM_V4_SORTING = "s"; // lp (낮은 가격). hp (높은 가격), r (만족도)

    //    private static final String PARAM_V5_EVENT_NAME = "en"; // 이벤트 이름 --> "t" 로 통일 하도록 한다.
    private static final String PARAM_V5_CALENDAR_FLAG = "cal"; // 0: 달력을 띄우지 않는다.(디폴트), 1 : 달력 띄운다.

    private static final String PARAM_V6_WORD = "w"; // 검색어
    private static final String PARAM_V6_LATITUDE = "lat"; //
    private static final String PARAM_V6_LONGITUDE = "lng";
    private static final String PARAM_V6_RADIUS = "rd"; // km

    private static final String PARAM_V7_TITLE = "t"; // 타이틀
    private static final String PARAM_V7_RESERVATION_INDEX = "ri"; // 예약 인덱스
    private static final String PARAM_V7_PLACE_TYPE = "pt"; // stay, gourmet, all
    private static final String PARAM_V7_NOTICE_INDEX = "ni"; // 공지사항 인덱스

    //    private static final String PARAM_V9_QUERY = "qr"; // 검색 쿼리
    private static final String PARAM_V9_OPEN_TICKET_INDEX = "oti"; // 스테이/고메 메뉴 오픈시에 해당 인덱스
    private static final String PARAM_V9_QUERY_TYPE = "qt"; // 쿼리 방식
    //    private static final String PARAM_V9_TITLE_IMAGE_URL = "tiu"; // 타이틀 이미지 URL

    //    private static final String PARAM_V10_START_DATE = "sd"; // 캘린더 시작 날짜
    //    private static final String PARAM_V10_END_DATE = "ed"; // 캘린더 끝날짜

    private static final String VALUE_V4_SORTING_LOW_TO_HIGH = "lp";
    private static final String VALUE_V4_SORTING_HIGH_TO_LOW = "hp";
    private static final String VALUE_V4_SORTING_SATISFACTION = "r";

    private static final String PARAM_V10_BASE_URL = "baseUrl"; // 서버 BASE URL 변경
    private static final String PARAM_V10_BASE_OUTBOUND_URL = "baseOutBoundUrl"; // 서버 BASE URL 변경

    //    private static final String PARAM_V13_PRODUCT_INDEX = "pdi"; // 상품 인덱스.

    private static final String PARAM_V15_VR = "vr"; // vr

    private static final String PARAM_V19_CATEGORY_KEY = "ck"; // 해외 호텔 검색에서 사용되는 키값

    private static final String PARAM_V22_WEEK = "week";
    private static final String PARAM_V24_STATION_INDEX = "si";

    private static final String PARAM_V25_DESCRIPTION = "desc";
    private static final String PARAM_V25_IMAGE_URL = "iurl";
    private static final String PARAM_V25_AGGREGATION_ID = "agi";


    // Version
    private static final int MINIMUM_VERSION_CODE = 3;
    private static final int MAXIMUM_VERSION_CODE = 25;

    private int mVersionCode;

    public DailyExternalDeepLink(Uri uri)
    {
        super(uri);
    }

    @Override
    public void setDeepLink(Uri uri)
    {
        if (uri == null)
        {
            clear();
            return;
        }

        mDeepLinkUri = uri;

        String scheme = uri.getScheme();
        String host = uri.getHost();

        if (HOST_DAILYHOTEL.equalsIgnoreCase(host) == true || HOST_KAKAOLINK.equalsIgnoreCase(host) == true)
        {
            decodingLink(uri);
        } else
        {
            clear();
        }
    }

    public void clear()
    {
        super.clear();

        mVersionCode = 0;
    }


    private boolean equalsView(int version, String viewName)
    {
        if (DailyTextUtils.isTextEmpty(viewName) == true || mVersionCode < version)
        {
            return false;
        }

        return viewName.equalsIgnoreCase(getView());
    }

    private String getStringValue(int version, String valueName)
    {
        if (DailyTextUtils.isTextEmpty(valueName) == true || mVersionCode < version)
        {
            return null;
        }

        return mParamsMap.get(valueName);
    }

    private int getIntValue(int version, String valueName)
    {
        if (DailyTextUtils.isTextEmpty(valueName) == true || mVersionCode < version)
        {
            return 0;
        }

        int value = 0;

        String stringValue = mParamsMap.get(valueName);

        if (DailyTextUtils.isTextEmpty(stringValue) == false)
        {
            try
            {
                value = Integer.parseInt(stringValue);
            } catch (NumberFormatException e)
            {
            }
        }

        return value;
    }

    private double getDoubleValue(int version, String valueName)
    {
        if (DailyTextUtils.isTextEmpty(valueName) == true || mVersionCode < version)
        {
            return 0;
        }

        double value = 0;

        String stringValue = mParamsMap.get(valueName);

        if (DailyTextUtils.isTextEmpty(stringValue) == false)
        {
            try
            {
                value = Double.parseDouble(stringValue);
            } catch (NumberFormatException e)
            {
            }
        }

        return value;
    }

    private boolean hasParam(String paramName)
    {
        if (DailyTextUtils.isTextEmpty(paramName) == true)
        {
            return false;
        }

        return mParamsMap.containsKey(paramName);
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // Version 25 - 최소 동작 버전은 기존 hed(홈 이벤트 딥링크) 와 같음
    ///////////////////////////////////////////////////////////////////////////////////
    public String getDescription()
    {
        return getStringValue(12, PARAM_V25_DESCRIPTION);
    }

    public String getImageUrl()
    {
        return getStringValue(12, PARAM_V25_IMAGE_URL);
    }

    public String getAggregationId()
    {
        return getStringValue(25, PARAM_V25_AGGREGATION_ID);
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // Version 24
    ///////////////////////////////////////////////////////////////////////////////////

    public boolean isLoginView()
    {
        return equalsView(23, LOGIN_V24);
    }

    public int getStationIndex()
    {
        return getIntValue(24, PARAM_V24_STATION_INDEX);
    }

    public boolean hasStationIndexParam()
    {
        return hasParam(PARAM_V24_STATION_INDEX);
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // Version 23
    ///////////////////////////////////////////////////////////////////////////////////

    public boolean isSearchHomeView()
    {
        return equalsView(23, SEARCH_HOME_V23);
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // Version 22
    ///////////////////////////////////////////////////////////////////////////////////

    public String getWeek()
    {
        return getStringValue(22, PARAM_V22_WEEK);
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // Version 21
    ///////////////////////////////////////////////////////////////////////////////////

    public boolean isRewardView()
    {
        return equalsView(21, REWARD_V21);
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // Version 20
    ///////////////////////////////////////////////////////////////////////////////////

    public boolean isPlaceDetailView()
    {
        return equalsView(20, PLACE_V20_DETAIL);
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // Version 19
    ///////////////////////////////////////////////////////////////////////////////////

    public boolean isCampaignTagListView()
    {
        return equalsView(19, CAMPAIGN_TAG_LIST);
    }

    public boolean isStayOutboundSearchResultView()
    {
        return equalsView(19, STAY_OUTBOUND_V19_SEARCH_RESULT_LIST);
    }

    public String getCategoryKey()
    {
        return getStringValue(19, PARAM_V19_CATEGORY_KEY);
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // Version 16
    ///////////////////////////////////////////////////////////////////////////////////

    public boolean isShortcutView()
    {
        return equalsView(16, STAY_V16_SHORTCUT_LIST);
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // Version 15
    ///////////////////////////////////////////////////////////////////////////////////

    public boolean isShowVR()
    {
        return getIntValue(15, PARAM_V15_VR) == 1;
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // Version 14
    ///////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////
    // Version 13
    ///////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////
    // Version 12
    ///////////////////////////////////////////////////////////////////////////////////

    public boolean isMyDailyView()
    {
        return equalsView(12, MYDAILY_V12);
    }

    public boolean isHomeEventDetailView()
    {
        return equalsView(12, HOME_V12_EVENT_DETAIL);
    }

    public boolean isHomeRecommendationPlaceListView()
    {
        return equalsView(12, HOME_V12_RECOMMENDATION_PLACE_LIST);
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // Version 11
    ///////////////////////////////////////////////////////////////////////////////////

    public String getBaseUrl()
    {
        return getStringValue(11, PARAM_V10_BASE_URL);
    }

    public String getBaseOutBoundUrl()
    {
        return getStringValue(11, PARAM_V10_BASE_OUTBOUND_URL);
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // Version 10
    ///////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////
    // Version 9
    ///////////////////////////////////////////////////////////////////////////////////

    public boolean isWishListHotelView()
    {
        return equalsView(9, WISHLIST_HOTEL_V9);
    }

    public boolean isWishListGourmetView()
    {
        return equalsView(9, WISHLIST_GOURMET_V9);
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // Version 8
    ///////////////////////////////////////////////////////////////////////////////////

    public boolean isRecentlyWatchHotelView()
    {
        return equalsView(8, RECENTLY_WATCH_HOTEL_V8);
    }

    public boolean isRecentlyWatchGourmetView()
    {
        return equalsView(8, RECENTLY_WATCH_GOURMET_V8);
    }

    public boolean isFAQView()
    {
        return equalsView(8, FAQ_V8);
    }

    public boolean isTermsNPolicyView()
    {
        return equalsView(8, TERMS_N_POLICY_V8);
    }

    public boolean isProfileView()
    {
        return equalsView(8, PROFILE_V8);
    }

    public boolean isProfileBirthdayView()
    {
        return equalsView(8, PROFILE_BIRTHDAY_V8);
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // Version 7
    ///////////////////////////////////////////////////////////////////////////////////

    public boolean isRegisterCouponView()
    {
        return equalsView(7, REGISTER_COUPON_V7);
    }

    public boolean isBookingDetailView()
    {
        return equalsView(7, BOOKING_DETAIL_V7);
    }

    public boolean isNoticeDetailView()
    {
        return equalsView(7, NOTICE_DETAIL_V7);
    }

    public String getTitle()
    {
        return getStringValue(7, PARAM_V7_TITLE);
    }

    public int getReservationIndex()
    {
        return getIntValue(7, PARAM_V7_RESERVATION_INDEX);
    }

    public String getPlaceType()
    {
        return getStringValue(7, PARAM_V7_PLACE_TYPE);
    }

    public int getNoticeIndex()
    {
        return getIntValue(7, PARAM_V7_NOTICE_INDEX);
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // Version 6
    ///////////////////////////////////////////////////////////////////////////////////

    public boolean isStaySearchResultView()
    {
        return equalsView(6, HOTEL_V6_SEARCH_RESULT);
    }

    public boolean isGourmetSearchResultView()
    {
        return equalsView(6, GOURMET_V6_SEARCH_RESULT);
    }

    public String getSearchWord()
    {
        return getStringValue(6, PARAM_V6_WORD);
    }

    public LatLng getLatLng()
    {
        LatLng latLng = null;

        if (mVersionCode >= 6)
        {
            String lat = mParamsMap.get(PARAM_V6_LATITUDE);
            String lng = mParamsMap.get(PARAM_V6_LONGITUDE);

            if (DailyTextUtils.isTextEmpty(lat, lng) == false)
            {
                latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
            }
        }

        return latLng;
    }

    public double getRadius()
    {
        return getDoubleValue(6, PARAM_V6_RADIUS);
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // Version 5
    ///////////////////////////////////////////////////////////////////////////////////

    public boolean isCouponView()
    {
        return equalsView(5, COUPON_V5_LIST);
    }

    public boolean isEventDetailView()
    {
        return equalsView(5, EVENT_V5_DETAIL);
    }

    public boolean isInformationView()
    {
        return equalsView(5, INFORMATION_V5);
    }

    public boolean isShowCalendar()
    {
        return getIntValue(5, PARAM_V5_CALENDAR_FLAG) == 1;
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // Version 4
    ///////////////////////////////////////////////////////////////////////////////////

    public boolean isSingUpView()
    {
        return equalsView(4, SINGUP_V4);
    }

    public int getDatePlus()
    {
        return getIntValue(4, PARAM_V4_DATE_PLUS);
    }

    public Constants.SortType getSorting()
    {
        String value;

        if (mVersionCode >= 4)
        {
            value = mParamsMap.get(PARAM_V4_SORTING);

            if (DailyTextUtils.isTextEmpty(value) == false)
            {
                if (VALUE_V4_SORTING_LOW_TO_HIGH.equalsIgnoreCase(value) == true)
                {
                    return Constants.SortType.LOW_PRICE;
                } else if (VALUE_V4_SORTING_HIGH_TO_LOW.equalsIgnoreCase(value) == true)
                {
                    return Constants.SortType.HIGH_PRICE;
                } else if (VALUE_V4_SORTING_SATISFACTION.equalsIgnoreCase(value) == true)
                {
                    return Constants.SortType.SATISFACTION;
                }
            }
        }

        return Constants.SortType.DEFAULT;
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // Version 3
    ///////////////////////////////////////////////////////////////////////////////////

    private String getView()
    {
        return mParamsMap.get(PARAM_V3_VIEW);
    }

    public boolean isHotelListView()
    {
        return equalsView(3, HOTEL_V3_LIST);
    }

    public boolean isHotelDetailView()
    {
        return equalsView(3, HOTEL_V3_DETAIL);
    }

    public boolean isGourmetListView()
    {
        return equalsView(3, GOURMET_V3_LIST);
    }

    public boolean isGourmetDetailView()
    {
        return equalsView(3, GOURMET_V3_DETAIL);
    }

    public boolean isBookingView()
    {
        return equalsView(3, BOOKING_V3_LIST);
    }

    public boolean isBonusView()
    {
        return equalsView(3, BONUS_V3);
    }

    public boolean isEventView()
    {
        return equalsView(3, EVENT_V3_LIST);
    }

    public String getIndex()
    {
        return getStringValue(3, PARAM_V3_INDEX);
    }

    public String getNights()
    {
        return getStringValue(3, PARAM_V3_NIGHTS);
    }

    public String getDate()
    {
        return getStringValue(3, PARAM_V3_DATE);
    }

    public String getUrl()
    {
        return getStringValue(3, PARAM_V3_URL);
    }

    public int getProvinceIndex()
    {
        return getIntValue(3, PARAM_V3_PROVINCE_INDEX);
    }

    public int getAreaIndex()
    {
        return getIntValue(3, PARAM_V3_AREA_INDEX);
    }

    public boolean getIsOverseas()
    {
        return "1".equalsIgnoreCase(getStringValue(3, PARAM_V3_REGION_ISOVERSEA));
    }

    public String getCategoryCode()
    {
        return getStringValue(3, PARAM_V3_CATEGORY_CODE);
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // Version 2
    ///////////////////////////////////////////////////////////////////////////////////

    private boolean putVersionCode(int versionCode)
    {
        if (versionCode < MINIMUM_VERSION_CODE || versionCode > MAXIMUM_VERSION_CODE)
        {
            return false;
        } else
        {
            mVersionCode = versionCode;
            return true;
        }
    }

    private boolean decodingLink(Uri uri)
    {
        mParamsMap.clear();

        if (uri == null)
        {
            clear();
            return false;
        }

        Set<String> keySet = uri.getQueryParameterNames();
        if (keySet == null || keySet.isEmpty() == true)
        {
            clear();
            return false;
        }

        int versionCode;

        String versionString = uri.getQueryParameter(PARAM_V3_VERSION_CODE);
        if (DailyTextUtils.isTextEmpty(versionString) == true)
        {
            versionCode = MINIMUM_VERSION_CODE;
        } else
        {
            try
            {
                versionCode = Integer.parseInt(versionString);
            } catch (NumberFormatException e)
            {
                versionCode = -1;
            }
        }

        if (putVersionCode(versionCode) == false)
        {
            // 현재 버전 Deeplink 동작가능범위 초과!
            clear();
            return false;
        }

        for (String key : keySet)
        {
            putParams(uri, key);
        }

        return true;
    }
}
