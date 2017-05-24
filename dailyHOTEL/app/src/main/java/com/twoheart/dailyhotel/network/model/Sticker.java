package com.twoheart.dailyhotel.network.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonIgnore;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class Sticker
{
    @JsonIgnore
    public static final int DEFAULT_SCREEN_WIDTH = 720;

    @JsonIgnore
    public static final int LARGE_SCREEN_WIDTH = 1440;

    @JsonIgnore
    public static final float MEDIUM_RATE = 0.75f;

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
