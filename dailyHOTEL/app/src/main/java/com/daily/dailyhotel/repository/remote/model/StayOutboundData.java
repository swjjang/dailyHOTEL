package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.ImageMap;
import com.daily.dailyhotel.entity.StayOutbound;

@JsonObject
public class StayOutboundData
{
    @JsonField(name = "hotelId")
    public int hotelId;

    @JsonField(name = "name")
    public String name;

    @JsonField(name = "nameEng")
    public String nameEng;

    @JsonField(name = "hotelRating")
    public int hotelRating;

    @JsonField(name = "locationDescription")
    public String locationDescription;

    @JsonField(name = "latitude")
    public double latitude;

    @JsonField(name = "longitude")
    public double longitude;

    @JsonField(name = "distance")
    public String distance;

    @JsonField(name = "promo")
    public boolean promo;

    @JsonField(name = "total")
    public int total;

    @JsonField(name = "nightlyRate")
    public int nightlyRate;

    @JsonField(name = "nightlyBaseRate")
    public int nightlyBaseRate;

    @JsonField(name = "imageMap")
    public ImageMapData imageMapData;

    @JsonField(name = "tripAdvisor")
    public TripAdvisorData tripAdvisorData;

    public StayOutboundData()
    {

    }

    public StayOutbound getStayOutbound()
    {
        StayOutbound stayOutbound = new StayOutbound();

        stayOutbound.index = hotelId;
        stayOutbound.name = name;
        stayOutbound.nameEng = nameEng;
        stayOutbound.rating = hotelRating;
        stayOutbound.latitude = latitude;
        stayOutbound.longitude = longitude;
        stayOutbound.promo = promo;
        stayOutbound.locationDescription = locationDescription;
        stayOutbound.nightlyRate = nightlyRate;
        stayOutbound.nightlyBaseRate = nightlyBaseRate;
        stayOutbound.total = total;

        ImageMap imageMap = null;

        if (imageMapData != null)
        {
            imageMap = new ImageMap();
            imageMap.smallUrl = imageMapData.small;
            imageMap.mediumUrl = imageMapData.medium;
            imageMap.bigUrl = imageMapData.big;
        }

        if (tripAdvisorData != null)
        {
            stayOutbound.tripAdvisorRating = tripAdvisorData.tripAdvisorRating;
            stayOutbound.tripAdvisorReviewCount = tripAdvisorData.tripAdvisorReviewCount;
        }

        stayOutbound.setImageMap(imageMap);

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

    @JsonObject
    static class TripAdvisorData
    {
        @JsonField(name = "tripAdvisorRating")
        public int tripAdvisorRating;

        @JsonField(name = "tripAdvisorReviewCount")
        public int tripAdvisorReviewCount;

        public TripAdvisorData()
        {

        }
    }
}
