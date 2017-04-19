package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.base.util.DailyTextUtils;
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

    @JsonField(name = "airport")
    public List<SuggestData> airport;

    public SuggestsData()
    {

    }

    public List<Suggest> getSuggestList()
    {
        final int SUGGEST_MAX_COUNT = 5;

        List<Suggest> list = new ArrayList<>();

        List<Suggest> regionList = getSuggestList("도시/지역", region, SUGGEST_MAX_COUNT);

        if (regionList != null)
        {
            list.addAll(regionList);
        }

        List<Suggest> hotelList = getSuggestList("호텔", hotel, SUGGEST_MAX_COUNT);

        if (hotelList != null)
        {
            list.addAll(hotelList);
        }

        List<Suggest> pointList = getSuggestList("주요지점", point, SUGGEST_MAX_COUNT);

        if (pointList != null)
        {
            list.addAll(pointList);
        }

        List<Suggest> airportList = getSuggestList("공항", airport, SUGGEST_MAX_COUNT);

        if (airportList != null)
        {
            list.addAll(airportList);
        }

        List<Suggest> stationList = getSuggestList("역", station, SUGGEST_MAX_COUNT);

        if (stationList != null)
        {
            list.addAll(stationList);
        }

        return list;
    }

    private List<Suggest> getSuggestList(String title, List<SuggestData> suggestDataList, int maxCount)
    {
        if (suggestDataList == null || suggestDataList.size() == 0 || DailyTextUtils.isTextEmpty(title) == true)
        {
            return null;
        }

        List<Suggest> list = new ArrayList<>();

        list.add(new Suggest(null, title));

        int size = Math.min(suggestDataList.size(), maxCount);

        for (int i = 0; i < size; i++)
        {
            list.add(suggestDataList.get(i).getSuggests());
        }

        return list;
    }
}
