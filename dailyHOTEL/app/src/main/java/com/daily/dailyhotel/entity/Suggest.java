package com.daily.dailyhotel.entity;

import com.daily.base.util.DailyTextUtils;

public class Suggest
{
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
            suggest.name = new String(name);
        }

        if (DailyTextUtils.isTextEmpty(city) == false)
        {
            suggest.city = new String(city);
        }

        if (DailyTextUtils.isTextEmpty(country) == false)
        {
            suggest.country = new String(country);
        }

        if (DailyTextUtils.isTextEmpty(countryCode) == false)
        {
            suggest.countryCode = new String(countryCode);
        }

        if (DailyTextUtils.isTextEmpty(categoryKey) == false)
        {
            suggest.categoryKey = new String(categoryKey);
        }

        if (DailyTextUtils.isTextEmpty(display) == false)
        {
            suggest.display = new String(display);
        }

        suggest.latitude = latitude;
        suggest.longitude = longitude;

        return suggest;
    }
}
