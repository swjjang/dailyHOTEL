package com.twoheart.dailyhotel.network.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class Holiday
{
    @JsonField
    public String date;

    @JsonField
    public String description;

    @JsonField
    public boolean holiday;

    public Holiday()
    {
    }
}
