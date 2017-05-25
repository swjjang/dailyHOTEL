package com.twoheart.dailyhotel.network.model;

import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.annotation.OnJsonParseComplete;

/**
 * Created by android_sam on 2017. 5. 24..
 */
@JsonObject
public class GourmetWishItem extends PlaceWishItem<GourmetWishDetails>
{
    @Override
    public GourmetWishDetails getDetails()
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
