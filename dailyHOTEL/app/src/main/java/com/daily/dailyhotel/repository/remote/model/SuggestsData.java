package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.Suggest;

import java.util.ArrayList;
import java.util.List;

@JsonObject
public class SuggestsData
{
    @JsonField(name = "station")
    public List<SuggestData> station;

    @JsonField(name = "hotel")
    public List<SuggestData> hotel;

    @JsonField(name = "region")
    public List<SuggestData> region;

    @JsonField(name = "point")
    public List<SuggestData> point;

    public SuggestsData()
    {

    }

    public List<Suggest> getSuggestList()
    {
        List<Suggest> list = new ArrayList<>();

        return list;
    }
}
