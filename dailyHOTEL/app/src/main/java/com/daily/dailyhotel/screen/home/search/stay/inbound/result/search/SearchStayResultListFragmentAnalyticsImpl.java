package com.daily.dailyhotel.screen.home.search.stay.inbound.result.search;

import android.app.Activity;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.Category;
import com.daily.dailyhotel.entity.Stay;
import com.daily.dailyhotel.entity.StayArea;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayFilter;
import com.daily.dailyhotel.entity.StaySuggestV2;
import com.daily.dailyhotel.screen.home.search.stay.inbound.result.SearchStayResultTabPresenter;
import com.daily.dailyhotel.storage.preference.DailyUserPreference;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.HashMap;
import java.util.Map;

public class SearchStayResultListFragmentAnalyticsImpl implements SearchStayResultListFragmentInterface.AnalyticsInterface
{
    @Override
    public void onScreen(Activity activity, SearchStayResultTabPresenter.ViewType viewType, StayBookDateTime bookDateTime//
        , StaySuggestV2 suggest, StayFilter stayFilter, boolean empty, String callByScreen)
    {
        if (activity == null || bookDateTime == null || suggest == null || stayFilter == null)
        {
            return;
        }

        try
        {
            Map<String, String> params = new HashMap<>();

            params.put(AnalyticsManager.KeyType.CHECK_IN, bookDateTime.getCheckInDateTime("yyyy-MM-dd"));
            params.put(AnalyticsManager.KeyType.CHECK_OUT, bookDateTime.getCheckOutDateTime("yyyy-MM-dd"));
            params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.toString(bookDateTime.getNights()));

            if (DailyHotel.isLogin() == false)
            {
                params.put(AnalyticsManager.KeyType.IS_SIGNED, AnalyticsManager.ValueType.GUEST);
                params.put(AnalyticsManager.KeyType.MEMBER_TYPE, AnalyticsManager.ValueType.EMPTY);
            } else
            {
                params.put(AnalyticsManager.KeyType.IS_SIGNED, AnalyticsManager.ValueType.MEMBER);
                switch (DailyUserPreference.getInstance(activity).getType())
                {
                    case Constants.DAILY_USER:
                        params.put(AnalyticsManager.KeyType.MEMBER_TYPE, AnalyticsManager.UserType.EMAIL);
                        break;

                    case Constants.KAKAO_USER:
                        params.put(AnalyticsManager.KeyType.MEMBER_TYPE, AnalyticsManager.UserType.KAKAO);
                        break;

                    case Constants.FACEBOOK_USER:
                        params.put(AnalyticsManager.KeyType.MEMBER_TYPE, AnalyticsManager.UserType.FACEBOOK);
                        break;

                    default:
                        params.put(AnalyticsManager.KeyType.MEMBER_TYPE, AnalyticsManager.ValueType.EMPTY);
                        break;
                }
            }

            params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.STAY);
            params.put(AnalyticsManager.KeyType.PLACE_HIT_TYPE, AnalyticsManager.ValueType.STAY);
            params.put(AnalyticsManager.KeyType.CATEGORY, Category.ALL.code);


            StringBuffer stringBuffer = new StringBuffer();

            stringBuffer.append("[sort:");
            stringBuffer.append(getFilterSortString(stayFilter.sortType));
            stringBuffer.append(",persons:");
            stringBuffer.append(stayFilter.person);
            stringBuffer.append(",type:");
            stringBuffer.append(getFilterBedType(stayFilter.flagBedTypeFilters));
            stringBuffer.append(",facility:");
            stringBuffer.append(getFilterAmenityString(stayFilter.flagAmenitiesFilters));
            stringBuffer.append(",Room facility:");
            stringBuffer.append(getFilterRoomAmenityString(stayFilter.flagRoomAmenitiesFilters));

            params.put(AnalyticsManager.KeyType.FILTER, stringBuffer.toString());
            params.put(AnalyticsManager.KeyType.PUSH_NOTIFICATION, DailyUserPreference.getInstance(activity).isBenefitAlarm() ? "on" : "off");

