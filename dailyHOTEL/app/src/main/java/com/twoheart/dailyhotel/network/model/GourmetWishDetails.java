package com.twoheart.dailyhotel.network.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by android_sam on 2017. 5. 24..
 */
@JsonObject
public class GourmetWishDetails
{
    @JsonField(name = "category")
    public String category;

    @JsonField(name = "subCategory")
    public String subCategory;

    @JsonField(name = "sticker")
    public Sticker sticker;

    @JsonField(name = "persons")
    public int persons;

    @JsonField(name = "truevr")
    public boolean truevr;
}
