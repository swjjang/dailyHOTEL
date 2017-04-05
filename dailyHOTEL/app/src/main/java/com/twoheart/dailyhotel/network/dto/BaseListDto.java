package com.twoheart.dailyhotel.network.dto;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

@JsonObject
public class BaseListDto<E>
{
    @JsonField(name = "msgCode")
    public int msgCode;

    @JsonField(name = "msg")
    public String msg;

    @JsonField(name = "data")
    public List<E> data;
}
