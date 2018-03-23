package com.daily.dailyhotel.entity;

import java.util.List;

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
    public boolean provideRewardSticker; // 데일리 리워드 아이콘 여부
    private List<String> mVendorTypeList;
    public int discountRate;
    public boolean myWish;
    public String createdWishDateTime; // ISO-8601
    public boolean dailyChoice;
    public String couponDiscountPriceText;

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

    public void setVendorTypeList(List<String> vendorTypeList)
    {
        mVendorTypeList = vendorTypeList;
    }

    public List<String> getVendorTypeList()
    {
        return mVendorTypeList;
    }
}
