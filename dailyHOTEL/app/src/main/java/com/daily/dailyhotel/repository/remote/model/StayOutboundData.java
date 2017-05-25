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
    public float hotelRating;

    @JsonField(name = "locationDescription")
    public String locationDescription;

    @JsonField(name = "latitude")
    public double latitude;

    @JsonField(name = "longitude")
    public double longitude;

    @JsonField(name = "distance")
    public double distance;

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
        stayOutbound.distance = distance;
        stayOutbound.promo = promo;
        stayOutbound.locationDescription = locationDescription;
        stayOutbound.nightlyRate = nightlyRate;
        stayOutbound.nightlyBaseRate = nightlyBaseRate;
        stayOutbound.total = total;
        stayOutbound.setImageMap(imageMapData.getImageMap());

        if (tripAdvisorData != null)
        {
            stayOutbound.tripAdvisorRating = tripAdvisorData.tripAdvisorRating;
            stayOutbound.tripAdvisorReviewCount = tripAdvisorData.tripAdvisorReviewCount;
        }

        return stayOutbound;
    }
}
