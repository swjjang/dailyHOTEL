package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.GourmetFilterCount;

@JsonObject
public class GourmetFilterCountData
{
    @JsonField(name = "gourmetSalesCount")
    public int gourmetSalesCount;

    @JsonField(name = "searchMaxCount")
    public int searchMaxCount;

    public GourmetFilterCount getFilterCount()
    {
        GourmetFilterCount filterCount = new GourmetFilterCount();
        filterCount.searchCount = gourmetSalesCount;
        filterCount.searchCountOfMax = searchMaxCount;

        return filterCount;
    }
}
