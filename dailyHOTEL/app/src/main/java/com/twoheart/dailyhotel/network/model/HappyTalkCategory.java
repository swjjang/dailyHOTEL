package com.twoheart.dailyhotel.network.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class HappyTalkCategory
{
    @JsonField
    public String id;

    @JsonField
    public String name;

    @JsonField
    public String id2;

    @JsonField
    public String name2;

    @JsonField
    public String check;

    public HappyTalkCategory()
    {

    }
}
