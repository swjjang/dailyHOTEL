package com.daily.dailyhotel.repository.remote.model;

import android.content.Context;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.entity.Suggest;
import com.twoheart.dailyhotel.R;

import java.util.ArrayList;
import java.util.List;

@JsonObject
public class SuggestsData
{
    @JsonField(name = "station")
    public List<SuggestData> stationSuggestDataList;

    @JsonField(name = "hotel")
    public List<SuggestData> hotelSuggestDataList;

    @JsonField(name = "region")
    public List<SuggestData> regionSuggestDataList;

    @JsonField(name = "point")
    public List<SuggestData> pointSuggestDataList;

    @JsonField(name = "airport")
    public List<SuggestData> airportSuggestDataList;

    public SuggestsData()
    {

    }

    public List<Suggest> getSuggestList(Context context)
    {
        final int SUGGEST_MAX_COUNT = 5;

        List<Suggest> list = new ArrayList<>();

        if(context == null)
        {
            return list;
        }

        List<Suggest> regionList = getSuggestList(context.getString(R.string.label_stay_outbound_suggest_region), regionSuggestDataList, SUGGEST_MAX_COUNT);

        if (regionList != null)
        {
            list.addAll(regionList);
        }

        List<Suggest> hotelList = getSuggestList(context.getString(R.string.label_stay_outbound_suggest_hotel), hotelSuggestDataList, SUGGEST_MAX_COUNT);

        if (hotelList != null)
        {
            list.addAll(hotelList);
        }

        List<Suggest> pointList = getSuggestList(context.getString(R.string.label_stay_outbound_suggest_point), pointSuggestDataList, SUGGEST_MAX_COUNT);

        if (pointList != null)
        {
            list.addAll(pointList);
        }

        List<Suggest> airportList = getSuggestList(context.getString(R.string.label_stay_outbound_suggest_airport), airportSuggestDataList, SUGGEST_MAX_COUNT);

        if (airportList != null)
        {
            list.addAll(airportList);
        }

        List<Suggest> stationList = getSuggestList(context.getString(R.string.label_stay_outbound_suggest_station), stationSuggestDataList, SUGGEST_MAX_COUNT);

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

        list.add(new Suggest(0, title));

        int size = Math.min(suggestDataList.size(), maxCount);

        for (int i = 0; i < size; i++)
        {
            list.add(suggestDataList.get(i).getSuggests());
        }

        return list;
    }
}
