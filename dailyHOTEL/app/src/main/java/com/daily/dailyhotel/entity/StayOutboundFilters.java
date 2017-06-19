package com.daily.dailyhotel.entity;

public class StayOutboundFilters
{
    public static final int RATING_MIN_VALUE = 1;
    public static final int RATING_MAX_VALUE = 5;

    public int rating;
    public SortType sortType;
    public double latitude;
    public double longitude;

    public enum SortType
    {
        RECOMMENDATION("DEFAULT"),
        DISTANCE("PROXIMITY"),
        LOW_PRICE("PRICE_AVERAGE"),
        HIGH_PRICE("PRICE_REVERSE"),
        SATISFACTION("TRIP_ADVISOR");

        private String mValue;

        SortType(String value)
        {
            mValue = value;
        }

        public String getValue()
        {
            return mValue;
        }
    }

    public StayOutboundFilters()
    {

    }
}
