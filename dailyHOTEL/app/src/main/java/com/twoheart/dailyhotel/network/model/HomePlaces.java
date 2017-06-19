package com.twoheart.dailyhotel.network.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.base.util.DailyTextUtils;

import java.util.List;

/**
 * Created by android_sam on 2017. 2. 7..
 */
@JsonObject
public class HomePlaces
{
    @JsonField(name = "items")
    public List<HomePlace> items;

    @JsonField(name = "imgUrl")
    public String imageBaseUrl;

    public List<HomePlace> getHomePlaceList()
    {
        if (items == null || items.size() == 0)
        {
            return null;
        }

        List<HomePlace> homePlaceList = items;

        for (HomePlace homeItem : homePlaceList)
        {
            if (DailyTextUtils.isTextEmpty(homeItem.imageUrl) == false)
            {
                homeItem.imageUrl = imageBaseUrl + homeItem.imageUrl;
            }
        }

        return homePlaceList;
    }
}
