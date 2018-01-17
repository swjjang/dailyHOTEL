package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.TrueAwards;

/**
 * Created by android_sam on 2018. 1. 17..
 */
@JsonObject
public class TrueAwardsData
{
    @JsonField(name = "description")
    public String description;

    @JsonField(name = "idx")
    public String index;

    @JsonField(name = "imgUrl")
    public String imageUrl;

    @JsonField(name = "serviceType")
    public String serviceType;

    @JsonField(name = "title")
    public String title;

    public TrueAwards getTrueAwards()
    {
        TrueAwards trueAwards = new TrueAwards();

        trueAwards.description = description;
        trueAwards.index = index;
        trueAwards.imageUrl = imageUrl;
        trueAwards.serviceType = serviceType;
        trueAwards.title = title;

        return trueAwards;
    }
}
