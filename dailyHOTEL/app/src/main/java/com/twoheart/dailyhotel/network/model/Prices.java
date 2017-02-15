package com.twoheart.dailyhotel.network.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by android_sam on 2017. 2. 15..
 */
@JsonObject
public class Prices
{
    @JsonField
    public int price;

    @JsonField
    public int discountPrice;
}
