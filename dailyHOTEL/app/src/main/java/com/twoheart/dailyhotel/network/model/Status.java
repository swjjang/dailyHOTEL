package com.twoheart.dailyhotel.network.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class Status
{
    @JsonField(name = "isSuspend")
    public boolean isSuspend;

    @JsonField(name = "messageTitle")
    public String messageTitle;

    @JsonField(name = "messageBody")
    public String messageBody;
}