            params.put(AnalyticsManager.KeyType.VIEW_TYPE //
                , viewType == SearchStayResultTabPresenter.ViewType.MAP //
                    ? AnalyticsManager.ValueType.MAP : AnalyticsManager.ValueType.LIST);

            params.put(AnalyticsManager.KeyType.COUNTRY, AnalyticsManager.ValueType.DOMESTIC);
            params.put(AnalyticsManager.KeyType.PROVINCE, suggest.getSuggestItem().name);


            if (suggest.getSuggestType() == StaySuggestV2.SuggestType.AREA_GROUP)
            {
                StaySuggestV2.Area area = ((StaySuggestV2.AreaGroup) suggest.getSuggestItem()).area;

                params.put(AnalyticsManager.KeyType.DISTRICT, area == null || area.index == StayArea.ALL ? AnalyticsManager.ValueType.ALL_LOCALE_KR : area.name);
            }

            if (AnalyticsManager.Screen.HOME.equalsIgnoreCase(callByScreen) == true)
            {
                AnalyticsManager.getInstance(activity) //
                    .recordScreen(activity, AnalyticsManager.Screen.STAY_LIST_SHORTCUT_NEARBY, null, params);
            } else
            {
                String screen = empty ? AnalyticsManager.Screen.SEARCH_RESULT_EMPTY : AnalyticsManager.Screen.SEARCH_RESULT;

                AnalyticsManager.getInstance(activity).recordScreen(activity, screen + "_stay", null, params);
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    public void onEventWishClick(Activity activity, boolean wish)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
            , AnalyticsManager.Action.WISH_STAY, wish ? AnalyticsManager.Label.ON.toLowerCase() : AnalyticsManager.Label.OFF.toLowerCase(), null);
    }

    @Override
    public void onEventMarkerClick(Activity activity, String name)
    {

    }

    @Override
    public void onEventLocation(Activity activity, StayBookDateTime bookDateTime, String suggest, int searchCount, int searchMaxCount)
    {
        if (activity == null || bookDateTime == null)
        {
            return;
        }

        Map<String, String> params = new HashMap<>();

        try
        {
            params.put(AnalyticsManager.KeyType.CHECK_IN, bookDateTime.getCheckInDateTime("yyyy-MM-dd"));
            params.put(AnalyticsManager.KeyType.CHECK_OUT, bookDateTime.getCheckOutDateTime("yyyy-MM-dd"));
            params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.toString(bookDateTime.getNights()));
            params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.STAY);
            params.put(AnalyticsManager.KeyType.PLACE_HIT_TYPE, AnalyticsManager.ValueType.STAY);
            params.put(AnalyticsManager.KeyType.SEARCH_COUNT, Integer.toString(searchCount > searchMaxCount ? searchMaxCount : searchCount));

            params.put(AnalyticsManager.KeyType.SEARCH_PATH, AnalyticsManager.ValueType.AROUND);

            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_//
                , searchCount == 0 ? "AroundSearchNotFound_LocationList_stay" : "AroundSearchClicked_LocationList_stay"//
                , suggest, params);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    public void onEventCallClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_//
            , AnalyticsManager.Action.SEARCH_RESULT_VIEW, AnalyticsManager.Label.CALL, null);
    }

    @Override
    public void onEventStayClick(Activity activity, Stay stay, StaySuggestV2 suggest)
    {
        if (activity == null || stay == null || suggest == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , AnalyticsManager.Action.STAY_ITEM_CLICK, Integer.toString(stay.index), null);

        // 할인 쿠폰이 보이는 경우
        if (DailyTextUtils.isTextEmpty(stay.couponDiscountText) == false)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
                , AnalyticsManager.Action.COUPON_STAY, Integer.toString(stay.index), null);
        }

        if (stay.reviewCount > 0)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
                , AnalyticsManager.Action.TRUE_REVIEW_STAY, Integer.toString(stay.index), null);
        }

        if (stay.discountRate > 0)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
                , AnalyticsManager.Action.DISCOUNT_STAY, Integer.toString(stay.index), null);
        }

        if (stay.soldOut == true)
        {
            switch (suggest.menuType)
            {
                case LOCATION:
                case REGION_LOCATION:
                    AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SOLDOUT_STAY_ITEM_CLICK//
                        , AnalyticsManager.Action.NEARBY, Integer.toString(stay.index), null);
                    break;

                case SUGGEST:
                    AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SOLDOUT_STAY_ITEM_CLICK//
                        , AnalyticsManager.Action.AUTO_SEARCH, Integer.toString(stay.index), null);
                    break;

                case DIRECT:
                    AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SOLDOUT_STAY_ITEM_CLICK//
                        , AnalyticsManager.Action.KEYWORD, Integer.toString(stay.index), null);
                    break;

                case RECENTLY_STAY:
                case RECENTLY_SEARCH:
                    AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SOLDOUT_STAY_ITEM_CLICK//
                        , AnalyticsManager.Action.RECENT, Integer.toString(stay.index), null);
                    break;
            }
        }
    }

    @Override
    public void onEventSearchResult(Activity activity, StayBookDateTime bookDateTime, StaySuggestV2 suggest, String inputKeyword//
        , int searchCount, int searchMaxCount)
    {
        if (activity == null || bookDateTime == null || suggest == null)
        {
            return;
        }

        boolean empty = searchCount == 0;

        Map<String, String> params = new HashMap<>();
        try
        {
            params.put(AnalyticsManager.KeyType.CHECK_IN, bookDateTime.getCheckInDateTime("yyyy-MM-dd"));
            params.put(AnalyticsManager.KeyType.CHECK_OUT, bookDateTime.getCheckOutDateTime("yyyy-MM-dd"));
            params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.toString(bookDateTime.getNights()));
            params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.STAY);
            params.put(AnalyticsManager.KeyType.PLACE_HIT_TYPE, AnalyticsManager.ValueType.STAY);
            params.put(AnalyticsManager.KeyType.COUNTRY, AnalyticsManager.ValueType.DOMESTIC);

            params.put(AnalyticsManager.KeyType.SEARCH_COUNT, Integer.toString(searchCount > searchMaxCount ? searchMaxCount : searchCount));
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        if (suggest.isLocationSuggestType() == true)
        {
            params.put(AnalyticsManager.KeyType.SEARCH_PATH, AnalyticsManager.ValueType.AROUND);

            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_//
                , empty ? "AroundSearchNotFound_LocationList_stay" : "AroundSearchClicked_LocationList_stay"//
                , suggest.getText1(), params);
        }

        String displayName = suggest.getText1();

        switch (suggest.menuType)
        {
            case RECENTLY_SEARCH:
            case RECENTLY_STAY:
                recordEventSearchResultByRecentKeyword(activity, displayName, empty, params);
                break;

            case DIRECT:
                recordEventSearchResultByKeyword(activity, displayName, empty, params);
                break;

            case SUGGEST:
                recordEventSearchResultByAutoSearch(activity, displayName, inputKeyword, empty, params);
                break;
        }
    }

    private void recordEventSearchResultByRecentKeyword(Activity activity, String displayName, boolean empty, Map<String, String> params)
    {
        String action = empty ? AnalyticsManager.Action.RECENT_KEYWORD_NOT_FOUND : AnalyticsManager.Action.RECENT_KEYWORD;

        params.put(AnalyticsManager.KeyType.SEARCH_PATH, AnalyticsManager.ValueType.RECENT);
        params.put(AnalyticsManager.KeyType.SEARCH_WORD, displayName);
        params.put(AnalyticsManager.KeyType.SEARCH_RESULT, displayName);

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_//
            , action + "_stay", displayName, params);
    }

    private void recordEventSearchResultByKeyword(Activity activity, String displayName, boolean empty, Map<String, String> params)
    {
        String action = empty ? AnalyticsManager.Action.KEYWORD_NOT_FOUND : AnalyticsManager.Action.KEYWORD_;

        params.put(AnalyticsManager.KeyType.SEARCH_PATH, AnalyticsManager.ValueType.DIRECT);
        params.put(AnalyticsManager.KeyType.SEARCH_WORD, displayName);
        params.put(AnalyticsManager.KeyType.SEARCH_RESULT, displayName);

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_//
            , action + "_stay", displayName, params);
    }

    private void recordEventSearchResultByAutoSearch(Activity activity, String displayName, String inputKeyword, boolean empty, Map<String, String> params)
    {
        String category = empty ? AnalyticsManager.Category.AUTO_SEARCH_NOT_FOUND : AnalyticsManager.Category.AUTO_SEARCH;

        params.put(AnalyticsManager.KeyType.SEARCH_PATH, AnalyticsManager.ValueType.AUTO);
        params.put(AnalyticsManager.KeyType.SEARCH_WORD, inputKeyword);
        params.put(AnalyticsManager.KeyType.SEARCH_RESULT, displayName);

        AnalyticsManager.getInstance(activity).recordEvent(category//
            , "stay_" + displayName, inputKeyword, params);
    }

    private String getFilterSortString(StayFilter.SortType sortType)
    {
        if (sortType == null)
        {
            return AnalyticsManager.ValueType.EMPTY;
        }

        StringBuffer sortStringBuffer = new StringBuffer();

        switch (sortType)
        {
            case DEFAULT:
                return AnalyticsManager.Label.SORTFILTER_DISTRICT;

            case DISTANCE:
                return AnalyticsManager.Label.SORTFILTER_DISTANCE;

            case LOW_PRICE:
                return AnalyticsManager.Label.SORTFILTER_LOWTOHIGHPRICE;

            case HIGH_PRICE:
                return AnalyticsManager.Label.SORTFILTER_HIGHTOLOWPRICE;

            case SATISFACTION:
                return AnalyticsManager.Label.SORTFILTER_RATING;

            default:
                return AnalyticsManager.Label.SORTFILTER_DISTRICT;
        }
    }

    private String getFilterBedType(int flagBedTypeFilters)
    {
        // Bed Type
        if (flagBedTypeFilters == StayFilter.FLAG_BED_NONE)
        {
            return AnalyticsManager.Label.SORTFILTER_NONE;
        } else
        {
            StringBuffer stringBuffer = new StringBuffer();

            if ((flagBedTypeFilters & StayFilter.FLAG_BED_DOUBLE) == StayFilter.FLAG_BED_DOUBLE)
            {
                stringBuffer.append(AnalyticsManager.Label.SORTFILTER_DOUBLE).append(',');
            }

            if ((flagBedTypeFilters & StayFilter.FLAG_BED_TWIN) == StayFilter.FLAG_BED_TWIN)
            {
                stringBuffer.append(AnalyticsManager.Label.SORTFILTER_TWIN).append(',');
            }

            if ((flagBedTypeFilters & StayFilter.FLAG_BED_HEATEDFLOORS) == StayFilter.FLAG_BED_HEATEDFLOORS)
            {
                stringBuffer.append(AnalyticsManager.Label.SORTFILTER_ONDOL).append(',');
            }

            if (stringBuffer.charAt(stringBuffer.length() - 1) == ',')
            {
                stringBuffer.setLength(stringBuffer.length() - 1);
            }

            return stringBuffer.toString();
        }
    }

    private String getFilterAmenityString(int flagAmenitiesFilters)
    {
        // Amenity
        if (flagAmenitiesFilters == StayFilter.FLAG_AMENITIES_NONE)
        {
            return AnalyticsManager.Label.SORTFILTER_NONE;
        } else
        {
            StringBuffer stringBuffer = new StringBuffer();

            if ((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_PARKING) == StayFilter.FLAG_AMENITIES_PARKING)
            {
                stringBuffer.append(AnalyticsManager.Label.SORTFILTER_PARKINGAVAILABLE).append(',');
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_POOL) == StayFilter.FLAG_AMENITIES_POOL)
            {
                stringBuffer.append(AnalyticsManager.Label.SORTFILTER_POOL).append(',');
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_FITNESS) == StayFilter.FLAG_AMENITIES_FITNESS)
            {
                stringBuffer.append(AnalyticsManager.Label.SORTFILTER_FITNESS).append(',');
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_SAUNA) == StayFilter.FLAG_AMENITIES_SAUNA)
            {
                stringBuffer.append(AnalyticsManager.Label.SORTFILTER_SAUNA).append(',');
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_BUSINESS_CENTER) == StayFilter.FLAG_AMENITIES_BUSINESS_CENTER)
            {
                stringBuffer.append(AnalyticsManager.Label.SORTFILTER_BUSINESS_CENTER).append(',');
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_KIDS_PLAY_ROOM) == StayFilter.FLAG_AMENITIES_KIDS_PLAY_ROOM)
            {
                stringBuffer.append(AnalyticsManager.Label.SORTFILTER_KIDS_PLAY_ROOM).append(',');
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_SHARED_BBQ) == StayFilter.FLAG_AMENITIES_SHARED_BBQ)
            {
                stringBuffer.append(AnalyticsManager.Label.SORTFILTER_BBQ).append(',');
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_PET) == StayFilter.FLAG_AMENITIES_PET)
            {
                stringBuffer.append(AnalyticsManager.Label.SORTFILTER_PET).append(',');
            }

            if (stringBuffer.charAt(stringBuffer.length() - 1) == ',')
            {
                stringBuffer.setLength(stringBuffer.length() - 1);
            }

            return stringBuffer.toString();
        }
    }

    private String getFilterRoomAmenityString(int flagRoomAmenitiesFilters)
    {
        // Room Amenity
        if (flagRoomAmenitiesFilters == StayFilter.FLAG_ROOM_AMENITIES_NONE)
        {
            return AnalyticsManager.Label.SORTFILTER_NONE;
        } else
        {
            StringBuffer stringBuffer = new StringBuffer();

            if ((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_BREAKFAST) == StayFilter.FLAG_ROOM_AMENITIES_BREAKFAST)
            {
                stringBuffer.append(AnalyticsManager.Label.SORTFILTER_FREE_BREAKFAST).append(',');
            }

            if ((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_WIFI) == StayFilter.FLAG_ROOM_AMENITIES_WIFI)
            {
                stringBuffer.append(AnalyticsManager.Label.SORTFILTER_WIFI).append(',');
            }

            if ((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_COOKING) == StayFilter.FLAG_ROOM_AMENITIES_COOKING)
            {
                stringBuffer.append(AnalyticsManager.Label.SORTFILTER_KITCHEN).append(',');
            }

            if ((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_PC) == StayFilter.FLAG_ROOM_AMENITIES_PC)
            {
                stringBuffer.append(AnalyticsManager.Label.SORTFILTER_PC).append(',');
            }

            if ((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_BATHTUB) == StayFilter.FLAG_ROOM_AMENITIES_BATHTUB)
            {
                stringBuffer.append(AnalyticsManager.Label.SORTFILTER_BATHTUB).append(',');
            }

            if ((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_TV) == StayFilter.FLAG_ROOM_AMENITIES_TV)
            {
                stringBuffer.append(AnalyticsManager.Label.SORTFILTER_TV).append(',');
            }

            if ((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_SPA_WHIRLPOOL) == StayFilter.FLAG_ROOM_AMENITIES_SPA_WHIRLPOOL)
            {
                stringBuffer.append(AnalyticsManager.Label.SORTFILTER_SPA_WHIRLPOOL).append(',');
            }

            if ((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_PRIVATE_BBQ) == StayFilter.FLAG_ROOM_AMENITIES_PRIVATE_BBQ)
            {
                stringBuffer.append(AnalyticsManager.Label.SORTFILTER_PRIVATE_BBQ).append(',');
            }

            if ((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_KARAOKE) == StayFilter.FLAG_ROOM_AMENITIES_KARAOKE)
            {
                stringBuffer.append(AnalyticsManager.Label.SORTFILTER_KARAOKE).append(',');
            }

            if ((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_PARTY_ROOM) == StayFilter.FLAG_ROOM_AMENITIES_PARTY_ROOM)
            {
                stringBuffer.append(AnalyticsManager.Label.SORTFILTER_PARTYROOM).append(',');
            }

            if (stringBuffer.charAt(stringBuffer.length() - 1) == ',')
            {
                stringBuffer.setLength(stringBuffer.length() - 1);
            }

            return stringBuffer.toString();
        }
    }
}
