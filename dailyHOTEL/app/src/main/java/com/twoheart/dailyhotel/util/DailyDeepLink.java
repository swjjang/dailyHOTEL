package com.twoheart.dailyhotel.util;

import android.net.Uri;

import java.util.HashMap;
import java.util.Map;

public class DailyDeepLink
{
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

    private static final String VALUE_V4_SORTING_LOW_TO_HIGH = "lp";
    private static final String VALUE_V4_SORTING_HIGH_TO_LOW = "hp";
    private static final String VALUE_V4_SORTING_SATISFACTION = "r";

    private static final String HOTEL_V3_LIST = "hl"; // 호텔리스트
    private static final String HOTEL_V3_DETAIL = "hd"; // 호텔 상세
    private static final String HOTEL_V3_REGION_LIST = "hrl"; // 호텔 지역 리스트
    private static final String HOTEL_V3_EVENT_BANNER_WEB = "hebw"; // 이벤트 배너 웹
    private static final String GOURMET_V3_LIST = "gl"; // 고메 리스트
    private static final String GOURMET_V3_DETAIL = "gd"; // 고메 상세
    private static final String GOURMET_V3_REGION_LIST = "grl"; // 고메 지역 리스트
    private static final String GOURMET_V3_EVENT_BANNER_WEB = "gebw"; // 이벤트 배너 웹
    private static final String BONUS_V3 = "b"; // 적립금
    private static final String EVENT_V3_LIST = "el"; // 이벤트 리스트
    private static final String BOOKING_V3_LIST = "bl"; // 예약 리스트

    private static final String SINGUP_V4 = "su"; // 회원 가입 화면

    private static final String V3 = "3";
    private static final String V4 = "4";

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

        String versionCode = uri.getQueryParameter(PARAM_V3_VERSION_CODE);

