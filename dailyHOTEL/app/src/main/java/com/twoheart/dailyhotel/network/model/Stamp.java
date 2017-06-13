package com.twoheart.dailyhotel.network.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

@JsonObject
public class Stamp
{
    @JsonField(name = "list")
    public List<StampHistory> list;

    @JsonField(name = "count")
    public int count;

    public Stamp()
    {
    }
}
