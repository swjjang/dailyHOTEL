package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

/**
 * Created by android_sam on 2017. 6. 20..
 */
@JsonObject
public class GourmetFilterData
{
    @JsonField(name = "categories")
    public List<GourmetCategoryData> gourmetCategoryDataList;
}
