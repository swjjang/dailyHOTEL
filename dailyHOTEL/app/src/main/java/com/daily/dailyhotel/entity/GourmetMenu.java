package com.daily.dailyhotel.entity;

import java.util.List;

public class GourmetMenu
{
    public int index;
    public int saleIndex;
    public String name;
    public int price;
    public int discountPrice;
    public String menuBenefit;
    public String needToKnow;
    public String reserveCondition;
    public String openTime;
    public String closeTime;
    public String lastOrderTime;
    public String menuSummary;

    public int minimumOrderQuantity;
    public int maximumOrderQuantity;
    public String startEatingTime;
    public String endEatingTime;
    public String readyTime;
    public String expiryTime;
    public int timeInterval;


    private List<ImageInformation> mImageInformationList;
    private List<String> mMenuDetailList;

    public GourmetMenu()
    {
    }

    public List<ImageInformation> getImageList()
    {
        return mImageInformationList;
    }

    public void setImageList(List<ImageInformation> imageInformationList)
    {
        mImageInformationList = imageInformationList;
    }

    public ImageInformation getPrimaryImage()
    {
        if (mImageInformationList == null || mImageInformationList.size() == 0)
        {
            return null;
        }

        return mImageInformationList.get(0);
    }

    public List<String> getMenuDetailList()
    {
        return mMenuDetailList;
    }

    public void setMenuDetailList(List<String> menuDetailList)
    {
        mMenuDetailList = menuDetailList;
    }
}