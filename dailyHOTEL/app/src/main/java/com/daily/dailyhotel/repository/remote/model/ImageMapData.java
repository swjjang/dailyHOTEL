package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.ImageMap;

@JsonObject
public class ImageMapData
{
    @JsonField(name = "small")
    public String small;

    @JsonField(name = "big")
    public String big;

    @JsonField(name = "medium")
    public String medium;

    public ImageMapData()
    {

    }

    public ImageMap getImageMap()
    {
        ImageMap imageMap = new ImageMap();
        imageMap.smallUrl = small;
        imageMap.mediumUrl = medium;
        imageMap.bigUrl = big;

        return imageMap;
    }
}
