package com.daily.dailyhotel.entity;

import com.daily.base.util.DailyTextUtils;

public class Suggest
{
    public String id;
    public String name;
    public String city;
    public String country;
    public String countryCode;
    public String display;
    public double latitude;
    public double longitude;

    public Suggest()
    {

    }

    public Suggest(String id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public Suggest getClone()
    {
        Suggest suggest = new Suggest();

        if (DailyTextUtils.isTextEmpty(id) == false)
        {
            suggest.id = new String(id);
        }

        if (DailyTextUtils.isTextEmpty(name) == false)
        {
            suggest.id = new String(name);
        }

        if (DailyTextUtils.isTextEmpty(city) == false)
        {
            suggest.id = new String(city);
        }

        if (DailyTextUtils.isTextEmpty(country) == false)
        {
            suggest.id = new String(country);
        }

        if (DailyTextUtils.isTextEmpty(display) == false)
        {
            suggest.id = new String(display);
        }

        return suggest;
    }
}
