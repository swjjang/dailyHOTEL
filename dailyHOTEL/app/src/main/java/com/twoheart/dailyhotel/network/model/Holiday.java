package com.twoheart.dailyhotel.network.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class Holiday
{
    @JsonField(name = "date")
    public String date;

    @JsonField(name = "description")
    public String description;

    @JsonField(name = "holiday")
    public boolean holiday;
}
