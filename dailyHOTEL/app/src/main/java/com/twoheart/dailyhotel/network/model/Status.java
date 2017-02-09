package com.twoheart.dailyhotel.network.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class Status
{
    @JsonField
    public boolean isSuspend;

    @JsonField
    public String messageTitle;

    @JsonField
    public String messageBody;
}
