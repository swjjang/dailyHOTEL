package com.twoheart.dailyhotel.network.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

/**
 * Created by android_sam on 2017. 1. 31..
 */
@JsonObject
public class HomeEvents
{
    @JsonField(name = "serverDate")
    public String serverDate;

    @JsonField(name = "list")
    public List<HomeEvent> list;
}
