package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.StayOutbound;

import java.util.List;

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

    @JsonField(name = "city")
    public String city;

    @JsonField(name = "vendorTypes")
    public List<String> vendorTypeList;

    @JsonField(name = "discountRate")
    public int discountRate;

    @JsonField(name = "provideRewardSticker")
    public boolean provideRewardSticker;

    @JsonField(name = "myWish")
    public boolean myWish;

    @JsonField(name = "createAt")
    public String createAtWish; // 위시 목록일때만 내려옴.

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
        stayOutbound.city = city;
        stayOutbound.discountRate = discountRate;
        stayOutbound.setVendorTypeList(vendorTypeList);

        if (tripAdvisorData != null)
        {
            stayOutbound.tripAdvisorRating = tripAdvisorData.tripAdvisorRating;
            stayOutbound.tripAdvisorReviewCount = tripAdvisorData.tripAdvisorReviewCount;
        }

        stayOutbound.provideRewardSticker = provideRewardSticker;
        stayOutbound.myWish = myWish;
        stayOutbound.createAtWish = createAtWish;

        return stayOutbound;
    }
}