        if (Util.isTextEmpty(versionCode) == false)
        {
            if (V3.equalsIgnoreCase(versionCode) == true)
            {
                mVersionCode = 3;
                decodingLinkV3(uri);
            } else if (V4.equalsIgnoreCase(versionCode) == true)
            {
                mVersionCode = 4;
                decodingLinkV4(uri);
            } else
            {
                clear();
            }
        } else
        {
            mVersionCode = 2;
            decodingLinkV2(uri);
        }
    }

    public void clear()
    {
        mVersionCode = 0;
        mDeepLinkUri = null;
        mParams.clear();
    }

    private String getView()
    {
        String value;

        switch (mVersionCode)
        {
            case 3:
            case 4:
                value = mParams.get(PARAM_V3_VIEW);
                break;

            default:
                value = mParams.get(PARAM_V2_VIEW);
                break;
        }

        return value;
    }

    public boolean isHotelView()
    {
        String view = getView();

        switch (mVersionCode)
        {
            case 3:
            case 4:
                return HOTEL_V3_LIST.equalsIgnoreCase(view)//
                    || HOTEL_V3_DETAIL.equalsIgnoreCase(view)//
                    || HOTEL_V3_REGION_LIST.equalsIgnoreCase(view)//
                    || HOTEL_V3_EVENT_BANNER_WEB.equalsIgnoreCase(view);

            default:
                return HOTEL_V2_LIST.equalsIgnoreCase(view);
        }
    }

    public boolean isHotelListView()
    {
        String view = getView();

        switch (mVersionCode)
        {
            case 3:
            case 4:
                return HOTEL_V3_LIST.equalsIgnoreCase(view);

            default:
                return false;
        }
    }

    public boolean isHotelDetailView()
    {
        String view = getView();

        switch (mVersionCode)
        {
            case 3:
            case 4:
                return HOTEL_V3_DETAIL.equalsIgnoreCase(view);

            default:
            {
                return HOTEL_V2_LIST.equalsIgnoreCase(view)//
                    && Util.isTextEmpty(mParams.get(PARAM_V2_INDEX)) == false//
                    && Util.isTextEmpty(mParams.get(PARAM_V2_DATE)) == false//
                    && Util.isTextEmpty(mParams.get(PARAM_V2_NIGHTS)) == false;
            }
        }
    }

    public boolean isHotelRegionListView()
    {
        String view = getView();

        switch (mVersionCode)
        {
            case 3:
            case 4:
                return HOTEL_V3_REGION_LIST.equalsIgnoreCase(view);

            default:
                return false;
        }
    }

    public boolean isHotelEventBannerWebView()
    {
        String view = getView();

        switch (mVersionCode)
        {
            case 3:
            case 4:
                return HOTEL_V3_EVENT_BANNER_WEB.equalsIgnoreCase(view);

            default:
                return false;
        }
    }

    public boolean isGourmetView()
    {
        String view = getView();

        switch (mVersionCode)
        {
            case 3:
            case 4:
                return GOURMET_V3_LIST.equalsIgnoreCase(view)//
                    || GOURMET_V3_DETAIL.equalsIgnoreCase(view)//
                    || GOURMET_V3_REGION_LIST.equalsIgnoreCase(view)//
                    || GOURMET_V3_EVENT_BANNER_WEB.equalsIgnoreCase(view);

            default:
                return GOURMET_V2_LIST.equalsIgnoreCase(view);
        }
    }

    public boolean isGourmetListView()
    {
        String view = getView();

        switch (mVersionCode)
        {
            case 3:
            case 4:
                return GOURMET_V3_LIST.equalsIgnoreCase(view);

            default:
                return false;
        }
    }

    public boolean isGourmetDetailView()
    {
        String view = getView();

        switch (mVersionCode)
        {
            case 3:
            case 4:
                return GOURMET_V3_DETAIL.equalsIgnoreCase(view);

            default:
            {
                return GOURMET_V2_LIST.equalsIgnoreCase(view)//
                    && Util.isTextEmpty(mParams.get(PARAM_V2_INDEX)) == false//
                    && Util.isTextEmpty(mParams.get(PARAM_V2_DATE)) == false;
            }
        }
    }

    public boolean isGourmetRegionListView()
    {
        String view = getView();

        switch (mVersionCode)
        {
            case 3:
            case 4:
                return GOURMET_V3_REGION_LIST.equalsIgnoreCase(view);

            default:
                return false;
        }
    }

    public boolean isGourmetEventBannerWebView()
    {
        String view = getView();

        switch (mVersionCode)
        {
            case 3:
            case 4:
                return GOURMET_V3_EVENT_BANNER_WEB.equalsIgnoreCase(view);

            default:
                return false;
        }
    }

    public boolean isBookingView()
    {
        String view = getView();

        switch (mVersionCode)
        {
            case 3:
            case 4:
                return BOOKING_V3_LIST.equalsIgnoreCase(view);

            default:
                return BOOKING_V2_LIST.equalsIgnoreCase(view);
        }
    }

    public boolean isBonusView()
    {
        String view = getView();

        switch (mVersionCode)
        {
            case 3:
            case 4:
                return BONUS_V3.equalsIgnoreCase(view);

            default:
                return false;
        }
    }

    public boolean isEventView()
    {
        String view = getView();

        switch (mVersionCode)
        {
            case 3:
            case 4:
                return EVENT_V3_LIST.equalsIgnoreCase(view);

            default:
                return EVENT_V2_LIST.equalsIgnoreCase(view);
        }
    }

    public boolean isSingUpView()
    {
        String view = getView();

        switch (mVersionCode)
        {
            case 4:
                return SINGUP_V4.equalsIgnoreCase(view);

            default:
                return false;
        }
    }

    public String getIndex()
    {
        String value;

        switch (mVersionCode)
        {
            case 3:
            case 4:
                value = mParams.get(PARAM_V3_INDEX);
                break;

            default:
                value = mParams.get(PARAM_V2_INDEX);
                break;
        }

        return value;
    }

    public String getNights()
    {
        String value;

        switch (mVersionCode)
        {
            case 3:
            case 4:
                value = mParams.get(PARAM_V3_NIGHTS);
                break;

            default:
                value = mParams.get(PARAM_V2_NIGHTS);
                break;
        }

        return value;
    }

    public String getDate()
    {
        String value;

        switch (mVersionCode)
        {
            case 3:
            case 4:
                value = mParams.get(PARAM_V3_DATE);
                break;

            default:
                value = mParams.get(PARAM_V2_DATE);
                break;
        }

        return value;
    }

    public String getUrl()
    {
        String value;

        switch (mVersionCode)
        {
            case 3:
            case 4:
                value = mParams.get(PARAM_V3_URL);
                break;

            default:
                value = null;
                break;
        }

        return value;
    }

    public String getProvinceIndex()
    {
        String value;

        switch (mVersionCode)
        {
            case 3:
            case 4:
                value = mParams.get(PARAM_V3_PROVINCE_INDEX);
                break;

            default:
                value = null;
                break;
        }

        return value;
    }

    public String getAreaIndex()
    {
        String value;

        switch (mVersionCode)
        {
            case 3:
            case 4:
                value = mParams.get(PARAM_V3_AREA_INDEX);
                break;

            default:
                value = null;
                break;
        }

        return value;
    }

    public boolean getIsOverseas()
    {
        boolean value = false;

        switch (mVersionCode)
        {
            case 3:
            case 4:
                String isOverseas = mParams.get(PARAM_V3_REGION_ISOVERSEA);
                if ("0".equalsIgnoreCase(isOverseas) == true)
                {
                    value = false;
                } else if ("1".equalsIgnoreCase(isOverseas) == true)
                {
                    value = true;
                }
                break;

            default:
                value = false;
                break;
        }

        return value;
    }

    public String getCategoryCode()
    {
        String value;

        switch (mVersionCode)
        {
            case 3:
            case 4:
                value = mParams.get(PARAM_V3_CATEGORY_CODE);
                break;

            default:
                value = null;
                break;
        }

        return value;
    }

    public String getRecommenderCode()
    {
        String value;

        switch (mVersionCode)
        {
            case 4:
                value = mParams.get(PARAM_V4_RECOMMENDER_CODE);
                break;

            default:
                value = null;
                break;
        }

        return value;
    }

    public int getDatePlus()
    {
        String value;

        switch (mVersionCode)
        {
            case 4:
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
                break;

            default:
                break;
        }

        return -1;
    }

    public Constants.SortType getSorting()
    {
        String value;

        switch (mVersionCode)
        {
            case 4:
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
                break;

            default:
                break;
        }

        return Constants.SortType.DEFAULT;
    }

    private void decodingLinkV4(Uri uri)
    {
        decodingLinkV3(uri);

        putParams(uri, PARAM_V4_RECOMMENDER_CODE);
        putParams(uri, PARAM_V4_DATE_PLUS);
        putParams(uri, PARAM_V4_SORTING);
    }

    private void decodingLinkV3(Uri uri)
    {
        mParams.clear();

        if (uri == null)
        {
            clear();
            return;
        }

        if (putParams(uri, PARAM_V3_VIEW) == false)
        {
            // view는 기본요소라서 없으면 안된다
            clear();
            return;
        }

        putParams(uri, PARAM_V3_DATE);
        putParams(uri, PARAM_V3_NIGHTS);
        putParams(uri, PARAM_V3_URL);
        putParams(uri, PARAM_V3_INDEX);
        putParams(uri, PARAM_V3_PROVINCE_INDEX);
        putParams(uri, PARAM_V3_AREA_INDEX);
        putParams(uri, PARAM_V3_REGION_ISOVERSEA);
        putParams(uri, PARAM_V3_CATEGORY_CODE);
    }

    private void decodingLinkV2(Uri uri)
    {
        mParams.clear();

        if (uri == null)
        {
            clear();
            return;
        }

        if (putParams(uri, PARAM_V2_VIEW) == false)
        {
            // view는 기본요소라서 없으면 안된다
            clear();
            return;
        }

        putParams(uri, PARAM_V2_DATE);
        putParams(uri, PARAM_V2_NIGHTS);
        putParams(uri, PARAM_V2_INDEX);
    }

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
}
