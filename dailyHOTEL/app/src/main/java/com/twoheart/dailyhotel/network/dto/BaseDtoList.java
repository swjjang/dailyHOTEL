package com.twoheart.dailyhotel.network.dto;

import java.util.List;

//@JsonObject
public class BaseDtoList<E>
{
    //    @JsonField
    public int msgCode;

    //    @JsonField
    public String msg;

    //    @JsonField
    public List<E> data;
}
