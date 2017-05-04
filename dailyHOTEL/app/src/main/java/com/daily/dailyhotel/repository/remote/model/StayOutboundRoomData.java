package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.StayOutboundDetail;

import java.util.List;

@JsonObject
public class StayOutboundRoomData
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

    public StayOutboundRoomData()
    {

    }

    public StayOutboundDetail getStayOutboundDetail()
    {
        StayOutboundDetail stayOutboundDetail = new StayOutboundDetail();

        return stayOutboundDetail;
    }
}
