package com.twoheart.dailyhotel.network.dto;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class Base_Dto<T>
{
    @JsonField(name = "msg_code")
    public int msgCode;

    @JsonField(name = "msg")
    public String msg;

    @JsonField(name = "data")
    public T data;
}
