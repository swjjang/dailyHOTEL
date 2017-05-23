package com.daily.dailyhotel.entity;

public class StayOutbound
{
    public int index;
    public String name;
    public String nameEng;
    public float rating;
    public double latitude;
    public double longitude;
    public boolean promo;
    public String locationDescription;
    public String distance;
    public int nightlyRate;
    public int nightlyBaseRate;
    public int total;
    public ImageMap mImageMap;
    public float tripAdvisorRating;
    public int tripAdvisorReviewCount;

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
