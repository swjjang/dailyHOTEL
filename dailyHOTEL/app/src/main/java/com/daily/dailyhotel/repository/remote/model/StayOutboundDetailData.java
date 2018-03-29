package com.daily.dailyhotel.repository.remote.model;

import android.util.SparseArray;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.DetailImageInformation;
import com.daily.dailyhotel.entity.StayOutboundDetail;

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
    public float hotelRating;

    @JsonField(name = "tripAdvisor")
    public TripAdvisorData tripAdvisorData;

    @JsonField(name = "latitude")
    public double latitude;

    @JsonField(name = "longitude")
    public double longitude;

    @JsonField(name = "details")
    public List<LinkedHashMap<String, List<String>>> detailList;

    @JsonField(name = "amenities")
    public List<AmenityData> amenityDataList;

    @JsonField(name = "dailyHotelDetailImages")
    public List<ImageData> imageDataList;

    @JsonField(name = "rewardCard")
    public RewardCardData rewardCard;

    @JsonField(name = "configurations")
    public ConfigurationsData configurations;

    @JsonField(name = "myWish")
    public boolean myWish;

    @JsonField(name = "wishCount")
    public int wishCount;

    @JsonField(name = "couponDiscount")
    public int couponDiscount;

    @JsonField(name = "vendorTypes")
    public List<String> vendorTypes;

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
        stayOutboundDetail.rating = hotelRating;

        if (tripAdvisorData != null)
        {
            stayOutboundDetail.tripAdvisorRating = tripAdvisorData.tripAdvisorRating;
            stayOutboundDetail.tripAdvisorReviewCount = tripAdvisorData.tripAdvisorReviewCount;
        }

        stayOutboundDetail.latitude = latitude;
        stayOutboundDetail.longitude = longitude;
        stayOutboundDetail.myWish = myWish;
        stayOutboundDetail.wishCount = wishCount;
        stayOutboundDetail.couponPrice = couponDiscount;

        if (vendorTypes != null)
        {
            int size = vendorTypes.size();

            stayOutboundDetail.vendorTypes = new String[size];

            for (int i = 0; i < size; i++)
            {
                stayOutboundDetail.vendorTypes[i] = vendorTypes.get(i);
            }
        }

        LinkedHashMap<String, List<String>> detailsMap = new LinkedHashMap<>();

        for (LinkedHashMap<String, List<String>> detailMap : detailList)
        {
            detailsMap.putAll(detailMap);
        }

        stayOutboundDetail.setInformationMap(detailsMap);

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
            List<DetailImageInformation> detailImageList = new ArrayList<>(imageDataList.size());

            for (ImageData imageData : imageDataList)
            {
                detailImageList.add(imageData.getDetailImageInformation());
            }

            stayOutboundDetail.setImageList(detailImageList);
        }

        // 리워드
        if (rewardCard != null)
        {
            stayOutboundDetail.rewardStickerCount = rewardCard.rewardStickerCount;
        }

        if (configurations != null)
        {
            stayOutboundDetail.activeReward = configurations.activeReward;
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

        public DetailImageInformation getDetailImageInformation()
        {
            DetailImageInformation detailImageInformation = new DetailImageInformation();
            detailImageInformation.caption = caption;
            detailImageInformation.setImageMap(imageMap.getImageMap());

            return detailImageInformation;
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
