package com.daily.dailyhotel.entity;

public class StayOutboundFilters
{
    public int rating;
    public SortType sortType;

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
