package com.daily.dailyhotel.screen.home.stay.inbound.filter;

import android.app.Activity;

import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.StayArea;
import com.daily.dailyhotel.entity.StayFilter;
import com.daily.dailyhotel.entity.StaySuggestV2;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.HashMap;
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
    public void onConfirmClick(Activity activity, StaySuggestV2 suggest, StayFilter stayFilter, int listCountByFilter)
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

                if (suggest.getSuggestType() == StaySuggestV2.SuggestType.AREA_GROUP)
                {
                    StaySuggestV2.AreaGroup suggestItem = (StaySuggestV2.AreaGroup) suggest.getSuggestItem();
                    StaySuggestV2.Area area = suggestItem.area;
                    eventParams.put(AnalyticsManager.KeyType.DISTRICT, area == null || area.index == StayArea.ALL ? AnalyticsManager.ValueType.ALL_LOCALE_KR : area.name);
                } else
                {
                    eventParams.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.ALL_LOCALE_KR);
                }
            }

            StringBuilder stringBuilder = new StringBuilder();

            String filterSortString = getFilterSortString(stayFilter.sortType);
            stringBuilder.append(filterSortString);
            stringBuilder.append('-');
            stringBuilder.append(stayFilter.person);
            stringBuilder.append('-');

            String filterBedTypeString = getFilterBedType(stayFilter.flagBedTypeFilters);
            stringBuilder.append(filterBedTypeString);
            stringBuilder.append('-');

            String filterAmenityString = getFilterAmenityString(stayFilter.flagAmenitiesFilters);
            stringBuilder.append(filterAmenityString);
            stringBuilder.append('-');

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

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(getFilterSortString(stayFilter.sortType));
        stringBuffer.append('-');
        stringBuffer.append(stayFilter.person);
        stringBuffer.append('-');
        stringBuffer.append(getFilterBedType(stayFilter.flagBedTypeFilters));
        stringBuffer.append('-');
        stringBuffer.append(getFilterAmenityString(stayFilter.flagAmenitiesFilters));
        stringBuffer.append('-');
        stringBuffer.append(getFilterRoomAmenityString(stayFilter.flagRoomAmenitiesFilters));

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SORT_FLITER //
            , AnalyticsManager.Action.STAY_NO_RESULT, stringBuffer.toString(), null);
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
