package com.twoheart.dailyhotel.network.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonIgnore;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class Sticker
{
    @JsonIgnore
    public static final int DEFAULT_SCREEN_WIDTH = 1440;

    @JsonField(name = "idx")
    public int index;

    @JsonField(name = "defaultImageUrl")
    public String defaultImageUrl;

    @JsonField(name = "lowResolutionImageUrl")
    public String lowResolutionImageUrl;

    public Sticker()
    {

    }
}
