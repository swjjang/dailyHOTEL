package com.daily.dailyhotel.entity;

import java.util.List;

public class GourmetMenu
{
    public int index;
    public int saleIdx;
    public String ticketName;
    public int price;
    public int discountPrice;
    public String menuBenefit;
    public String needToKnow;
    public String openTime;
    public String closeTime;
    public String lastOrderTime;
    public String menuSummary;
    private List<GourmetMenuImage> mGourmetMenuImageList;
    private List<String> mMenuDetailList;

    private int mPrimaryImageIndex;

    public GourmetMenu()
    {
    }

    public List<GourmetMenuImage> getImageList()
    {
        return mGourmetMenuImageList;
    }

    public void setImageList(List<GourmetMenuImage> gourmetMenuImageList)
    {
        mGourmetMenuImageList = gourmetMenuImageList;
    }

    public GourmetMenuImage getPrimaryImage()
    {
        if (mGourmetMenuImageList == null || mGourmetMenuImageList.size() == 0)
        {
            return null;
        }

        return mGourmetMenuImageList.get(mPrimaryImageIndex);
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