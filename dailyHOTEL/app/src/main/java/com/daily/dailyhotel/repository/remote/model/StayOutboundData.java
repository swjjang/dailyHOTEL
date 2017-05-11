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

    @JsonField(name = "engName")
    public String engName;

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

    @JsonField(name = "promo")
    public boolean promo;

    @JsonField(name = "promoDescription")
    public String promoDescription;

    @JsonField(name = "totalKrw")
    public String totalKrw;

    @JsonField(name = "nightlyRateKrw")
    public String nightlyRateKrw;

    @JsonField(name = "nightlyBaseRateKrw")
    public String nightlyBaseRateKrw;

    @JsonField(name = "imageMap")
    public ImageMapData imageMap;

    public StayOutboundData()
    {

    }

    public StayOutbound getStayOutbound()
    {
        StayOutbound stayOutbound = new StayOutbound();

        stayOutbound.index = hotelId;
        stayOutbound.name = name;
        stayOutbound.engName = engName;
        stayOutbound.rating = hotelRating;
        stayOutbound.latitude = latitude;
        stayOutbound.longitude = longitude;

        if (promo == true)
        {
            stayOutbound.promoDescription = promoDescription;
        }

        stayOutbound.locationDescription = locationDescription;
        stayOutbound.nightlyRateKrw = Integer.parseInt(nightlyRateKrw);
        stayOutbound.nightlyBaseRateKrw = Integer.parseInt(nightlyBaseRateKrw);
        stayOutbound.totalKrw = Integer.parseInt(totalKrw);

        stayOutbound.hdpiImageUrl = imageMap.medium;
        stayOutbound.xxhdpiImageUrl = imageMap.big;

        return stayOutbound;
    }

    @JsonObject
    static class ImageMapData
    {
        @JsonField(name = "small")
        public String small;

        @JsonField(name = "big")
        public String big;

        @JsonField(name = "medium")
        public String medium;

        public ImageMapData()
        {

        }
    }
}
