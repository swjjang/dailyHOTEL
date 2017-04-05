package com.twoheart.dailyhotel.network.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class HappyTalkCategory
{
    @JsonField(name = "id")
    public String id;

    @JsonField(name = "name")
    public String name;

    @JsonField(name = "id2")
    public String id2;

    @JsonField(name = "name2")
    public String name2;

    @JsonField(name = "check")
    public String check;

    public HappyTalkCategory()
    {

    }
}
