package com.twoheart.dailyhotel.model;

public class StayFilter
{
    public static final int MIN_PERSON = 2;
    public static final int MAX_PERSON = 10;

    public static final int FLAG_HOTEL_FILTER_BED_NONE = 0x00;
    public static final int FLAG_HOTEL_FILTER_BED_DOUBLE = 0x01;
    public static final int FLAG_HOTEL_FILTER_BED_TWIN = 0x02;
    public static final int FLAG_HOTEL_FILTER_BED_HEATEDFLOORS = 0x04;
    public static final int FLAG_HOTEL_FILTER_BED_CHECKIN = 0x08;
    //
    public static final int FLAG_HOTEL_FILTER_AMENITIES_NONE = 0x00;
    public static final int FLAG_HOTEL_FILTER_AMENITIES_WIFI = 0x01;
    public static final int FLAG_HOTEL_FILTER_AMENITIES_BREAKFAST = 0x02;
    public static final int FLAG_HOTEL_FILTER_AMENITIES_COOKING = 0x04;
    public static final int FLAG_HOTEL_FILTER_AMENITIES_BATH = 0x08;
    public static final int FLAG_HOTEL_FILTER_AMENITIES_PARKING = 0x10;
    public static final int FLAG_HOTEL_FILTER_AMENITIES_POOL = 0x20;
    public static final int FLAG_HOTEL_FILTER_AMENITIES_FITNESS = 0x40;
    public static final int FLAG_HOTEL_FILTER_AMENITIES_NOPARKING = 0x80;
    public static final int FLAG_HOTEL_FILTER_AMENITIES_PET = 0x0100;
    public static final int FLAG_HOTEL_FILTER_AMENITIES_SHAREDBBQ = 0x0200;
}
