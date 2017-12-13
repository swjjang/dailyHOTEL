package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.StayFilterCount;

@JsonObject
public class StayFilterCountData
{
    @JsonField(name = "hotelSalesCount")
    public int hotelSalesCount;

    @JsonField(name = "searchMaxCount")
    public int searchMaxCount;

    public StayFilterCount getFilterCount()
    {
        StayFilterCount stayFilterCount = new StayFilterCount();
        stayFilterCount.searchCount = hotelSalesCount;
        stayFilterCount.searchCountOfMax = searchMaxCount;

        return stayFilterCount;
    }
}
