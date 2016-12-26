package com.twoheart.dailyhotel.util;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DailyDeepLink
{
    public enum SearchType
    {
        NONE,
        LOCATION
    }

    private static final String HOST_DAILYHOTEL = "dailyhotel.co.kr";
    private static final String HOST_KAKAOLINK = "kakaolink";

    private static final String PARAM_V2_VIEW = "view"; // view
    private static final String PARAM_V2_INDEX = "idx"; // index
    private static final String PARAM_V2_DATE = "date"; // date
    private static final String PARAM_V2_NIGHTS = "nights"; // nights

    private static final String HOTEL_V2_LIST = "hotel"; // hotel
    private static final String GOURMET_V2_LIST = "gourmet"; // gourmet
    private static final String BOOKING_V2_LIST = "bookings"; // bookings
    private static final String EVENT_V2_LIST = "event"; // event

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

    private static final String PARAM_V4_RECOMMENDER_CODE = "rc"; // 추천인 코드
    private static final String PARAM_V4_DATE_PLUS = "dp"; // 오늘 날짜에 더해줄 일
    private static final String PARAM_V4_SORTING = "s"; // lp (낮은 가격). hp (높은 가격), r (만족도)

    private static final String PARAM_V5_EVENT_NAME = "en"; // 이벤트 이름
    private static final String PARAM_V5_CALENDAR_FLAG = "cal"; // 0: 달력을 띄우지 않는다.(디폴트), 1 : 달력 띄운다.

    private static final String PARAM_V6_WORD = "w"; // 검색어
    private static final String PARAM_V6_LATITUDE = "lat"; //
    private static final String PARAM_V6_LONGITUDE = "lng";
    private static final String PARAM_V6_RADIUS = "rd"; // km

    private static final String PARAM_V7_TITLE = "t"; // 타이틀
    private static final String PARAM_V7_RESERVATION_INDEX = "ri"; // 예약 인덱스
    private static final String PARAM_V7_PLACE_TYPE = "pt"; // stay, gourmet, all
    private static final String PARAM_V7_NOTICE_INDEX = "ni"; // 공지사항 인덱스

    private static final String PARAM_V9_QUERY = "qr"; // 검색 쿼리
    private static final String PARAM_V9_OPEN_TICKEt_INDEX = "oti"; // 스테이/고메 메뉴 오픈시에 해당 인덱스
    private static final String PARAM_V9_QUERY_TYPE = "qt"; // 쿼리 방식
    private static final String PARAM_V9_TITLE_IMAGE_URL = "tiu"; // 타이틀 이미지 URL

    private static final String PARAM_V10_START_DATE = "sd"; // 캘린더 시작 날짜
    private static final String PARAM_V10_END_DATE = "ed"; // 캘린더 끝날짜

    private static final String VALUE_V4_SORTING_LOW_TO_HIGH = "lp";
    private static final String VALUE_V4_SORTING_HIGH_TO_LOW = "hp";
    private static final String VALUE_V4_SORTING_SATISFACTION = "r";

    private static final String HOTEL_V3_LIST = "hl"; // 호텔리스트
    private static final String HOTEL_V3_DETAIL = "hd"; // 호텔 상세
    //    private static final String HOTEL_V3_REGION_LIST = "hrl"; // 호텔 지역 리스트 (deprecated)
    private static final String HOTEL_V3_EVENT_BANNER_WEB = "hebw"; // 이벤트 배너 웹
    private static final String HOTEL_V6_SEARCH = "hs"; // 호텔 검색화면
    private static final String HOTEL_V6_SEARCH_RESULT = "hsr"; // 호텔 검색 결과 화면

    private static final String GOURMET_V3_LIST = "gl"; // 고메 리스트
    private static final String GOURMET_V3_DETAIL = "gd"; // 고메 상세
    //    private static final String GOURMET_V3_REGION_LIST = "grl"; // 고메 지역 리스트 (deprecated)
    private static final String GOURMET_V3_EVENT_BANNER_WEB = "gebw"; // 이벤트 배너 웹
    private static final String GOURMET_V6_SEARCH = "gs"; // 고메 검색화면
    private static final String GOURMET_V6_SEARCH_RESULT = "gsr"; // 고메 검색 결과 화면

    private static final String BONUS_V3 = "b"; // 적립금
    private static final String EVENT_V3_LIST = "el"; // 이벤트 리스트
    private static final String BOOKING_V3_LIST = "bl"; // 예약 리스트

    private static final String SINGUP_V4 = "su"; // 회원 가입 화면

    private static final String COUPON_V5_LIST = "cl"; // 쿠폰 리스트
    private static final String EVENT_V5_DETAIL = "ed";
    private static final String INFORMATION_V5 = "m"; // 더보기 화면
    private static final String RECOMMEND_FRIEND_V5 = "rf"; // 친구 추천하기 화면

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
    private static final String COLECTION_VIWE_V9 = "cv"; // 모아보기 화면

    private static final String PARAM_V10_BASE_URL = "baseUrl"; // 서버 BASE URL 변경

    private static final int MINIMUM_VERSION_CODE = 2;
    private static final int MAXIMUM_VERSION_CODE = 11;

    private static DailyDeepLink mInstance;

    private Uri mDeepLinkUri;
    private Map<String, String> mParams;
    private int mVersionCode;

    public static synchronized DailyDeepLink getInstance()
    {
        if (mInstance == null)
        {
            mInstance = new DailyDeepLink();
        }

        return mInstance;
    }

    private DailyDeepLink()
    {
        mParams = new HashMap<>();
    }

    /**
     * 꼭 setDeepLink 후에 호출해야한다
     *
     * @return
     */
    public boolean isValidateLink()
    {
        return mDeepLinkUri != null;
    }

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

        if (HOST_DAILYHOTEL.equalsIgnoreCase(host) == false && HOST_KAKAOLINK.equalsIgnoreCase(host) == false)
        {
            clear();
            return;
        }

        decodingLink(uri);
    }

    public String getDeepLink()
    {
        return mDeepLinkUri.toString();
    }

    public void clear()
    {
        mVersionCode = 0;
        mDeepLinkUri = null;
        mParams.clear();
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // Version 11
    public String getBaseUrl()
    {
        String value;

        if (mVersionCode >= 11)
        {
            value = mParams.get(PARAM_V10_BASE_URL);
        } else
        {
            value = null;
        }

        return value;
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // Version 10
    public String getStartDate()
    {
        String value;

        if (mVersionCode >= 10)
        {
            value = mParams.get(PARAM_V10_START_DATE);
        } else
        {
            value = null;
        }

        return value;
    }

    public String getEndDate()
    {
        String value;

        if (mVersionCode >= 10)
        {
            value = mParams.get(PARAM_V10_END_DATE);
        } else
        {
            value = null;
        }

        return value;
    }


    ///////////////////////////////////////////////////////////////////////////////////
    // Version 9
    ///////////////////////////////////////////////////////////////////////////////////
    public boolean isWishlistHotelView()
    {
        String view = getView();

        if (mVersionCode >= 9)
        {
            return WISHLIST_HOTEL_V9.equalsIgnoreCase(view);
        } else
        {
            return false;
        }
    }

    public boolean isWishlistGourmetView()
    {
        String view = getView();

        if (mVersionCode >= 9)
        {
            return WISHLIST_GOURMET_V9.equalsIgnoreCase(view);
        } else
        {
            return false;
        }
    }

    public boolean isCollectionView()
    {
        String view = getView();

        if (mVersionCode >= 9)
        {
            return COLECTION_VIWE_V9.equalsIgnoreCase(view);
        } else
        {
            return false;
        }
    }

    public int getOpenTicketIndex()
    {
        int index = 0;

        if (mVersionCode >= 9)
        {
            String value = mParams.get(PARAM_V9_OPEN_TICKEt_INDEX);

            if (Util.isTextEmpty(value) == false)
            {
                try
                {
                    index = Integer.parseInt(value);
                } catch (NumberFormatException e)
                {
                }
            }
        }

        return index;
    }

    public String getQueryType()
    {
        String value;

        if (mVersionCode >= 9)
        {
            value = mParams.get(PARAM_V9_QUERY_TYPE);
        } else
        {
            value = null;
        }

        return value;
    }

    public String getQuery()
    {
        String value;

        if (mVersionCode >= 9)
        {
            value = mParams.get(PARAM_V9_QUERY);
        } else
        {
            value = null;
        }

        return value;
    }

    public String getTitleImageUrl()
    {
        String value;

        if (mVersionCode >= 9)
        {
            value = mParams.get(PARAM_V9_TITLE_IMAGE_URL);
        } else
        {
            value = null;
        }

        return value;
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // Version 8
    ///////////////////////////////////////////////////////////////////////////////////
    public boolean isRecentlyWatchHotelView()
    {
        String view = getView();

        if (mVersionCode >= 8)
        {
            return RECENTLY_WATCH_HOTEL_V8.equalsIgnoreCase(view);
        } else
        {
            return false;
        }
    }

    public boolean isRecentlyWatchGourmetView()
    {
        String view = getView();

        if (mVersionCode >= 8)
        {
            return RECENTLY_WATCH_GOURMET_V8.equalsIgnoreCase(view);
        } else
        {
            return false;
        }
    }

    public boolean isFAQView()
    {
        String view = getView();

        if (mVersionCode >= 8)
        {
            return FAQ_V8.equalsIgnoreCase(view);
        } else
        {
            return false;
        }
    }

    public boolean isTermsNPolicyView()
    {
        String view = getView();

        if (mVersionCode >= 8)
        {
            return TERMS_N_POLICY_V8.equalsIgnoreCase(view);
        } else
        {
            return false;
        }
    }

    public boolean isProfileView()
    {
        String view = getView();

        if (mVersionCode >= 8)
        {
            return PROFILE_V8.equalsIgnoreCase(view);
        } else
        {
            return false;
        }
    }

    public boolean isProfileBirthdayView()
    {
        String view = getView();

        if (mVersionCode >= 8)
        {
            return PROFILE_BIRTHDAY_V8.equalsIgnoreCase(view);
        } else
        {
            return false;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // Version 7
    ///////////////////////////////////////////////////////////////////////////////////
    public boolean isRegisterCouponView()
    {
        String view = getView();

        if (mVersionCode >= 7)
        {
            return REGISTER_COUPON_V7.equalsIgnoreCase(view);
        } else
        {
            return false;
        }
    }

    public boolean isBookingDetailView()
    {
        String view = getView();

        if (mVersionCode >= 7)
        {
            return BOOKING_DETAIL_V7.equalsIgnoreCase(view);
        } else
        {
            return false;
        }
    }

    public boolean isNoticeDetailView()
    {
        String view = getView();

        if (mVersionCode >= 7)
        {
            return NOTICE_DETAIL_V7.equalsIgnoreCase(view);
        } else
        {
            return false;
        }
    }

    public String getTitle()
    {
        String value;

        if (mVersionCode >= 7)
        {
            value = mParams.get(PARAM_V7_TITLE);
        } else
        {
            value = null;
        }

        return value;
    }

    public int getReservationIndex()
    {
        int reservationIndex = 0;

        if (mVersionCode >= 7)
        {
            String value = mParams.get(PARAM_V7_RESERVATION_INDEX);

            if (Util.isTextEmpty(value) == false)
            {
                try
                {
                    reservationIndex = Integer.parseInt(value);
                } catch (NumberFormatException e)
                {
                }
            }
        }

        return reservationIndex;
    }

    public String getPlaceType()
    {
        String value;

        if (mVersionCode >= 7)
        {
            value = mParams.get(PARAM_V7_PLACE_TYPE);
        } else
        {
            value = null;
        }

        return value;
    }

    public int getNoticeIndex()
    {
        int noticeIndex = 0;

        if (mVersionCode >= 7)
        {
            String value = mParams.get(PARAM_V7_NOTICE_INDEX);

            if (Util.isTextEmpty(value) == false)
            {
                try
                {
                    noticeIndex = Integer.parseInt(value);
                } catch (NumberFormatException e)
                {
                }
            }
        }

        return noticeIndex;
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // Version 6
    ///////////////////////////////////////////////////////////////////////////////////
    public boolean isHotelSearchView()
    {
        String view = getView();

        if (mVersionCode >= 6)
        {
            return HOTEL_V6_SEARCH.equalsIgnoreCase(view);
        } else
        {
            return false;
        }
    }

    public boolean isHotelSearchResultView()
    {
        String view = getView();

        if (mVersionCode >= 6)
        {
            return HOTEL_V6_SEARCH_RESULT.equalsIgnoreCase(view);
        } else
        {
            return false;
        }
    }

    public boolean isGourmetSearchView()
    {
        String view = getView();

        if (mVersionCode >= 6)
        {
            return GOURMET_V6_SEARCH.equalsIgnoreCase(view);
        } else
        {
            return false;
        }
    }

    public boolean isGourmetSearchResultView()
    {
        String view = getView();

        if (mVersionCode >= 6)
        {
            return GOURMET_V6_SEARCH_RESULT.equalsIgnoreCase(view);
        } else
        {
            return false;
        }
    }

    public String getSearchWord()
    {
        String value;

        if (mVersionCode >= 6)
        {
            value = mParams.get(PARAM_V6_WORD);
        } else
        {
            value = null;
        }

        return value;
    }

    public SearchType getSearchLocationType()
    {
        SearchType type = SearchType.NONE;

        if (mVersionCode >= 6)
        {
            String lat = mParams.get(PARAM_V6_LATITUDE);
            String lng = mParams.get(PARAM_V6_LONGITUDE);

            if (Util.isTextEmpty(lat, lng) == false)
            {
                type = SearchType.LOCATION;
            }
        }

        return type;
    }

    public LatLng getLatLng()
    {
        LatLng latLng = null;

        if (mVersionCode >= 6)
        {
            String lat = mParams.get(PARAM_V6_LATITUDE);
            String lng = mParams.get(PARAM_V6_LONGITUDE);

            if (Util.isTextEmpty(lat, lng) == false)
            {
                latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
            }
        }

        return latLng;
    }

    public double getRadius()
    {
        // 기본값 10.0km
        double radius = 10.0d;

        if (mVersionCode >= 6)
        {
            String value = mParams.get(PARAM_V6_RADIUS);

            if (Util.isTextEmpty(value) == false)
            {
                try
                {
                    radius = Double.parseDouble(value);
                } catch (NumberFormatException e)
                {
                }
            }
        }

        return radius;
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // Version 5
    ///////////////////////////////////////////////////////////////////////////////////
    public boolean isCouponView()
    {
        String view = getView();

        if (mVersionCode >= 5)
        {
            return COUPON_V5_LIST.equalsIgnoreCase(view);
        } else
        {
            return false;
        }
    }

    public boolean isEventDetailView()
    {
        String view = getView();

        if (mVersionCode >= 5)
        {
            return EVENT_V5_DETAIL.equalsIgnoreCase(view);
        } else
        {
            return false;
        }
    }

    public boolean isInformationView()
    {
        String view = getView();

        if (mVersionCode >= 5)
        {
            return INFORMATION_V5.equalsIgnoreCase(view);
        } else
        {
            return false;
        }
    }

    public boolean isRecommendFriendView()
    {
        String view = getView();

        if (mVersionCode >= 5)
        {
            return RECOMMEND_FRIEND_V5.equalsIgnoreCase(view);
        } else
        {
            return false;
        }
    }

    public String getEventName()
    {
        String value;

        if (mVersionCode >= 5)
        {
            value = mParams.get(PARAM_V5_EVENT_NAME);
        } else
        {
            value = null;
        }

        return value;
    }

    public boolean isShowCalendar()
    {
        if (mVersionCode >= 5)
        {
            String value = mParams.get(PARAM_V5_CALENDAR_FLAG);

            if (Util.isTextEmpty(value) == false)
            {
                try
                {
                    return Integer.parseInt(value) == 1;
                } catch (NumberFormatException e)
                {
                    return false;
                }
            }
        }

        return false;
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // Version 4
    ///////////////////////////////////////////////////////////////////////////////////
    public boolean isSingUpView()
    {
        String view = getView();

        if (mVersionCode >= 4)
        {
            return SINGUP_V4.equalsIgnoreCase(view);
        } else
        {
            return false;
        }
    }

    public String getRecommenderCode()
    {
        String value;

        if (mVersionCode >= 4)
        {
            value = mParams.get(PARAM_V4_RECOMMENDER_CODE);
        } else
        {
            value = null;
        }

        return value;
    }

    public int getDatePlus()
    {
        String value;

        if (mVersionCode >= 4)
        {
            value = mParams.get(PARAM_V4_DATE_PLUS);

            if (Util.isTextEmpty(value) == false)
            {
                try
                {
                    return Integer.parseInt(value);
                } catch (NumberFormatException e)
                {
                    return -1;
                }
            }
        }

        return -1;
    }

    public Constants.SortType getSorting()
    {
        String value;

        if (mVersionCode >= 4)
        {
            value = mParams.get(PARAM_V4_SORTING);

            if (Util.isTextEmpty(value) == false)
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
        String value;

        if (mVersionCode >= 3)
        {
            value = mParams.get(PARAM_V3_VIEW);
        } else
        {
            value = mParams.get(PARAM_V2_VIEW);
        }

        return value;
    }

    public boolean isHotelView()
    {
        String view = getView();

        if (mVersionCode >= 3)
        {
            return HOTEL_V3_LIST.equalsIgnoreCase(view)//
                || HOTEL_V3_DETAIL.equalsIgnoreCase(view)//
                //                || HOTEL_V3_REGION_LIST.equalsIgnoreCase(view)//
                || HOTEL_V3_EVENT_BANNER_WEB.equalsIgnoreCase(view)//
                || HOTEL_V6_SEARCH.equalsIgnoreCase(view) //
                || HOTEL_V6_SEARCH_RESULT.equalsIgnoreCase(view);
        } else
        {
            return HOTEL_V2_LIST.equalsIgnoreCase(view);
        }
    }

    public boolean isHotelListView()
    {
        String view = getView();

        if (mVersionCode >= 3)
        {
            return HOTEL_V3_LIST.equalsIgnoreCase(view);
        } else
        {
            return false;
        }
    }

    public boolean isHotelDetailView()
    {
        String view = getView();

        if (mVersionCode >= 3)
        {
            return HOTEL_V3_DETAIL.equalsIgnoreCase(view);
        } else
        {
            return HOTEL_V2_LIST.equalsIgnoreCase(view)//
                && Util.isTextEmpty(mParams.get(PARAM_V2_INDEX)) == false//
                && Util.isTextEmpty(mParams.get(PARAM_V2_DATE)) == false//
                && Util.isTextEmpty(mParams.get(PARAM_V2_NIGHTS)) == false;
        }
    }

    //    public boolean isHotelRegionListView()
    //    {
    //        String view = getView();
    //
    //        if (mVersionCode >= 3)
    //        {
    //            return HOTEL_V3_REGION_LIST.equalsIgnoreCase(view);
    //        } else
    //        {
    //            return false;
    //        }
    //    }

    public boolean isHotelEventBannerWebView()
    {
        String view = getView();

        if (mVersionCode >= 3)
        {
            return HOTEL_V3_EVENT_BANNER_WEB.equalsIgnoreCase(view);
        } else
        {
            return false;
        }
    }

    public boolean isGourmetView()
    {
        String view = getView();

        if (mVersionCode >= 3)
        {
            return GOURMET_V3_LIST.equalsIgnoreCase(view) //
                || GOURMET_V3_DETAIL.equalsIgnoreCase(view) //
                //                || GOURMET_V3_REGION_LIST.equalsIgnoreCase(view) //
                || GOURMET_V3_EVENT_BANNER_WEB.equalsIgnoreCase(view) //
                || GOURMET_V6_SEARCH.equalsIgnoreCase(view) //
                || GOURMET_V6_SEARCH_RESULT.equalsIgnoreCase(view); //
        } else
        {
            return GOURMET_V2_LIST.equalsIgnoreCase(view);
        }
    }

    public boolean isGourmetListView()
    {
        String view = getView();

        if (mVersionCode >= 3)
        {
            return GOURMET_V3_LIST.equalsIgnoreCase(view);
        } else
        {
            return false;
        }
    }

    public boolean isGourmetDetailView()
    {
        String view = getView();

        if (mVersionCode >= 3)
        {
            return GOURMET_V3_DETAIL.equalsIgnoreCase(view);
        } else
        {
            return GOURMET_V2_LIST.equalsIgnoreCase(view)//
                && Util.isTextEmpty(mParams.get(PARAM_V2_INDEX)) == false//
                && Util.isTextEmpty(mParams.get(PARAM_V2_DATE)) == false;
        }
    }

    //    public boolean isGourmetRegionListView()
    //    {
    //        String view = getView();
    //
    //        if (mVersionCode >= 3)
    //        {
    //            return GOURMET_V3_REGION_LIST.equalsIgnoreCase(view);
    //        } else
    //        {
    //            return false;
    //        }
    //    }

    public boolean isGourmetEventBannerWebView()
    {
        String view = getView();

        if (mVersionCode >= 3)
        {
            return GOURMET_V3_EVENT_BANNER_WEB.equalsIgnoreCase(view);
        } else
        {
            return false;
        }
    }

    public boolean isBookingView()
    {
        String view = getView();

        if (mVersionCode >= 3)
        {
            return BOOKING_V3_LIST.equalsIgnoreCase(view);
        } else
        {
            return BOOKING_V2_LIST.equalsIgnoreCase(view);
        }
    }

    public boolean isBonusView()
    {
        String view = getView();

        if (mVersionCode >= 3)
        {
            return BONUS_V3.equalsIgnoreCase(view);
        } else
        {
            return false;
        }
    }

    public boolean isEventView()
    {
        String view = getView();

        if (mVersionCode >= 3)
        {
            return EVENT_V3_LIST.equalsIgnoreCase(view);
        } else
        {
            return EVENT_V2_LIST.equalsIgnoreCase(view);
        }
    }

    public String getIndex()
    {
        String value;

        if (mVersionCode >= 3)
        {
            value = mParams.get(PARAM_V3_INDEX);
        } else
        {
            value = mParams.get(PARAM_V2_INDEX);
        }

        return value;
    }

    public String getNights()
    {
        String value;

        if (mVersionCode >= 3)
        {
            value = mParams.get(PARAM_V3_NIGHTS);
        } else
        {
            value = mParams.get(PARAM_V2_NIGHTS);
        }

        return value;
    }

    public String getDate()
    {
        String value;

        if (mVersionCode >= 3)
        {
            value = mParams.get(PARAM_V3_DATE);
        } else
        {
            value = mParams.get(PARAM_V2_DATE);
        }

        return value;
    }

    public String getUrl()
    {
        String value;

        if (mVersionCode >= 3)
        {
            value = mParams.get(PARAM_V3_URL);
        } else
        {
            value = null;
        }

        return value;
    }

    public String getProvinceIndex()
    {
        String value;

        if (mVersionCode >= 3)
        {
            value = mParams.get(PARAM_V3_PROVINCE_INDEX);
        } else
        {
            value = null;
        }

        return value;
    }

    public String getAreaIndex()
    {
        String value;

        if (mVersionCode >= 3)
        {
            value = mParams.get(PARAM_V3_AREA_INDEX);
        } else
        {
            value = null;
        }

        return value;
    }

    public boolean getIsOverseas()
    {
        boolean value = false;

        if (mVersionCode >= 3)
        {
            String isOverseas = mParams.get(PARAM_V3_REGION_ISOVERSEA);
            if ("0".equalsIgnoreCase(isOverseas) == true)
            {
                value = false;
            } else if ("1".equalsIgnoreCase(isOverseas) == true)
            {
                value = true;
            }
        } else
        {
            value = false;
        }

        return value;
    }

    public String getCategoryCode()
    {
        String value;

        if (mVersionCode >= 3)
        {
            value = mParams.get(PARAM_V3_CATEGORY_CODE);
        } else
        {
            value = null;
        }

        return value;
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // Version 2
    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////

    private boolean putParams(Uri uri, String param)
    {
        String value = uri.getQueryParameter(param);

        if (Util.isTextEmpty(value) == false)
        {
            mParams.put(param, value);
            return true;
        } else
        {
            return false;
        }
    }


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
        mParams.clear();

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
        if (Util.isTextEmpty(versionString) == true)
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
