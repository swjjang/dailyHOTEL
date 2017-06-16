package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by android_sam on 2017. 6. 15..
 */
@JsonObject
public class CategoryData
{
    @JsonField(name = "alias")
    public String alias;

    @JsonField(name = "count")
    public int count;

    @JsonField(name = "name")
    public String name;

}
