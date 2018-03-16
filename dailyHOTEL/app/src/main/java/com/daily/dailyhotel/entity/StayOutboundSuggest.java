package com.daily.dailyhotel.entity;

import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.repository.remote.model.StayOutboundSuggestData;

public class StayOutboundSuggest
{
    public static final String CATEGORY_AIRPORT = "airport";
    public static final String CATEGORY_HOTEL = "hotel";
    public static final String CATEGORY_POINT = "point";
    public static final String CATEGORY_REGION = "region";
    public static final String CATEGORY_STATION = "station";
    public static final String CATEGORY_LOCATION = "location";
    public static final String CATEGORY_DIRECT = "direct";

    public static final int MENU_TYPE_DIRECT = 1;
    public static final int MENU_TYPE_LOCATION = 2;
    public static final int MENU_TYPE_RECENTLY_SEARCH = 3;
    public static final int MENU_TYPE_RECENTLY_STAY = 4;
    public static final int MENU_TYPE_SUGGEST = 5;
    public static final int MENU_TYPE_POPULAR_AREA = 6;

    public long id;
    public String name;
    public String city;
    public String country;
    public String countryCode;
    public String categoryKey;
    public String display;
    public double latitude;
    public double longitude;
    public int menuType;

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
        stayOutboundSuggest.menuType = menuType;

        return stayOutboundSuggest;
    }

    public StayOutboundSuggestData getSuggestData()
    {
        StayOutboundSuggestData data = new StayOutboundSuggestData();
        data.id = id;
        data.name = name;
        data.city = city;
        data.country = country;
        data.countryCode = countryCode;
        data.categoryKey = categoryKey;
        data.display = display;
        data.lat = latitude;
        data.lng = longitude;

        return data;
    }
}
