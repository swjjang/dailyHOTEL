package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.StayOutbound;

import java.util.ArrayList;
import java.util.List;

@JsonObject
public class StayOutboundsData
{
    @JsonField(name = "hotelId")
    public int hotelId;

    @JsonField(name = "dailyHotels")
    public List<StayOutboundData> dailyHotels;

    @JsonField(name = "cacheKey")
    public String cacheKey;

    @JsonField(name = "cacheLocation")
    public String cacheLocation;

    @JsonField(name = "moreResultsAvailable")
    public boolean moreResultsAvailable;

    public StayOutboundsData()
    {

    }

    public List<StayOutbound> getStayOutboundList()
    {
        List<StayOutbound> stayOutboundList = new ArrayList<>();

        for (StayOutboundData stayOutboundsData : dailyHotels)
        {
            stayOutboundList.add(stayOutboundsData.getStayOutbound());
        }

        return stayOutboundList;
    }
}
