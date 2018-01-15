package com.daily.dailyhotel.entity;

/**
 * Created by android_sam on 2016. 11. 25..
 */

public class ReviewItem
{
    public int itemIdx;
    public String itemName;
    public String serviceType;
    public String useEndDate;
    public String useStartDate;

    private ImageMap mImageMap;

    public void setImageMap(ImageMap imageMap)
    {
        mImageMap = imageMap;
    }

    public ImageMap getImageMap()
    {
        return mImageMap;
    }
}
