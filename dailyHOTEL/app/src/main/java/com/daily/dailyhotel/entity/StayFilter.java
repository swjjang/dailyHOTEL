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
    public static final int FLAG_BED_HEATEDFLOORS = FLAG_BED_TWIN << 1;
    public static final int FLAG_BED_SINGLE = FLAG_BED_HEATEDFLOORS << 1;


    //
    public static final int FLAG_AMENITIES_NONE = 0x00;
    public static final int FLAG_AMENITIES_POOL = 0x01;
    public static final int FLAG_AMENITIES_SAUNA = FLAG_AMENITIES_POOL << 1;
    public static final int FLAG_AMENITIES_SPA_MASSAGE = FLAG_AMENITIES_SAUNA << 1;
    public static final int FLAG_AMENITIES_BREAKFAST_RESTAURANT = FLAG_AMENITIES_SPA_MASSAGE << 1;
    public static final int FLAG_AMENITIES_CAFETERIA = FLAG_AMENITIES_BREAKFAST_RESTAURANT << 1;
    public static final int FLAG_AMENITIES_SEMINAR_ROOM = FLAG_AMENITIES_CAFETERIA << 1;
    public static final int FLAG_AMENITIES_BUSINESS_CENTER = FLAG_AMENITIES_SEMINAR_ROOM << 1;
    public static final int FLAG_AMENITIES_WIFI = FLAG_AMENITIES_BUSINESS_CENTER << 1;
    public static final int FLAG_AMENITIES_FITNESS = FLAG_AMENITIES_WIFI << 1;
    public static final int FLAG_AMENITIES_CLUB_LOUNGE = FLAG_AMENITIES_FITNESS << 1;
    public static final int FLAG_AMENITIES_SHARED_BBQ = FLAG_AMENITIES_CLUB_LOUNGE << 1;
    public static final int FLAG_AMENITIES_PICK_UP = FLAG_AMENITIES_SHARED_BBQ << 1;
    public static final int FLAG_AMENITIES_CONVENIENCE_STORE = FLAG_AMENITIES_PICK_UP << 1;
    public static final int FLAG_AMENITIES_PARKING = FLAG_AMENITIES_CONVENIENCE_STORE << 1;
    public static final int FLAG_AMENITIES_PET = FLAG_AMENITIES_PARKING << 1;
    public static final int FLAG_AMENITIES_KIDS_PLAY_ROOM = FLAG_AMENITIES_PET << 1;
    public static final int FLAG_AMENITIES_BASSINET = FLAG_AMENITIES_KIDS_PLAY_ROOM << 1;


    //
    public static final int FLAG_ROOM_AMENITIES_NONE = 0x00;
    public static final int FLAG_ROOM_AMENITIES_SPA_WALL_POOL = 0x01;
    public static final int FLAG_ROOM_AMENITIES_BATHTUB = FLAG_ROOM_AMENITIES_SPA_WALL_POOL << 1;
    public static final int FLAG_ROOM_AMENITIES_BATH_AMENITY = FLAG_ROOM_AMENITIES_BATHTUB << 1;
    public static final int FLAG_ROOM_AMENITIES_SHOWER_GOWN = FLAG_ROOM_AMENITIES_BATH_AMENITY << 1;
    public static final int FLAG_ROOM_AMENITIES_TOOTHBRUSH_SET = FLAG_ROOM_AMENITIES_SHOWER_GOWN << 1;
    public static final int FLAG_ROOM_AMENITIES_PRIVATE_BBQ = FLAG_ROOM_AMENITIES_TOOTHBRUSH_SET << 1;
    public static final int FLAG_ROOM_AMENITIES_PRIVATE_POOL = FLAG_ROOM_AMENITIES_PRIVATE_BBQ << 1;
    public static final int FLAG_ROOM_AMENITIES_PARTY_ROOM = FLAG_ROOM_AMENITIES_PRIVATE_POOL << 1;
    public static final int FLAG_ROOM_AMENITIES_KARAOKE = FLAG_ROOM_AMENITIES_PARTY_ROOM << 1;
    public static final int FLAG_ROOM_AMENITIES_BREAKFAST = FLAG_ROOM_AMENITIES_KARAOKE << 1;
    public static final int FLAG_ROOM_AMENITIES_PC = FLAG_ROOM_AMENITIES_BREAKFAST << 1;
    public static final int FLAG_ROOM_AMENITIES_TV = FLAG_ROOM_AMENITIES_PC << 1;
    public static final int FLAG_ROOM_AMENITIES_KITCHENETTE = FLAG_ROOM_AMENITIES_TV << 1;
    public static final int FLAG_ROOM_AMENITIES_SMOKEABLE = FLAG_ROOM_AMENITIES_KITCHENETTE << 1;
    public static final int FLAG_ROOM_AMENITIES_DISABLED_FACILITIES = FLAG_ROOM_AMENITIES_SMOKEABLE << 1;

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
            bedTypeList.add("DOUBLE");
        }

        if ((flagBedTypeFilters & FLAG_BED_TWIN) == FLAG_BED_TWIN)
        {
            bedTypeList.add("TWIN");
        }

        if ((flagBedTypeFilters & FLAG_BED_HEATEDFLOORS) == FLAG_BED_HEATEDFLOORS)
        {
            bedTypeList.add("IN_FLOOR_HEATING");
        }

        if ((flagBedTypeFilters & FLAG_BED_SINGLE) == FLAG_BED_SINGLE)
        {
            bedTypeList.add("SINGLE");
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

        if ((flagAmenitiesFilters & FLAG_AMENITIES_POOL) == FLAG_AMENITIES_POOL)
        {
            amenitiesFilterList.add("Pool");
        }

        if ((flagAmenitiesFilters & FLAG_AMENITIES_SAUNA) == FLAG_AMENITIES_SAUNA)
        {
            amenitiesFilterList.add("Sauna");
        }

        if ((flagAmenitiesFilters & FLAG_AMENITIES_SPA_MASSAGE) == FLAG_AMENITIES_SPA_MASSAGE)
        {
            amenitiesFilterList.add("SpaMassage");
        }

        if ((flagAmenitiesFilters & FLAG_AMENITIES_BREAKFAST_RESTAURANT) == FLAG_AMENITIES_BREAKFAST_RESTAURANT)
        {
            amenitiesFilterList.add("BreakfastRestaurant");
        }

        if ((flagAmenitiesFilters & FLAG_AMENITIES_CAFETERIA) == FLAG_AMENITIES_CAFETERIA)
        {
            amenitiesFilterList.add("Cafeteria");
        }

        if ((flagAmenitiesFilters & FLAG_AMENITIES_SEMINAR_ROOM) == FLAG_AMENITIES_SEMINAR_ROOM)
        {
            amenitiesFilterList.add("SeminarRoom");
        }

        if ((flagAmenitiesFilters & FLAG_AMENITIES_BUSINESS_CENTER) == FLAG_AMENITIES_BUSINESS_CENTER)
        {
            amenitiesFilterList.add("BusinessCenter");
        }

        if ((flagAmenitiesFilters & FLAG_AMENITIES_WIFI) == FLAG_AMENITIES_WIFI)
        {
            amenitiesFilterList.add("WiFi");
        }

        if ((flagAmenitiesFilters & FLAG_AMENITIES_FITNESS) == FLAG_AMENITIES_FITNESS)
        {
            amenitiesFilterList.add("Fitness");
        }

        if ((flagAmenitiesFilters & FLAG_AMENITIES_CLUB_LOUNGE) == FLAG_AMENITIES_CLUB_LOUNGE)
        {
            amenitiesFilterList.add("ClubLounge");
        }

        if ((flagAmenitiesFilters & FLAG_AMENITIES_SHARED_BBQ) == FLAG_AMENITIES_SHARED_BBQ)
        {
            amenitiesFilterList.add("SharedBbq");
        }

        if ((flagAmenitiesFilters & FLAG_AMENITIES_PICK_UP) == FLAG_AMENITIES_PICK_UP)
        {
            amenitiesFilterList.add("PickupAvailable");
        }

        if ((flagAmenitiesFilters & FLAG_AMENITIES_CONVENIENCE_STORE) == FLAG_AMENITIES_CONVENIENCE_STORE)
        {
            amenitiesFilterList.add("ConvenienceStore");
        }

        if ((flagAmenitiesFilters & FLAG_AMENITIES_PARKING) == FLAG_AMENITIES_PARKING)
        {
            amenitiesFilterList.add("Parking");
        }

        if ((flagAmenitiesFilters & FLAG_AMENITIES_PET) == FLAG_AMENITIES_PET)
        {
            amenitiesFilterList.add("Pet");
        }
        if ((flagAmenitiesFilters & FLAG_AMENITIES_KIDS_PLAY_ROOM) == FLAG_AMENITIES_KIDS_PLAY_ROOM)
        {
            amenitiesFilterList.add("KidsPlayroom");
        }

        if ((flagAmenitiesFilters & FLAG_AMENITIES_BASSINET) == FLAG_AMENITIES_BASSINET)
        {
            amenitiesFilterList.add("Bassinet");
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

        if ((flagRoomAmenitiesFilters & FLAG_ROOM_AMENITIES_SPA_WALL_POOL) == FLAG_ROOM_AMENITIES_SPA_WALL_POOL)
        {
            roomAmenitiesFilterList.add("SpaWallpool");
        }

        if ((flagRoomAmenitiesFilters & FLAG_ROOM_AMENITIES_BATHTUB) == FLAG_ROOM_AMENITIES_BATHTUB)
        {
            roomAmenitiesFilterList.add("Bath");
        }

        if ((flagRoomAmenitiesFilters & FLAG_ROOM_AMENITIES_BATH_AMENITY) == FLAG_ROOM_AMENITIES_BATH_AMENITY)
        {
            roomAmenitiesFilterList.add("Amenity");
        }

        if ((flagRoomAmenitiesFilters & FLAG_ROOM_AMENITIES_SHOWER_GOWN) == FLAG_ROOM_AMENITIES_SHOWER_GOWN)
        {
            roomAmenitiesFilterList.add("ShowerGown");
        }

        if ((flagRoomAmenitiesFilters & FLAG_ROOM_AMENITIES_TOOTHBRUSH_SET) == FLAG_ROOM_AMENITIES_TOOTHBRUSH_SET)
        {
            roomAmenitiesFilterList.add("ToothbrushSet");
        }

        if ((flagRoomAmenitiesFilters & FLAG_ROOM_AMENITIES_PRIVATE_BBQ) == FLAG_ROOM_AMENITIES_PRIVATE_BBQ)
        {
            roomAmenitiesFilterList.add("PrivateBbq");
        }

        if ((flagRoomAmenitiesFilters & FLAG_ROOM_AMENITIES_PRIVATE_POOL) == FLAG_ROOM_AMENITIES_PRIVATE_POOL)
        {
            roomAmenitiesFilterList.add("PrivatePool");
        }

        if ((flagRoomAmenitiesFilters & FLAG_ROOM_AMENITIES_PARTY_ROOM) == FLAG_ROOM_AMENITIES_PARTY_ROOM)
        {
            roomAmenitiesFilterList.add("PartyRoom");
        }

        if ((flagRoomAmenitiesFilters & FLAG_ROOM_AMENITIES_KARAOKE) == FLAG_ROOM_AMENITIES_KARAOKE)
        {
            roomAmenitiesFilterList.add("Karaoke");
        }

        if ((flagRoomAmenitiesFilters & FLAG_ROOM_AMENITIES_BREAKFAST) == FLAG_ROOM_AMENITIES_BREAKFAST)
        {
            roomAmenitiesFilterList.add("Breakfast");
        }

        if ((flagRoomAmenitiesFilters & FLAG_ROOM_AMENITIES_PC) == FLAG_ROOM_AMENITIES_PC)
        {
            roomAmenitiesFilterList.add("Pc");
        }

        if ((flagRoomAmenitiesFilters & FLAG_ROOM_AMENITIES_TV) == FLAG_ROOM_AMENITIES_TV)
        {
            roomAmenitiesFilterList.add("Tv");
        }

        if ((flagRoomAmenitiesFilters & FLAG_ROOM_AMENITIES_KITCHENETTE) == FLAG_ROOM_AMENITIES_KITCHENETTE)
        {
            roomAmenitiesFilterList.add("Kitchenette");
        }

        if ((flagRoomAmenitiesFilters & FLAG_ROOM_AMENITIES_SMOKEABLE) == FLAG_ROOM_AMENITIES_SMOKEABLE)
        {
            roomAmenitiesFilterList.add("Smokeable");
        }

        if ((flagRoomAmenitiesFilters & FLAG_ROOM_AMENITIES_DISABLED_FACILITIES) == FLAG_ROOM_AMENITIES_DISABLED_FACILITIES)
        {
            roomAmenitiesFilterList.add("DisabledFacilities");
        }

        return roomAmenitiesFilterList;
    }
}
