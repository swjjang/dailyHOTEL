package com.daily.dailyhotel.entity;

public class StayFilter
{
    public static final int MIN_PERSON = 1;
    public static final int DEFAULT_PERSON = 2;
    public static final int MAX_PERSON = 10;

    public static final int FLAG_BED_NONE = 0x00;
    public static final int FLAG_BED_DOUBLE = 0x01;
    public static final int FLAG_BED_TWIN = FLAG_BED_DOUBLE << 1;
    public static final int FLAG_BED_HEATEDFLOORS = FLAG_BED_DOUBLE << 2;
    public static final int FLAG_BED_CHECKIN = FLAG_BED_DOUBLE << 3;
    //
    public static final int FLAG_AMENITIES_NONE = 0x00;
    public static final int FLAG_AMENITIES_PARKING = 0x01;
    public static final int FLAG_AMENITIES_NO_PARKING = FLAG_AMENITIES_PARKING << 1;
    public static final int FLAG_AMENITIES_SHARED_BBQ = FLAG_AMENITIES_PARKING << 2;
    public static final int FLAG_AMENITIES_POOL = FLAG_AMENITIES_PARKING << 3;
    public static final int FLAG_AMENITIES_BUSINESS_CENTER = FLAG_AMENITIES_PARKING << 4;
    public static final int FLAG_AMENITIES_FITNESS = FLAG_AMENITIES_PARKING << 5;
    public static final int FLAG_AMENITIES_SAUNA = FLAG_AMENITIES_PARKING << 6;
    public static final int FLAG_AMENITIES_PET = FLAG_AMENITIES_PARKING << 7;
    public static final int FLAG_AMENITIES_KIDS_PLAY_ROOM = FLAG_AMENITIES_PARKING << 8;
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

    public int person;
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

    public boolean isDefaultFilter()
    {
        return (sortType == defaultSortType//
            && person == DEFAULT_PERSON//
            && flagBedTypeFilters == FLAG_BED_NONE//
            && flagAmenitiesFilters == FLAG_AMENITIES_NONE//
            && flagRoomAmenitiesFilters == FLAG_ROOM_AMENITIES_NONE);
    }
}
