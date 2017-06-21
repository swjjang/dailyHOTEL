package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by android_sam on 2017. 6. 20..
 */
@JsonObject
public class GourmetCategoryData
{
    @JsonField(name = "name")
    public String name;

    @JsonField(name = "code")
    public int code;

    @JsonField(name = "sequence")
    public int sequence;
}
