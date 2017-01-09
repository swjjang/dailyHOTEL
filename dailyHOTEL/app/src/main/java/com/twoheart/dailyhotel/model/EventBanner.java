package com.twoheart.dailyhotel.model;

import com.twoheart.dailyhotel.util.Util;

import org.json.JSONObject;

public class EventBanner
{
    public int index;
    public long checkInTime;
    public int nights;
    private boolean mIsHotel;
    public String imageUrl;
    public String webLink;
    public String name;

    public EventBanner(JSONObject jsonObject, String url) throws Exception
    {
        name = jsonObject.getString("name");

        if (jsonObject.isNull("linkUrl") == false)
        {
            // 웹링크인 경우
            webLink = jsonObject.getString("linkUrl");
        } else
        {
            // 딥링크인 경우
            if (jsonObject.isNull("hotelIdx") == false)
            {
                mIsHotel = true;
                index = jsonObject.getInt("hotelIdx");
            } else if (jsonObject.isNull("fnbRestaurantIdx") == false)
            {
                mIsHotel = false;
                index = jsonObject.getInt("fnbRestaurantIdx");
            } else
            {
                throw new NullPointerException();
            }

            checkInTime = jsonObject.getLong("dateCheckIn");
            nights = jsonObject.getInt("nights");
        }

        imageUrl = url + jsonObject.getString("imagePath");
    }

    public boolean isDeepLink()
    {
        return Util.isTextEmpty(webLink) == true;
    }

    public boolean isHotel()
    {
        return mIsHotel;
    }
}
