package com.daily.dailyhotel.repository.remote.model;

import android.util.SparseArray;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.StayOutboundDetail;
import com.daily.dailyhotel.entity.StayOutboundDetailImage;
import com.daily.dailyhotel.entity.StayOutboundRoom;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

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

    @JsonField(name = "hotelRating")
    public int hotelRating;

    @JsonField(name = "tripAdvisor")
    public TripAdvisorData tripAdvisorData;

    @JsonField(name = "latitude")
    public double latitude;

    @JsonField(name = "longitude")
    public double longitude;

    @JsonField(name = "details")
    public List<LinkedHashMap<String, List<String>>> detailList;

    @JsonField(name = "rooms")
    public List<RoomData> roomDataList;

    @JsonField(name = "amenities")
    public List<AmenityData> amenityDataList;

    @JsonField(name = "dailyHotelDetailImages")
    public List<ImageData> imageDataList;

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

        if (tripAdvisorData != null)
        {
            stayOutboundDetail.tripAdvisorRating = tripAdvisorData.tripAdvisorRating;
            stayOutboundDetail.tripAdvisorReviewCount = tripAdvisorData.tripAdvisorReviewCount;
        }

        stayOutboundDetail.latitude = latitude;
        stayOutboundDetail.longitude = longitude;

        LinkedHashMap<String, List<String>> detailsMap = new LinkedHashMap<>();

        for (LinkedHashMap<String, List<String>> detailMap : detailList)
        {
            detailsMap.putAll(detailMap);
        }

        stayOutboundDetail.setInformationMap(detailsMap);

        if (roomDataList != null && roomDataList.size() > 0)
        {
            List<StayOutboundRoom> stayOutboundRoomList = new ArrayList<>(roomDataList.size());

            for (RoomData roomData : roomDataList)
            {
                stayOutboundRoomList.add(roomData.getStayOutboundRoom());
            }

            stayOutboundDetail.setRoomList(stayOutboundRoomList);
        }


        if (amenityDataList != null && amenityDataList.size() > 0)
        {
            SparseArray<String> amenitySparseArray = new SparseArray<>(amenityDataList.size());

            for (AmenityData amenityData : amenityDataList)
            {
                amenitySparseArray.append(amenityData.amenityId, amenityData.amenity);
            }

            stayOutboundDetail.setAmenityList(amenitySparseArray);
        }

        if (imageDataList != null && imageDataList.size() > 0)
        {
            List<StayOutboundDetailImage> detailImageList = new ArrayList<>(imageDataList.size());

            for (ImageData imageData : imageDataList)
            {
                detailImageList.add(imageData.getStayOutboundDetailImage());
            }

            stayOutboundDetail.setImageList(detailImageList);
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

        public StayOutboundDetailImage getStayOutboundDetailImage()
        {
            StayOutboundDetailImage stayOutboundDetailImage = new StayOutboundDetailImage();
            stayOutboundDetailImage.caption = caption;
            stayOutboundDetailImage.setImageMap(imageMap.getImageMap());

            return stayOutboundDetailImage;
        }
    }

    @JsonObject
    static class RoomData
    {
        @JsonField(name = "rateKey")
        public String rateKey;

        @JsonField(name = "roomTypeCode")
        public String roomTypeCode;

        @JsonField(name = "rateCode")
        public String rateCode;

        @JsonField(name = "roomName")
        public String roomName;

        @JsonField(name = "roomBedTypeId")
        public int roomBedTypeId;

        @JsonField(name = "base")
        public int base;

        @JsonField(name = "total")
        public int total;

        @JsonField(name = "baseNightly")
        public int baseNightly;

        @JsonField(name = "nightly")
        public int nightly;

        @JsonField(name = "quotedOccupancy")
        public int quotedOccupancy;

        @JsonField(name = "rateOccupancyPerRoom")
        public int rateOccupancyPerRoom;

        @JsonField(name = "promotion")
        public boolean promotion;

        @JsonField(name = "promotionDescription")
        public String promotionDescription;

        @JsonField(name = "nonRefundable")
        public boolean nonRefundable;

        @JsonField(name = "nonRefundableDescription")
        public String nonRefundableDescription;

        @JsonField(name = "valueAddName")
        public String valueAddName;

        public RoomData()
        {

        }

        public StayOutboundRoom getStayOutboundRoom()
        {
            StayOutboundRoom stayOutboundRoom = new StayOutboundRoom();
            stayOutboundRoom.rateKey = rateKey;
            stayOutboundRoom.roomTypeCode = roomTypeCode;
            stayOutboundRoom.rateCode = rateCode;
            stayOutboundRoom.roomName = roomName;
            stayOutboundRoom.roomBedTypeId = roomBedTypeId;
            stayOutboundRoom.base = base;
            stayOutboundRoom.total = total;
            stayOutboundRoom.baseNightly = baseNightly;
            stayOutboundRoom.nightly = nightly;
            stayOutboundRoom.quotedOccupancy = quotedOccupancy;
            stayOutboundRoom.rateOccupancyPerRoom = rateOccupancyPerRoom;
            stayOutboundRoom.promotion = promotion;
            stayOutboundRoom.promotionDescription = promotionDescription;
            stayOutboundRoom.nonRefundable = nonRefundable;
            stayOutboundRoom.nonRefundableDescription = nonRefundableDescription;
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
