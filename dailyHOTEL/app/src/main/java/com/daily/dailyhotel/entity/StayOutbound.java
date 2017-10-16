package com.daily.dailyhotel.entity;

public class StayOutbound
{
    public int index;
    public String name;
    public String nameEng;
    public float rating;
    public double latitude;
    public double longitude;
    public double distance;
    public boolean promo;
    public String locationDescription;
    public int nightlyRate;
    public int nightlyBaseRate;
    public int total;
    public ImageMap mImageMap;
    public float tripAdvisorRating;
    public int tripAdvisorReviewCount;
    public String city;
    public String vendorType;
    public boolean dailyReward; // 데일리 리워드 아이콘 여부

    public StayOutbound()
    {

    }

    public ImageMap getImageMap()
    {
        return mImageMap;
    }

    public void setImageMap(ImageMap imageMap)
    {
        mImageMap = imageMap;
    }
}
