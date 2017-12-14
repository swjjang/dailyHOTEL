package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.WishCount;

@JsonObject
public class WishCountData
{
    @JsonField(name = "wishGourmetCount")
    public int wishGourmetCount;

    @JsonField(name = "wishHotelCount")
    public int wishHotelCount;

    public WishCountData()
    {

    }

    public WishCount getWishCount()
    {
        WishCount wishCount = new WishCount();
        wishCount.wishStayCount = wishHotelCount;
        wishCount.wishGourmetCount = wishGourmetCount;

        return wishCount;
    }
}
