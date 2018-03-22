package com.daily.dailyhotel.entity;

import java.util.ArrayList;
import java.util.List;

public class StayFilter
{
    public static final int PERSON_COUNT_OF_MIN = 1;
    public static final int PERSON_COUNT_OF_DEFAULT = 1;
    public static final int PERSON_COUNT_OF_MAX = 10;

    public static final int FLAG_BED_NONE = 0x00;
    public static final int FLAG_BED_DOUBLE = 0x01;
    public static final int FLAG_BED_TWIN = FLAG_BED_DOUBLE << 1;
    public static final int FLAG_BED_HEATEDFLOORS = FLAG_BED_DOUBLE << 2;
    //
    public static final int FLAG_AMENITIES_NONE = 0x00;
    public static final int FLAG_AMENITIES_PARKING = 0x01;
    public static final int FLAG_AMENITIES_SHARED_BBQ = FLAG_AMENITIES_PARKING << 1;
    public static final int FLAG_AMENITIES_POOL = FLAG_AMENITIES_PARKING << 2;
    public static final int FLAG_AMENITIES_BUSINESS_CENTER = FLAG_AMENITIES_PARKING << 3;
    public static final int FLAG_AMENITIES_FITNESS = FLAG_AMENITIES_PARKING << 4;
    public static final int FLAG_AMENITIES_SAUNA = FLAG_AMENITIES_PARKING << 5;
    public static final int FLAG_AMENITIES_PET = FLAG_AMENITIES_PARKING << 6;
    public static final int FLAG_AMENITIES_KIDS_PLAY_ROOM = FLAG_AMENITIES_PARKING << 7;
    //
    public static final int FLAG_ROOM_AMENITIES_NONE = 0x00;
    public static final int FLAG_ROOM_AMENITIES_WIFI = 0x01;
    public static final int FLAG_ROOM_AMENITIES_COOKING = FLAG_ROOM_AMENITIES_WIFI << 1;
    public static final int FLAG_ROOM_AMENITIES_PC = FLAG_ROOM_AMENITIES_WIFI << 2;
    public static final int FLAG_ROOM_AMENITIES_BATHTUB = FLAG_ROOM_AMENITIES_WIFI << 3;
    public static final int FLAG_ROOM_AMENITIES_TV = FLAG_ROOM_AMENITIES_WIFI << 4;
    public static final int FLAG_ROOM_AMENITIES_SPA_WHIRLPOOL = FLAG_ROOM_AMENITIES_WIFI << 5;
    public static final int FLAG_ROOM_AMENITIES_PRIVATE_BBQ = FLAG_ROOM_AMENITIES_WIFI << 6;
    public static final int FLAG_ROOM_AMENITIES_BREAKFAST = FLAG_ROOM_AMENITIES_WIFI << 7;
    public static final int FLAG_ROOM_AMENITIES_KARAOKE = FLAG_ROOM_AMENITIES_WIFI << 8;
    public static final int FLAG_ROOM_AMENITIES_PARTY_ROOM = FLAG_ROOM_AMENITIES_WIFI << 9;

    public int person = PERSON_COUNT_OF_DEFAULT;
    public int flagBedTypeFilters;
    public int flagAmenitiesFilters; // luxuries
    public int flagRoomAmenitiesFilters; // room luxuries

    public SortType defaultSortType = SortType.DEFAULT;
    public SortType sortType = defaultSortType;

    public enum SortType
    {
        DEFAULT,
        DISTANCE,
        LOW_PRICE,
        HIGH_PRICE,
        SATISFACTION
    }

    public boolean isDistanceSort()
    {
        return sortType == SortType.DISTANCE;
    }

    public boolean isDefault()
    {
        return (sortType == defaultSortType//
            && person == PERSON_COUNT_OF_DEFAULT//
            && flagBedTypeFilters == FLAG_BED_NONE//
            && flagAmenitiesFilters == FLAG_AMENITIES_NONE//
            && flagRoomAmenitiesFilters == FLAG_ROOM_AMENITIES_NONE);
    }

    public StayFilter reset()
    {
        sortType = defaultSortType;
        person = PERSON_COUNT_OF_DEFAULT;
        flagBedTypeFilters = FLAG_BED_NONE;
        flagAmenitiesFilters = FLAG_AMENITIES_NONE;
        flagRoomAmenitiesFilters = FLAG_ROOM_AMENITIES_NONE;

        return this;
    }

    public List<String> getBedTypeList()
    {
        if (flagBedTypeFilters == FLAG_BED_NONE)
        {
            return null;
        }

        List<String> bedTypeList = new ArrayList();

        if ((flagBedTypeFilters & FLAG_BED_DOUBLE) == FLAG_BED_DOUBLE)
        {
            bedTypeList.add("Double");
        }

        if ((flagBedTypeFilters & FLAG_BED_TWIN) == FLAG_BED_TWIN)
        {
            bedTypeList.add("Twin");
        }

        if ((flagBedTypeFilters & FLAG_BED_HEATEDFLOORS) == FLAG_BED_HEATEDFLOORS)
        {
            bedTypeList.add("Ondol");
        }

        return bedTypeList;
    }

