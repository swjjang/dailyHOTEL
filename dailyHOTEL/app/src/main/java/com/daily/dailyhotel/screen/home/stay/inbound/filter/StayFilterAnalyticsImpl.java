package com.daily.dailyhotel.screen.home.stay.inbound.filter;

import android.app.Activity;

import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.StayArea;
import com.daily.dailyhotel.entity.StayFilter;
import com.daily.dailyhotel.entity.StaySuggest;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StayFilterAnalyticsImpl implements StayFilterPresenter.StayFilterAnalyticsInterface
{
    @Override
    public void onScreen(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.DAILYHOTEL_CURATION, null);
    }

    @Override
    public void onConfirmClick(Activity activity, StaySuggest suggest, StayFilter stayFilter, int listCountByFilter)
    {
        if (activity == null)
        {
            return;
        }

        try
        {
            Map<String, String> eventParams = new HashMap<>();

            eventParams.put(AnalyticsManager.KeyType.SORTING, stayFilter.sortType.name());
            eventParams.put(AnalyticsManager.KeyType.SEARCH_COUNT, Integer.toString(listCountByFilter));

            if (suggest != null)
            {
                eventParams.put(AnalyticsManager.KeyType.COUNTRY, AnalyticsManager.ValueType.DOMESTIC);
                eventParams.put(AnalyticsManager.KeyType.PROVINCE, suggest.getSuggestItem().name);

                if (suggest.getSuggestType() == StaySuggest.SuggestType.AREA_GROUP)
                {
                    StaySuggest.AreaGroup suggestItem = (StaySuggest.AreaGroup) suggest.getSuggestItem();
                    StaySuggest.Area area = suggestItem.area;
                    eventParams.put(AnalyticsManager.KeyType.DISTRICT, area == null || area.index == StayArea.ALL ? AnalyticsManager.ValueType.ALL_LOCALE_KR : area.name);
                } else
                {
                    eventParams.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.ALL_LOCALE_KR);
                }
            }

            // 베드타입
            List<String> bedTypeList = stayFilter.getBedTypeList();
            if (bedTypeList != null && bedTypeList.size() > 0)
            {
                eventParams.put(AnalyticsManager.KeyType.BEDTYPE_DOUBLE, Boolean.toString(bedTypeList.contains("DOUBLE")));
                eventParams.put(AnalyticsManager.KeyType.BEDTYPE_TWIN, Boolean.toString(bedTypeList.contains("TWIN")));
                eventParams.put(AnalyticsManager.KeyType.BEDTYPE_IN_FLOOR_HEATING, Boolean.toString(bedTypeList.contains("IN_FLOOR_HEATING")));
                eventParams.put(AnalyticsManager.KeyType.BEDTYPE_SINGLE, Boolean.toString(bedTypeList.contains("SINGLE")));
            }

            // 시설
            List<String> amenitiesList = stayFilter.getAmenitiesFilter();
            eventParams.put(AnalyticsManager.KeyType.FACILITY_KIDS_PLAY_ROOM, Boolean.toString(amenitiesList.contains("KidsPlayroom")));
            eventParams.put(AnalyticsManager.KeyType.FACILITY_POOL, Boolean.toString(amenitiesList.contains("Pool")));
            eventParams.put(AnalyticsManager.KeyType.FACILITY_PET, Boolean.toString(amenitiesList.contains("Pet")));

            List<String> roomAmenitiesList = stayFilter.getRoomAmenitiesFilterList();
            eventParams.put(AnalyticsManager.KeyType.FACILITY_BREAKFAST, Boolean.toString(roomAmenitiesList.contains("Breakfast")));
            eventParams.put(AnalyticsManager.KeyType.FACILITY_PART_ROOM, Boolean.toString(roomAmenitiesList.contains("PartyRoom")));
            eventParams.put(AnalyticsManager.KeyType.FACILITY_WHIRLPOOL, Boolean.toString(roomAmenitiesList.contains("SpaWallpool")));

            final char DELIMITER = '-';

            StringBuilder stringBuilder = new StringBuilder();

            String filterSortString = getFilterSortString(stayFilter.sortType);
            stringBuilder.append(filterSortString);
            stringBuilder.append(DELIMITER);
            stringBuilder.append(stayFilter.person);
            stringBuilder.append(DELIMITER);

            String filterBedTypeString = getFilterBedTypeString(stayFilter.flagBedTypeFilters);
            stringBuilder.append(filterBedTypeString);
            stringBuilder.append(DELIMITER);

            String filterAmenityString = getFilterAmenityString(stayFilter.flagAmenitiesFilters);
            stringBuilder.append(filterAmenityString);
            stringBuilder.append(DELIMITER);

            String filterRoomAmenityString = getFilterRoomAmenityString(stayFilter.flagRoomAmenitiesFilters);
            stringBuilder.append(filterRoomAmenityString);

            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                , AnalyticsManager.Action.HOTEL_SORT_FILTER_APPLY_BUTTON_CLICKED, stringBuilder.toString(), eventParams);

            // 추가 항목
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SORT_FLITER//
                , AnalyticsManager.Action.STAY_SORT, filterSortString, null);

            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SORT_FLITER//
                , AnalyticsManager.Action.STAY_PERSON, Integer.toString(stayFilter.person), null);

            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SORT_FLITER//
                , AnalyticsManager.Action.STAY_BEDTYPE, filterBedTypeString, null);

            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SORT_FLITER//
                , AnalyticsManager.Action.STAY_AMENITIES, filterAmenityString, null);

            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SORT_FLITER//
                , AnalyticsManager.Action.STAY_ROOM_AMENITIES, filterRoomAmenityString, null);

            if (Constants.DEBUG == true)
            {
                ExLog.d(stringBuilder.toString());
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    @Override
    public void onBackClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
            , AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_CLICKED, AnalyticsManager.Label.CLOSE_BUTTON_CLICKED, null);
    }

    @Override
    public void onResetClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
            , AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_CLICKED, AnalyticsManager.Label.RESET_BUTTON_CLICKED, null);
    }

    @Override
    public void onEmptyResult(Activity activity, StayFilter stayFilter)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SORT_FLITER //
            , AnalyticsManager.Action.STAY_NO_RESULT, getFilterString(stayFilter), null);
    }

    private String getFilterString(StayFilter filter)
    {
        if (filter == null)
        {
            return null;
        }

        final char DELIMITER = '-';

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getFilterSortString(filter.sortType));
        stringBuilder.append(DELIMITER);
        stringBuilder.append(filter.person);
        stringBuilder.append(DELIMITER);
        stringBuilder.append(getFilterBedTypeString(filter.flagBedTypeFilters));
        stringBuilder.append(DELIMITER);
        stringBuilder.append(getFilterAmenityString(filter.flagAmenitiesFilters));
        stringBuilder.append(DELIMITER);
        stringBuilder.append(getFilterRoomAmenityString(filter.flagRoomAmenitiesFilters));

        return stringBuilder.toString();
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

    private String getFilterBedTypeString(int flagBedTypeFilters)
    {
        // Bed Type
        if (flagBedTypeFilters == StayFilter.FLAG_BED_NONE)
        {
            return AnalyticsManager.Label.SORTFILTER_NONE;
        } else
        {
            final char DELIMITER = ',';

            StringBuilder stringBuilder = new StringBuilder();

            if ((flagBedTypeFilters & StayFilter.FLAG_BED_DOUBLE) == StayFilter.FLAG_BED_DOUBLE)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_DOUBLE).append(DELIMITER);
            }

            if ((flagBedTypeFilters & StayFilter.FLAG_BED_TWIN) == StayFilter.FLAG_BED_TWIN)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_TWIN).append(DELIMITER);
            }

            if ((flagBedTypeFilters & StayFilter.FLAG_BED_HEATEDFLOORS) == StayFilter.FLAG_BED_HEATEDFLOORS)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_ONDOL).append(DELIMITER);
            }

            if ((flagBedTypeFilters & StayFilter.FLAG_BED_SINGLE) == StayFilter.FLAG_BED_SINGLE)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_ONDOL).append(DELIMITER);
            }

            if (stringBuilder.length() > 0 && stringBuilder.charAt(stringBuilder.length() - 1) == DELIMITER)
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

            if ((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_BASSINET) == StayFilter.FLAG_AMENITIES_BASSINET)
            {
                stringBuilder.append(AnalyticsManager.Label.SORTFILTER_BASSINET).append(DELIMITER);
            }

            if (stringBuilder.length() > 0 && stringBuilder.charAt(stringBuilder.length() - 1) == DELIMITER)
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
            final char DELIMITER = ',';

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

            if (stringBuilder.length() > 0 && stringBuilder.charAt(stringBuilder.length() - 1) == DELIMITER)
            {
                stringBuilder.setLength(stringBuilder.length() - 1);
            }

            return stringBuilder.toString();
        }
    }
}
