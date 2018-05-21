package com.daily.dailyhotel.screen.home.search.stay.inbound.result.search;

import android.app.Activity;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.Category;
import com.daily.dailyhotel.entity.Stay;
import com.daily.dailyhotel.entity.StayArea;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayFilter;
import com.daily.dailyhotel.entity.StaySuggest;
import com.daily.dailyhotel.screen.home.search.stay.inbound.result.SearchStayResultTabPresenter;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.daily.dailyhotel.storage.preference.DailyUserPreference;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SearchStayResultListFragmentAnalyticsImpl implements SearchStayResultListFragmentInterface.AnalyticsInterface
{
    @Override
    public void onScreen(Activity activity, SearchStayResultTabPresenter.ViewType viewType, StayBookDateTime bookDateTime//
        , StaySuggest suggest, StayFilter stayFilter, boolean empty, String callByScreen)
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


            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("[sort:");
            stringBuilder.append(getFilterSortString(stayFilter.sortType));
            stringBuilder.append(",persons:");
            stringBuilder.append(stayFilter.person);
            stringBuilder.append(",type:");
            stringBuilder.append(getFilterBedType(stayFilter.flagBedTypeFilters));
            stringBuilder.append(",facility:");
            stringBuilder.append(getFilterAmenityString(stayFilter.flagAmenitiesFilters));
            stringBuilder.append(",Room facility:");
            stringBuilder.append(getFilterRoomAmenityString(stayFilter.flagRoomAmenitiesFilters));

            params.put(AnalyticsManager.KeyType.FILTER, stringBuilder.toString());
            params.put(AnalyticsManager.KeyType.PUSH_NOTIFICATION, DailyUserPreference.getInstance(activity).isBenefitAlarm() ? "on" : "off");

            params.put(AnalyticsManager.KeyType.VIEW_TYPE //
                , viewType == SearchStayResultTabPresenter.ViewType.MAP //
                    ? AnalyticsManager.ValueType.MAP : AnalyticsManager.ValueType.LIST);

            params.put(AnalyticsManager.KeyType.COUNTRY, AnalyticsManager.ValueType.DOMESTIC);
            params.put(AnalyticsManager.KeyType.PROVINCE, suggest.getSuggestItem().name);


            if (suggest.getSuggestType() == StaySuggest.SuggestType.AREA_GROUP)
            {
                StaySuggest.Area area = ((StaySuggest.AreaGroup) suggest.getSuggestItem()).area;

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
    public void onEventWishClick(Activity activity, int stayIndex, boolean wish, boolean isListViewType)
    {
        if (activity == null)
        {
            return;
        }

        if (isListViewType)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
                , AnalyticsManager.Action.WISH_STAY, wish ? AnalyticsManager.Label.ON.toLowerCase() : AnalyticsManager.Label.OFF.toLowerCase(), null);
        } else
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION_//
                , "stay_" + (wish ? "WishListOn_mapview" : "WishListOff_mapview"), Integer.toString(stayIndex), null);
        }
    }

    @Override
    public void onEventMarkerClick(Activity activity, String name)
    {

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
    public void onEventStayClick(Activity activity, Stay stay, StaySuggest suggest)
    {
        if (activity == null || stay == null || suggest == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , AnalyticsManager.Action.STAY_ITEM_CLICK, Integer.toString(stay.index), null);

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

        if (stay.trueVR == true)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.STAY_ITEM_CLICK_TRUE_VR, Integer.toString(stay.index), null);
        }

        if (DailyRemoteConfigPreference.getInstance(activity).isKeyRemoteConfigRewardStickerEnabled()//
            && stay.provideRewardSticker == true)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.REWARD//
                , AnalyticsManager.Action.THUMBNAIL_CLICK, Integer.toString(stay.index), null);
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , AnalyticsManager.Action.STAY_ITEM_CLICK, String.format(Locale.KOREA, "%d_%d", stay.entryPosition, stay.index), null);

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
    public void onEventSearchResult(Activity activity, StayBookDateTime bookDateTime, StaySuggest suggest, String inputKeyword//
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
                recordEventSearchResultByRecentKeyword(activity, displayName, empty, params);
                break;

            case RECENTLY_STAY:
                recordEventSearchResultByRecentStay(activity, displayName, empty, params);
                break;

            case DIRECT:
                recordEventSearchResultByKeyword(activity, displayName, empty, params);
                break;

            case SUGGEST:
                recordEventSearchResultByAutoSearch(activity, suggest, inputKeyword, empty, params);
                break;
        }
    }

    @Override
    public void onEventSearchResultCountOneAndSoldOut(Activity activity, String stayName)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH//
            , "stay_selling_completion", stayName, null);
    }

    @Override
    public void onEventSearchResultAllSoldOut(Activity activity, String inputKeyword)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH//
            , "stay_selling_completion", inputKeyword, null);
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

    private void recordEventSearchResultByRecentStay(Activity activity, String displayName, boolean empty, Map<String, String> params)
    {
        String action = empty ? "RecentSearchPlaceNotFound" : "RecentSearchPlace";

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

    private void recordEventSearchResultByAutoSearch(Activity activity, StaySuggest suggest, String inputKeyword, boolean empty, Map<String, String> params)
    {
        if (activity == null || suggest == null || params == null)
        {
            return;
        }

        String category = empty ? AnalyticsManager.Category.AUTO_SEARCH_NOT_FOUND : AnalyticsManager.Category.AUTO_SEARCH;
        String displayName = suggest.getText1();

        params.put(AnalyticsManager.KeyType.SEARCH_PATH, AnalyticsManager.ValueType.AUTO);
        params.put(AnalyticsManager.KeyType.SEARCH_WORD, inputKeyword);
        params.put(AnalyticsManager.KeyType.SEARCH_RESULT, displayName);

        String suggestType;

        switch (suggest.getSuggestType())
        {
            case STAY:
                suggestType = "업장";
                break;

            case AREA_GROUP:
                suggestType = "도시/지역";
                break;

            case STATION:
                suggestType = "역";
                break;

            default:
                suggestType = "";
                break;
        }

        AnalyticsManager.getInstance(activity).recordEvent(category//
            , "stay_" + suggestType + "_" + displayName, inputKeyword, params);
    }

    private String getFilterSortString(StayFilter.SortType sortType)
    {
        if (sortType == null)
        {
            return AnalyticsManager.ValueType.EMPTY;
        }

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
            StringBuilder stringBuilder = new StringBuilder();

            if ((flagBedTypeFilters & StayFilter.FLAG_BED_DOUBLE) == StayFilter.FLAG_BED_DOUBLE)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_DOUBLE).append(',');
            }

            if ((flagBedTypeFilters & StayFilter.FLAG_BED_TWIN) == StayFilter.FLAG_BED_TWIN)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_TWIN).append(',');
            }

            if ((flagBedTypeFilters & StayFilter.FLAG_BED_HEATEDFLOORS) == StayFilter.FLAG_BED_HEATEDFLOORS)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_ONDOL).append(',');
            }

            if (stringBuilder.charAt(stringBuilder.length() - 1) == ',')
            {
                stringBuilder.setLength(stringBuilder.length() - 1);
            }

            return stringBuilder.toString();
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
            final char DELIMITER = ',';

            StringBuilder stringBuilder = new StringBuilder();

            if ((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_POOL) == StayFilter.FLAG_AMENITIES_POOL)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_POOL).append(DELIMITER);
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_SAUNA) == StayFilter.FLAG_AMENITIES_SAUNA)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_SAUNA).append(DELIMITER);
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_SPA_MASSAGE) == StayFilter.FLAG_AMENITIES_SPA_MASSAGE)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_SPA_MASSAGE).append(DELIMITER);
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_BREAKFAST_RESTAURANT) == StayFilter.FLAG_AMENITIES_BREAKFAST_RESTAURANT)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_BREAKFAST).append(DELIMITER);
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_CAFETERIA) == StayFilter.FLAG_AMENITIES_CAFETERIA)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_CAFETERIA).append(DELIMITER);
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_SEMINAR_ROOM) == StayFilter.FLAG_AMENITIES_SEMINAR_ROOM)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_SEMINAR_ROOM).append(DELIMITER);
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_BUSINESS_CENTER) == StayFilter.FLAG_AMENITIES_BUSINESS_CENTER)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_BUSINESS_CENTER).append(DELIMITER);
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_WIFI) == StayFilter.FLAG_AMENITIES_WIFI)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_WIFI).append(DELIMITER);
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_FITNESS) == StayFilter.FLAG_AMENITIES_FITNESS)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_FITNESS).append(DELIMITER);
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_CLUB_LOUNGE) == StayFilter.FLAG_AMENITIES_CLUB_LOUNGE)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_CLUB_LOUNGE).append(DELIMITER);
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_SHARED_BBQ) == StayFilter.FLAG_AMENITIES_SHARED_BBQ)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_SHARED_BBQ).append(DELIMITER);
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_PICK_UP) == StayFilter.FLAG_AMENITIES_PICK_UP)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_PICK_UP).append(DELIMITER);
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_CONVENIENCE_STORE) == StayFilter.FLAG_AMENITIES_CONVENIENCE_STORE)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_CONVENIENCE_STORE).append(DELIMITER);
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_PARKING) == StayFilter.FLAG_AMENITIES_PARKING)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_PARKING).append(DELIMITER);
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_PET) == StayFilter.FLAG_AMENITIES_PET)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_PET).append(DELIMITER);
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_KIDS_PLAY_ROOM) == StayFilter.FLAG_AMENITIES_KIDS_PLAY_ROOM)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_KIDS_PLAY_ROOM).append(DELIMITER);
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_RENT_BABY_BED) == StayFilter.FLAG_AMENITIES_RENT_BABY_BED)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_RENT_BABY_BED).append(DELIMITER);
            }

            if (stringBuilder.charAt(stringBuilder.length() - 1) == ',')
            {
                stringBuilder.setLength(stringBuilder.length() - 1);
            }

            return stringBuilder.toString();
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
            StringBuilder stringBuilder = new StringBuilder();

            if ((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_SPA_WALL_POOL) == StayFilter.FLAG_ROOM_AMENITIES_SPA_WALL_POOL)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_SPA_WALL_POOL).append(',');
            }

            if ((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_BATHTUB) == StayFilter.FLAG_ROOM_AMENITIES_BATHTUB)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_BATHTUB).append(',');
            }

            if ((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_BATH_AMENITY) == StayFilter.FLAG_ROOM_AMENITIES_BATH_AMENITY)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_BATH_AMENITIES).append(',');
            }

            if ((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_SHOWER_GOWN) == StayFilter.FLAG_ROOM_AMENITIES_SHOWER_GOWN)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_SHOWER_GOWN).append(',');
            }

            if ((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_TOOTHBRUSH_SET) == StayFilter.FLAG_ROOM_AMENITIES_TOOTHBRUSH_SET)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_TOOTHBRUSH_SET).append(',');
            }

            if ((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_PRIVATE_BBQ) == StayFilter.FLAG_ROOM_AMENITIES_PRIVATE_BBQ)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_PRIVATE_BBQ).append(',');
            }

            if ((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_PRIVATE_POOL) == StayFilter.FLAG_ROOM_AMENITIES_PRIVATE_POOL)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_PRIVATE_POOL).append(',');
            }

            if ((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_PARTY_ROOM) == StayFilter.FLAG_ROOM_AMENITIES_PARTY_ROOM)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_PARTYROOM).append(',');
            }

            if ((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_KARAOKE) == StayFilter.FLAG_ROOM_AMENITIES_KARAOKE)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_KARAOKE).append(',');
            }

            if ((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_BREAKFAST) == StayFilter.FLAG_ROOM_AMENITIES_BREAKFAST)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_BREAKFAST).append(',');
            }

            if ((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_PC) == StayFilter.FLAG_ROOM_AMENITIES_PC)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_PC).append(',');
            }

            if ((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_TV) == StayFilter.FLAG_ROOM_AMENITIES_TV)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_TV).append(',');
            }

            if ((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_COOKING) == StayFilter.FLAG_ROOM_AMENITIES_COOKING)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_COOKING).append(',');
            }

            if ((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_SMOKEABLE) == StayFilter.FLAG_ROOM_AMENITIES_SMOKEABLE)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_COOKING).append(',');
            }

            if ((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_DISABLED_FACILITIES) == StayFilter.FLAG_ROOM_AMENITIES_DISABLED_FACILITIES)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_DISABLED_FACILITIES).append(',');
            }

            if (stringBuilder.charAt(stringBuilder.length() - 1) == ',')
            {
                stringBuilder.setLength(stringBuilder.length() - 1);
            }

            return stringBuilder.toString();
        }
    }
}
