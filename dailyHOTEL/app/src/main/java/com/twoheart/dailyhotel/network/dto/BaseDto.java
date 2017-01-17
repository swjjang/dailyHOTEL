package com.twoheart.dailyhotel.network.dto;

//@JsonObject
public class BaseDto<T>
{
    //    @JsonField
    public int msgCode;

    //    @JsonField
    public String msg;

    //    @JsonField
    public T data;
}
