package com.daily.dailyhotel.screen.home.stay.inbound.list;

import android.app.Activity;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.AreaElement;
import com.daily.dailyhotel.entity.Stay;
import com.daily.dailyhotel.entity.StayArea;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayFilter;
import com.daily.dailyhotel.entity.StayRegion;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.daily.dailyhotel.storage.preference.DailyUserPreference;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.model.DailyCategoryType;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class StayListFragmentAnalyticsImpl implements StayListFragmentInterface.AnalyticsInterface
{
    @Override
    public void onScreen(Activity activity, DailyCategoryType categoryType, StayTabPresenter.ViewType viewType//
        , StayBookDateTime stayBookDateTime, String categoryCode, StayFilter stayFilter, StayRegion stayRegion)
    {
        if (activity == null || stayBookDateTime == null || categoryType == null || categoryCode == null || stayFilter == null || stayRegion == null)
        {
            return;
        }

        if (viewType == null)
        {
            AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.DAILYHOTEL_LIST_EMPTY, null);
            return;
        }

        String screen = getScreenName(categoryType, viewType);

        Map<String, String> params = new HashMap<>();

        switch (viewType)
        {
            case LIST:
                params.put(AnalyticsManager.KeyType.VIEW_TYPE, AnalyticsManager.ValueType.LIST);
                break;

            case MAP:
                params.put(AnalyticsManager.KeyType.VIEW_TYPE, AnalyticsManager.ValueType.MAP);
                break;

            default:
                return;
        }

        try
        {
            params.put(AnalyticsManager.KeyType.CHECK_IN, stayBookDateTime.getCheckInDateTime("yyyy-MM-dd"));
            params.put(AnalyticsManager.KeyType.CHECK_OUT, stayBookDateTime.getCheckOutDateTime("yyyy-MM-dd"));
            params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.toString(stayBookDateTime.getNights()));

            if (DailyHotel.isLogin() == false)
            {
                params.put(AnalyticsManager.KeyType.IS_SIGNED, AnalyticsManager.ValueType.GUEST);
            } else
            {
                params.put(AnalyticsManager.KeyType.IS_SIGNED, AnalyticsManager.ValueType.MEMBER);
            }

            params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.STAY);
            params.put(AnalyticsManager.KeyType.PLACE_HIT_TYPE, AnalyticsManager.ValueType.STAY);
            params.put(AnalyticsManager.KeyType.CATEGORY, categoryCode);
            params.put(AnalyticsManager.KeyType.PUSH_NOTIFICATION, DailyUserPreference.getInstance(activity).isBenefitAlarm() ? "on" : "off");

            // Filter
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
            params.put(AnalyticsManager.KeyType.COUNTRY, AnalyticsManager.ValueType.DOMESTIC);

            params.put(AnalyticsManager.KeyType.PROVINCE, stayRegion.getAreaGroupName());

            AreaElement areaElement = stayRegion.getAreaElement();
            params.put(AnalyticsManager.KeyType.DISTRICT, areaElement == null || areaElement.index == StayArea.ALL ? AnalyticsManager.ValueType.ALL_LOCALE_KR : areaElement.name);

            AnalyticsManager.getInstance(activity).recordScreen(activity, screen, null, params);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    private String getScreenName(DailyCategoryType categoryType, StayTabPresenter.ViewType viewType)
    {
        if (categoryType == null)
        {
            return null;
        }

        switch (categoryType)
        {
            case STAY_ALL:
                switch (viewType)
                {
                    case LIST:
                        return AnalyticsManager.Screen.DAILYHOTEL_LIST;

                    case MAP:
                        return AnalyticsManager.Screen.DAILYHOTEL_LIST_MAP;
                }
                break;

            case STAY_HOTEL:
                return AnalyticsManager.Screen.STAY_LIST_SHORTCUT_HOTEL;

            case STAY_BOUTIQUE:
                return AnalyticsManager.Screen.STAY_LIST_SHORTCUT_BOUTIQUE;

            case STAY_PENSION:
                return AnalyticsManager.Screen.STAY_LIST_SHORTCUT_PENSION;

            case STAY_RESORT:
                return AnalyticsManager.Screen.STAY_LIST_SHORTCUT_RESORT;
        }

        return null;
    }

    @Override
    public void onEventStayClick(Activity activity, DailyCategoryType categoryType, StayTabPresenter.ViewType viewType, Stay stay)
    {
        if (activity == null || viewType == null || stay == null)
        {
            return;
        }

        try
        {
            switch (viewType)
            {
                case LIST:
                    AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION//
                        , AnalyticsManager.Action.STAY_ITEM_CLICK, String.format(Locale.KOREA, "%d_%d", stay.entryPosition, stay.index), null);

                    AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION//
                        , stay.dailyChoice ? AnalyticsManager.Action.STAY_DAILYCHOICE_CLICK_Y : AnalyticsManager.Action.STAY_DAILYCHOICE_CLICK_N, Integer.toString(stay.index), null);

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

                    if (stay.discountRate > 0)
                    {
                        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
                            , AnalyticsManager.Action.DISCOUNT_STAY, Integer.toString(stay.index), null);
                    }
                    break;

                case MAP:
                    AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION_//
                        , AnalyticsManager.Action.HOTEL_MAP_DETAIL_VIEW_CLICKED, stay.name, null);
                    break;
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    public void onEventWishClick(Activity activity, DailyCategoryType categoryType, int stayIndex, boolean wish, boolean isListViewType)
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
    public void onEventMarkerClick(Activity activity, DailyCategoryType categoryType, String stayName)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION_, AnalyticsManager.Action.HOTEL_MAP_ICON_CLICKED, stayName, null);
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

            if (stringBuilder.length() > 0 && stringBuilder.charAt(stringBuilder.length() - 1) == ',')
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
            StringBuilder stringBuilder = new StringBuilder();

            if ((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_PARKING) == StayFilter.FLAG_AMENITIES_PARKING)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_PARKINGAVAILABLE).append(',');
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_POOL) == StayFilter.FLAG_AMENITIES_POOL)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_POOL).append(',');
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_FITNESS) == StayFilter.FLAG_AMENITIES_FITNESS)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_FITNESS).append(',');
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_SAUNA) == StayFilter.FLAG_AMENITIES_SAUNA)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_SAUNA).append(',');
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_BUSINESS_CENTER) == StayFilter.FLAG_AMENITIES_BUSINESS_CENTER)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_BUSINESS_CENTER).append(',');
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_KIDS_PLAY_ROOM) == StayFilter.FLAG_AMENITIES_KIDS_PLAY_ROOM)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_KIDS_PLAY_ROOM).append(',');
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_SHARED_BBQ) == StayFilter.FLAG_AMENITIES_SHARED_BBQ)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_BBQ).append(',');
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_PET) == StayFilter.FLAG_AMENITIES_PET)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_PET).append(',');
            }

            if (stringBuilder.length() > 0 && stringBuilder.charAt(stringBuilder.length() - 1) == ',')
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

            if ((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_KITCHENETTE) == StayFilter.FLAG_ROOM_AMENITIES_KITCHENETTE)
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

            if (stringBuilder.length() > 0 && stringBuilder.charAt(stringBuilder.length() - 1) == ',')
            {
                stringBuilder.setLength(stringBuilder.length() - 1);
            }

            return stringBuilder.toString();
        }
    }
}
