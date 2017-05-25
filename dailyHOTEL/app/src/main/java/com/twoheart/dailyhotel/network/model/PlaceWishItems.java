package com.twoheart.dailyhotel.network.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

/**
 * Created by android_sam on 2017. 5. 24..
 */
@JsonObject
public class PlaceWishItems<E>
{
    @JsonField(name = "items")
    public List<E> items;

    @JsonField(name = "imgUrl")
    public String imgUrl;
}
