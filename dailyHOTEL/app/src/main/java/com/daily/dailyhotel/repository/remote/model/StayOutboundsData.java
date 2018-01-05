package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.StayOutbound;
import com.daily.dailyhotel.entity.StayOutbounds;

import java.util.ArrayList;
import java.util.List;

@JsonObject
public class StayOutboundsData
{
    @JsonField(name = "dailyHotels")
    public List<StayOutboundData> stayOutboundDataList;

    @JsonField(name = "cacheKey")
    public String cacheKey;

    @JsonField(name = "cacheLocation")
    public String cacheLocation;

    @JsonField(name = "customerSessionId")
    public String customerSessionId;

    @JsonField(name = "moreResultsAvailable")
    public boolean moreResultsAvailable;

    @JsonField(name = "numberOfRoomsRequested")
    public int numberOfRoomsRequested;

    @JsonField(name = "configurations")
    public ConfigurationsData configurations;

    public StayOutboundsData()
    {

    }

    public StayOutbounds getStayOutbounds()
    {
        StayOutbounds stayOutbounds = new StayOutbounds();
        List<StayOutbound> stayOutboundList = new ArrayList<>();

        for (StayOutboundData stayOutboundsData : stayOutboundDataList)
        {
            stayOutboundList.add(stayOutboundsData.getStayOutbound());
        }

        stayOutbounds.setStayOutbound(stayOutboundList);

        stayOutbounds.cacheKey = cacheKey;
        stayOutbounds.cacheLocation = cacheLocation;
        stayOutbounds.customerSessionId = customerSessionId;
        stayOutbounds.moreResultsAvailable = moreResultsAvailable;

        if (configurations != null)
        {
            stayOutbounds.activeReward = configurations.activeReward;
        }

        return stayOutbounds;
    }
}
