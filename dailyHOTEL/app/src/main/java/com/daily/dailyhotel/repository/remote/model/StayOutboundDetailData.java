package com.daily.dailyhotel.repository.remote.model;

import android.util.SparseArray;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.StayOutboundDetail;
import com.daily.dailyhotel.entity.StayOutboundRoom;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonObject
public class StayOutboundDetailData
{
    @JsonField(name = "hotelId")
    public int hotelId;

    @JsonField(name = "name")
    public String name;

    @JsonField(name = "nameEng")
    public String nameEng;

    @JsonField(name = "address")
    public String address;

    @JsonField(name = "grade")
    public int hotelRating;

    @JsonField(name = "tripAdvisorRating")
    public String tripAdvisorRating;

    @JsonField(name = "latitude")
    public double latitude;

    @JsonField(name = "longitude")
    public double longitude;

    @JsonField(name = "details")
    public LinkedHashMap<String, List<String>> details;

    @JsonField(name = "rooms")
    public List<RoomData> rooms;

    @JsonField(name = "amenities")
    public List<AmenityData> amenities;

    public StayOutboundDetailData()
    {

    }

    public StayOutboundDetail getStayOutboundDetail()
    {
        StayOutboundDetail stayOutboundDetail = new StayOutboundDetail();
        stayOutboundDetail.index = hotelId;
        stayOutboundDetail.name = name;
        stayOutboundDetail.nameEng = nameEng;
        stayOutboundDetail.address = address;
        stayOutboundDetail.grade = hotelRating;
        stayOutboundDetail.ratingValue = tripAdvisorRating;
        stayOutboundDetail.latitude = latitude;
        stayOutboundDetail.longitude = longitude;
        stayOutboundDetail.setInformationMap(details);

        if (rooms != null && rooms.size() > 0)
        {
            List<StayOutboundRoom> stayOutboundRoomList = new ArrayList<>(rooms.size());

            for (RoomData roomData : rooms)
            {
                stayOutboundRoomList.add(roomData.getStayOutboundRoom());
            }

            stayOutboundDetail.setRoomList(stayOutboundRoomList);
        }


        if (amenities != null && amenities.size() > 0)
        {
            SparseArray<String> amenitySparseArray = new SparseArray<>(amenities.size());

            for (AmenityData amenityData : amenities)
            {
                amenitySparseArray.append(amenityData.amenityId, amenityData.amenity);
            }

            stayOutboundDetail.setAmenityList(amenitySparseArray);
        }

        return stayOutboundDetail;
    }

    @JsonObject
    static class ImageData
    {
        @JsonField(name = "caption")
        public String caption;

        @JsonField(name = "imageMap")
        public ImageMapData imageMap;

        public ImageData()
        {

        }
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
    static class RoomData
    {
        @JsonField(name = "rateKey")
        public int rateKey;

        @JsonField(name = "roomTypeCode")
        public String roomTypeCode;

        @JsonField(name = "rateCode")
        public String rateCode;

        @JsonField(name = "roomName")
        public String roomName;

        @JsonField(name = "baseKrw")
        public String baseKrw;

        @JsonField(name = "totalKrw")
        public String totalKrw;

        @JsonField(name = "baseNightlyKrw")
        public String baseNightlyKrw;

        @JsonField(name = "nightlyKrw")
        public String nightlyKrw;

        @JsonField(name = "promotion")
        public boolean promotion;

        @JsonField(name = "nonRefundable")
        public boolean nonRefundable;

        @JsonField(name = "nonRefundableDescription")
        public String nonRefundableDescription;

        @JsonField(name = "bedTypeName")
        public String bedTypeName;

        @JsonField(name = "valueAddName")
        public int valueAddName;

        public RoomData()
        {

        }

        public StayOutboundRoom getStayOutboundRoom()
        {
            StayOutboundRoom stayOutboundRoom = new StayOutboundRoom();
            stayOutboundRoom.rateKey = rateKey;
            stayOutboundRoom.roomTypeCode = roomTypeCode;
            stayOutboundRoom.roomName = roomName;
            stayOutboundRoom.baseKrw = baseKrw;
            stayOutboundRoom.totalKrw = totalKrw;
            stayOutboundRoom.baseNightlyKrw = baseNightlyKrw;
            stayOutboundRoom.nightlyKrw = nightlyKrw;
            stayOutboundRoom.promotion = promotion;
            stayOutboundRoom.nonRefundable = nonRefundable;
            stayOutboundRoom.nonRefundableDescription = nonRefundableDescription;
            stayOutboundRoom.bedTypeName = bedTypeName;
            stayOutboundRoom.valueAddName = valueAddName;

            return stayOutboundRoom;
        }
    }

    @JsonObject
    static class AmenityData
    {
        @JsonField(name = "amenityId")
        public int amenityId;

        @JsonField(name = "amenity")
        public String amenity;

        public AmenityData()
        {

        }
    }
}
