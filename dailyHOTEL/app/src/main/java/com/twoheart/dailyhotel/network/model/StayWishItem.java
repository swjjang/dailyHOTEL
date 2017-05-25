package com.twoheart.dailyhotel.network.model;

import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.annotation.OnJsonParseComplete;

/**
 * Created by android_sam on 2017. 5. 24..
 */
@JsonObject
public class StayWishItem extends PlaceWishItem<StayWishDetails>
{
    @Override
    public StayWishDetails getDetails()
    {
        return details;
    }

    @OnJsonParseComplete
    @Override
    void onParseComplete()
    {
        super.onParseComplete();
    }
}
