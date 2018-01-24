package com.daily.dailyhotel.entity;

import com.daily.base.util.DailyTextUtils;

public class StayOutboundSuggest
{
    public static final String CATEGORY_AIRPORT = "airport";
    public static final String CATEGORY_HOTEL = "hotel";
    public static final String CATEGORY_POINT = "point";
    public static final String CATEGORY_REGION = "region";
    public static final String CATEGORY_STATION = "station";

    public long id;
    public String name;
    public String city;
    public String country;
    public String countryCode;
    public String categoryKey;
    public String display;
    public double latitude;
    public double longitude;

    public StayOutboundSuggest()
    {

    }

    public StayOutboundSuggest(long id, String name)
    {
        this.id = id;
        this.name = name;
        this.display = name;
    }

    public StayOutboundSuggest(long id, String name, String city, String country, String countryCode //
        , String categoryKey, String display, double latitude, double longitude)
    {
        this.id = id;
        this.name = name;
        this.city = city;
        this.country = country;
        this.countryCode = countryCode;
        this.categoryKey = categoryKey;
        this.display = display;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public StayOutboundSuggest getClone()
    {
        StayOutboundSuggest stayOutboundSuggest = new StayOutboundSuggest();

        stayOutboundSuggest.id = id;

        if (DailyTextUtils.isTextEmpty(name) == false)
        {
            stayOutboundSuggest.name = name;
        }

        if (DailyTextUtils.isTextEmpty(city) == false)
        {
            stayOutboundSuggest.city = city;
        }

        if (DailyTextUtils.isTextEmpty(country) == false)
        {
            stayOutboundSuggest.country = country;
        }

        if (DailyTextUtils.isTextEmpty(countryCode) == false)
        {
            stayOutboundSuggest.countryCode = countryCode;
        }

        if (DailyTextUtils.isTextEmpty(categoryKey) == false)
        {
            stayOutboundSuggest.categoryKey = categoryKey;
        }

        if (DailyTextUtils.isTextEmpty(display) == false)
        {
            stayOutboundSuggest.display = display;
        }

        stayOutboundSuggest.latitude = latitude;
        stayOutboundSuggest.longitude = longitude;

        return stayOutboundSuggest;
    }
}
