package com.daily.dailyhotel.entity;

public class DetailImageInformation
{
    public String caption;
    ImageMap mImageMap;

    public DetailImageInformation()
    {

    }

    public void setImageMap(ImageMap imageMap)
    {
        mImageMap = imageMap;
    }

    public ImageMap getImageMap()
    {
        return mImageMap;
    }
}
