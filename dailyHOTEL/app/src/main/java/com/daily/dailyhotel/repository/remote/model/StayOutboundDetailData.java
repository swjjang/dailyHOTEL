package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.StayOutbound;
import com.daily.dailyhotel.entity.StayOutboundDetail;
import com.daily.dailyhotel.entity.StayOutbounds;

import java.util.ArrayList;
import java.util.List;

@JsonObject
public class StayOutboundDetailData
{
    @JsonField(name = "hotelId")
    public int hotelId;

    @JsonField(name = "name")
    public String name;

    @JsonField(name = "countryCode")
    public String countryCode;

    @JsonField(name = "cacheLocation")
    public String cacheLocation;

    @JsonField(name = "address")
    public String address;

    @JsonField(name = "city")
    public String city;

    @JsonField(name = "hotelRating")
    public int hotelRating;

    @JsonField(name = "tripAdvisorRating")
    public String tripAdvisorRating;

    @JsonField(name = "latitude")
    public double latitude;

    @JsonField(name = "longitude")
    public double longitude;

    @JsonField(name = "propertyInformation")
    public String propertyInformation;

    @JsonField(name = "areaInformation")
    public String areaInformation;

    @JsonField(name = "propertyDescription")
    public String propertyDescription;

    @JsonField(name = "hotelPolicy")
    public String hotelPolicy;

    @JsonField(name = "roomInformation")
    public String roomInformation;

    @JsonField(name = "checkInInstructions")
    public String checkInInstructions;

    @JsonField(name = "specialCheckInInstructions")
    public String specialCheckInInstructions;

    @JsonField(name = "amenitiesDescription")
    public String amenitiesDescription;

    @JsonField(name = "businessAmenitiesDescription")
    public String businessAmenitiesDescription;

    @JsonField(name = "roomDetailDescription")
    public String roomDetailDescription;

    @JsonField(name = "renovationsDescription")
    public String renovationsDescription;

    public StayOutboundDetailData()
    {

    }

    public StayOutboundDetail getStayOutboundDetail()
    {
        StayOutboundDetail stayOutboundDetail = new StayOutboundDetail();

        return stayOutboundDetail;
    }
}
