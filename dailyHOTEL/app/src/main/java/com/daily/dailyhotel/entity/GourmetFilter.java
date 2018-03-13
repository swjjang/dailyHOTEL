package com.daily.dailyhotel.entity;

public class GourmetFilter
{
    public class Time
    {
        public static final int FLAG_NONE = 0x00;
        public static final int FLAG_06_11 = 0x01;
        public static final int FLAG_11_15 = 0x02;
        public static final int FLAG_15_17 = 0x04;
        public static final int FLAG_17_21 = 0x08;
        public static final int FLAG_21_06 = 0x10;
    }

    public class Amenities
    {
        public static final int FLAG_NONE = 0x00;
        public static final int FLAG_PARKING = 0x01;
        public static final int FLAG_VALET = 0x02;
        public static final int FLAG_BABYSEAT = 0x04;
        public static final int FLAG_PRIVATEROOM = 0x08;
        public static final int FLAG_GROUPBOOKING = 0x10;
        public static final int FLAG_CORKAGE = 0x20;
    }
}
