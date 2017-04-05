package com.twoheart.dailyhotel.network.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

/**
 * Created by android_sam on 2017. 2. 7..
 */
@JsonObject
public class HomePlaces<E>
{
    @JsonField(name = "items")
    public List<E> items;

    @JsonField(name = "imgUrl")
    public String imageBaseUrl;
}
