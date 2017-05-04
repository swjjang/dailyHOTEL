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

    @JsonField(name = "address")
    public String address;

    @JsonField(name = "hotelRating")
    public int hotelRating;

    @JsonField(name = "tripAdvisorRating")
    public String tripAdvisorRating;

    @JsonField(name = "latitude")
    public double latitude;

    @JsonField(name = "longitude")
    public double longitude;

    @JsonField(name = "checkInTime")
    public String checkInTime;

    @JsonField(name = "checkOutTime")
    public String checkOutTime;

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

    @JsonField(name = "diningDescription")
    public String diningDescription;

    @JsonField(name = "roomDetailDescription")
    public String roomDetailDescription;

    @JsonField(name = "renovationsDescription")
    public String renovationsDescription;

    @JsonField(name = "dailyHotelDetailImages")
    public List<ImageData> dailyHotelDetailImages;

    @JsonField(name = "dailyHotelDetailPropertyAmenities")
    public List<AmenityData> dailyHotelDetailPropertyAmenities;

    public StayOutboundDetailData()
    {

    }

    public StayOutboundDetail getStayOutboundDetail()
    {
        StayOutboundDetail stayOutboundDetail = new StayOutboundDetail();

        return stayOutboundDetail;
    }

    @JsonObject
    static class ImageData
    {
        @JsonField(name = "name")
        public String name;

        @JsonField(name = "caption")
        public String caption;

        @JsonField(name = "url")
        public String url;

        public ImageData()
        {

        }
    }

    @JsonObject
    static class AmenityData
    {
        @JsonField(name = "amenityId")
        public int amenityId;

        @JsonField(name = "amenity")
        public int amenity;

        public AmenityData()
        {

        }
    }

    @JsonObject
    static class RoomData
    {
        @JsonField(name = "hotelId")
        public int hotelId;

        @JsonField(name = "hotelName")
        public String hotelName;

        @JsonField(name = "roomTypeCode")
        public String roomTypeCode;

        @JsonField(name = "smokingPreferences")
        public String smokingPreferences;

        @JsonField(name = "quotedOccupancy")
        public int quotedOccupancy;

        @JsonField(name = "rateOccupancyPerRoom")
        public int rateOccupancyPerRoom;

        @JsonField(name = "description")
        public String description;

        @JsonField(name = "nonRefundable")
        public boolean nonRefundable;

        public RoomData()
        {

        }
    }
}
