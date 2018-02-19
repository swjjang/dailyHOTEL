package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

/**
 * Created by android_sam on 2018. 1. 31..
 */
@JsonObject
public class GoogleAddressComponentsData
{
    @JsonField(name = "long_name")
    public String longName;

    @JsonField(name = "short_name")
    public String shortName;

    @JsonField(name = "types")
    public List<String> types;
}
