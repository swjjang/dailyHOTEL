package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.WishCount;

@JsonObject
public class StayOutboundWishCountData
{
    @JsonField(name = "wishOutboundCount")
    public int wishOutboundCount;

    public StayOutboundWishCountData()
    {

    }
}
