package com.twoheart.dailyhotel.network.dto;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class BaseDto<T>
{
    @JsonField
    public int msgCode;

    @JsonField
    public String msg;

    @JsonField
    public T data;
}
