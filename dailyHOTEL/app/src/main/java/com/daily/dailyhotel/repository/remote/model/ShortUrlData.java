package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class ShortUrlData
{
    @JsonField(name = "kind")
    public String kind;

    @JsonField(name = "id")
    public String id;

    @JsonField(name = "longUrl")
    public String longUrl;

    public ShortUrlData()
    {

    }
}
