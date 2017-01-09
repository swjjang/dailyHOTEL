package com.twoheart.dailyhotel.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

@JsonObject
public class BaseModelList<E>
{
    @JsonField
    public int msgCode;

    @JsonField
    public String msg;

    @JsonField
    public List<E> data;
}
