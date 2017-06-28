package com.daily.dailyhotel.entity;

import com.daily.base.util.DailyTextUtils;

public class Suggest
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

    public Suggest()
    {

    }

    public Suggest(long id, String name)
    {
        this.id = id;
        this.name = name;
        this.display = name;
    }

    public Suggest getClone()
    {
        Suggest suggest = new Suggest();

        suggest.id = id;

        if (DailyTextUtils.isTextEmpty(name) == false)
        {
            suggest.name = name;
        }

        if (DailyTextUtils.isTextEmpty(city) == false)
        {
            suggest.city = city;
        }

        if (DailyTextUtils.isTextEmpty(country) == false)
        {
            suggest.country = country;
        }

        if (DailyTextUtils.isTextEmpty(countryCode) == false)
        {
            suggest.countryCode = countryCode;
        }

        if (DailyTextUtils.isTextEmpty(categoryKey) == false)
        {
            suggest.categoryKey = categoryKey;
        }

        if (DailyTextUtils.isTextEmpty(display) == false)
        {
            suggest.display = display;
        }

        suggest.latitude = latitude;
        suggest.longitude = longitude;

        return suggest;
    }
}
