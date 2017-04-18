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

        boolean hasStation = station != null && station.size() > 0;
        boolean hasRegion = region != null && region.size() > 0;

        if (hasStation || hasRegion)
        {
            list.add(new Suggest(null, "도시/지역"));

            if (hasStation == true)
            {
                for (SuggestData suggestData : station)
                {
                    list.add(suggestData.getSuggests());
                }
            }

            if (hasRegion == true)
            {
                for (SuggestData suggestData : region)
                {
                    list.add(suggestData.getSuggests());
                }
            }
        }

        if (hotel != null && hotel.size() > 0)
        {
            list.add(new Suggest(null, "호텔"));

            for (SuggestData suggestData : hotel)
            {
                list.add(suggestData.getSuggests());
            }
        }

        if (point != null && point.size() > 0)
        {
            list.add(new Suggest(null, "주요지점"));

            for (SuggestData suggestData : point)
            {
                list.add(suggestData.getSuggests());
            }
        }

        return list;
    }
}
