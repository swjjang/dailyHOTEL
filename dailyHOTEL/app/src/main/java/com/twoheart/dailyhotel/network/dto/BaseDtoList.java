package com.twoheart.dailyhotel.network.dto;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

@JsonObject
public class BaseDtoList<E>
{
    @JsonField
    public int msgCode;

    @JsonField
    public String msg;

    @JsonField
    public List<E> data;
}
