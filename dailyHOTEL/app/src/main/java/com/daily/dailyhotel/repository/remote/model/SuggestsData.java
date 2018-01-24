package com.daily.dailyhotel.repository.remote.model;

import android.content.Context;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.entity.StayOutboundSuggest;
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

    public List<StayOutboundSuggest> getSuggestList(Context context)
    {
        final int SUGGEST_MAX_COUNT = 5;

        List<StayOutboundSuggest> list = new ArrayList<>();

        if (context == null)
        {
            return list;
        }

        List<StayOutboundSuggest> regionList = getSuggestList(context.getString(R.string.label_stay_outbound_suggest_region), regionSuggestDataList, SUGGEST_MAX_COUNT);

        if (regionList != null)
        {
            list.addAll(regionList);
        }

        List<StayOutboundSuggest> hotelList = getSuggestList(context.getString(R.string.label_stay_outbound_suggest_hotel), hotelSuggestDataList, SUGGEST_MAX_COUNT);

        if (hotelList != null)
        {
            list.addAll(hotelList);
        }

        List<StayOutboundSuggest> pointList = getSuggestList(context.getString(R.string.label_stay_outbound_suggest_point), pointSuggestDataList, SUGGEST_MAX_COUNT);

        if (pointList != null)
        {
            list.addAll(pointList);
        }

        List<StayOutboundSuggest> airportList = getSuggestList(context.getString(R.string.label_stay_outbound_suggest_airport), airportSuggestDataList, SUGGEST_MAX_COUNT);

        if (airportList != null)
        {
            list.addAll(airportList);
        }

        List<StayOutboundSuggest> stationList = getSuggestList(context.getString(R.string.label_stay_outbound_suggest_station), stationSuggestDataList, SUGGEST_MAX_COUNT);

        if (stationList != null)
        {
            list.addAll(stationList);
        }

        return list;
    }

    public List<StayOutboundSuggest> getRegionSuggestList(Context context)
    {
        List<StayOutboundSuggest> regionStayOutboundSuggestList = new ArrayList<>();

        if (regionSuggestDataList == null || regionSuggestDataList.size() == 0)
        {
            return regionStayOutboundSuggestList;
        }

        int count = regionSuggestDataList.size();

        for (SuggestData suggestData : regionSuggestDataList)
        {
            regionStayOutboundSuggestList.add(suggestData.getSuggests());
        }

        return regionStayOutboundSuggestList;
    }

    private List<StayOutboundSuggest> getSuggestList(String title, List<SuggestData> suggestDataList, int maxCount)
    {
        if (suggestDataList == null || suggestDataList.size() == 0 || DailyTextUtils.isTextEmpty(title) == true)
        {
            return null;
        }

        List<StayOutboundSuggest> list = new ArrayList<>();

        list.add(new StayOutboundSuggest(0, title));

        int size = Math.min(suggestDataList.size(), maxCount);

        for (int i = 0; i < size; i++)
        {
            list.add(suggestDataList.get(i).getSuggests());
        }

        return list;
    }
}
