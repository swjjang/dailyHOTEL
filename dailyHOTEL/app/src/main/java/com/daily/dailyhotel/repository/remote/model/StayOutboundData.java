package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.StayOutbound;

@JsonObject
public class StayOutboundData
{
    @JsonField(name = "hotelId")
    public int hotelId;

    @JsonField(name = "name")
    public String name;

    @JsonField(name = "hotelRating")
    public int hotelRating;

    @JsonField(name = "hotelRatingDisplay")
    public String hotelRatingDisplay;

    @JsonField(name = "locationDescription")
    public String locationDescription;

    @JsonField(name = "latitude")
    public double latitude;

    @JsonField(name = "longitude")
    public double longitude;

    @JsonField(name = "promoDescription")
    public String promoDescription;

    @JsonField(name = "totalKrw")
    public String totalKrw;

    @JsonField(name = "thumbNailUrl")
    public String thumbNailUrl;

    public StayOutboundData()
    {

    }

    public StayOutbound getStayOutbound()
    {
        StayOutbound stayOutbound = new StayOutbound();

        stayOutbound.index = hotelId;
        stayOutbound.name = name;
        stayOutbound.rating = hotelRating;
        stayOutbound.latitude = latitude;
        stayOutbound.longitude = longitude;
        stayOutbound.promoDescription = promoDescription;
        stayOutbound.averageKrw = totalKrw;
        stayOutbound.totalKrw = totalKrw;
        stayOutbound.thumbNailUrl = thumbNailUrl;

        return stayOutbound;
    }
}
