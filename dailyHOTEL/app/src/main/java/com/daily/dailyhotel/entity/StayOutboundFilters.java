package com.daily.dailyhotel.entity;

public class StayOutboundFilters
{
    public static final int FILTER_ALL_RATING = -1;

    public int rating;
    public SortType sortType;
    public SortType defaultSortType;
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
        defaultSortType = StayOutboundFilters.SortType.RECOMMENDATION;
    }

    public void reset()
    {
        sortType = defaultSortType;
        rating = FILTER_ALL_RATING;
    }

    public boolean isDefaultFilter()
    {
        return sortType == defaultSortType && rating == -1;
    }
}
