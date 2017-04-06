package com.twoheart.dailyhotel.network.dto;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class BaseDto<T>
{
    @JsonField(name = "msgCode")
    public int msgCode;

    @JsonField(name = "msg")
    public String msg;

    @JsonField(name = "data")
    public T data;
}