    public List<String> getAmenitiesFilter()
    {
        List<String> amenitiesFilterList = new ArrayList();

        if (flagAmenitiesFilters == FLAG_AMENITIES_NONE)
        {
            return amenitiesFilterList;
        }

        if ((flagAmenitiesFilters & FLAG_AMENITIES_PARKING) == FLAG_AMENITIES_PARKING)
        {
            amenitiesFilterList.add("Parking");
        }

        if ((flagAmenitiesFilters & FLAG_AMENITIES_SHARED_BBQ) == FLAG_AMENITIES_SHARED_BBQ)
        {
            amenitiesFilterList.add("SharedBbq");
        }

        if ((flagAmenitiesFilters & FLAG_AMENITIES_POOL) == FLAG_AMENITIES_POOL)
        {
            amenitiesFilterList.add("Pool");
        }

        if ((flagAmenitiesFilters & FLAG_AMENITIES_BUSINESS_CENTER) == FLAG_AMENITIES_BUSINESS_CENTER)
        {
            amenitiesFilterList.add("BusinessCenter");
        }

        if ((flagAmenitiesFilters & FLAG_AMENITIES_FITNESS) == FLAG_AMENITIES_FITNESS)
        {
            amenitiesFilterList.add("Fitness");
        }

        if ((flagAmenitiesFilters & FLAG_AMENITIES_SAUNA) == FLAG_AMENITIES_SAUNA)
        {
            amenitiesFilterList.add("Sauna");
        }

        if ((flagAmenitiesFilters & FLAG_AMENITIES_PET) == FLAG_AMENITIES_PET)
        {
            amenitiesFilterList.add("Pet");
        }

        if ((flagAmenitiesFilters & FLAG_AMENITIES_KIDS_PLAY_ROOM) == FLAG_AMENITIES_KIDS_PLAY_ROOM)
        {
            amenitiesFilterList.add("KidsPlayroom");
        }

        return amenitiesFilterList;
    }

    public List<String> getRoomAmenitiesFilterList()
    {
        List<String> roomAmenitiesFilterList = new ArrayList();

        if (flagRoomAmenitiesFilters == FLAG_ROOM_AMENITIES_NONE)
        {
            return roomAmenitiesFilterList;
        }

        if ((flagRoomAmenitiesFilters & FLAG_ROOM_AMENITIES_WIFI) == FLAG_ROOM_AMENITIES_WIFI)
        {
            roomAmenitiesFilterList.add("WiFi");
        }

        if ((flagRoomAmenitiesFilters & FLAG_ROOM_AMENITIES_COOKING) == FLAG_ROOM_AMENITIES_COOKING)
        {
            roomAmenitiesFilterList.add("Cooking");
        }

        if ((flagRoomAmenitiesFilters & FLAG_ROOM_AMENITIES_PC) == FLAG_ROOM_AMENITIES_PC)
        {
            roomAmenitiesFilterList.add("Pc");
        }

        if ((flagRoomAmenitiesFilters & FLAG_ROOM_AMENITIES_BATHTUB) == FLAG_ROOM_AMENITIES_BATHTUB)
        {
            roomAmenitiesFilterList.add("Bath");
        }

        if ((flagRoomAmenitiesFilters & FLAG_ROOM_AMENITIES_TV) == FLAG_ROOM_AMENITIES_TV)
        {
            roomAmenitiesFilterList.add("Tv");
        }

        if ((flagRoomAmenitiesFilters & FLAG_ROOM_AMENITIES_SPA_WHIRLPOOL) == FLAG_ROOM_AMENITIES_SPA_WHIRLPOOL)
        {
            roomAmenitiesFilterList.add("SpaWallpool");
        }

        if ((flagRoomAmenitiesFilters & FLAG_ROOM_AMENITIES_PRIVATE_BBQ) == FLAG_ROOM_AMENITIES_PRIVATE_BBQ)
        {
            roomAmenitiesFilterList.add("PrivateBbq");
        }

        if ((flagRoomAmenitiesFilters & FLAG_ROOM_AMENITIES_BREAKFAST) == FLAG_ROOM_AMENITIES_BREAKFAST)
        {
            roomAmenitiesFilterList.add("Breakfast");
        }

        if ((flagRoomAmenitiesFilters & FLAG_ROOM_AMENITIES_KARAOKE) == FLAG_ROOM_AMENITIES_KARAOKE)
        {
            roomAmenitiesFilterList.add("Karaoke");
        }

        if ((flagRoomAmenitiesFilters & FLAG_ROOM_AMENITIES_PARTY_ROOM) == FLAG_ROOM_AMENITIES_PARTY_ROOM)
        {
            roomAmenitiesFilterList.add("PartyRoom");
        }

        return roomAmenitiesFilterList;
    }
}
