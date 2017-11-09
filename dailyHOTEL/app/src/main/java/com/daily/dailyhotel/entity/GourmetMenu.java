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
    public int persons;
    public int minimumOrderQuantity;
    public int maximumOrderQuantity;
    public String startEatingTime;
    public String endEatingTime;
    public String readyTime;
    public String expiryTime;
    public int timeInterval;

    private List<DetailImageInformation> mDetailImageInformationList;
    private List<String> mMenuDetailList;

    // 멀티 메뉴
    public boolean visible;
    public int orderCount;
    private List<Integer> mOperationTimeList;

    public GourmetMenu()
    {
    }

    public List<DetailImageInformation> getImageList()
    {
        return mDetailImageInformationList;
    }

    public void setImageList(List<DetailImageInformation> detailImageInformationList)
    {
        mDetailImageInformationList = detailImageInformationList;
    }

    public DetailImageInformation getPrimaryImage()
    {
        if (mDetailImageInformationList == null || mDetailImageInformationList.size() == 0)
        {
            return null;
        }

        return mDetailImageInformationList.get(0);
    }

    public List<String> getMenuDetailList()
    {
        return mMenuDetailList;
    }

    public void setMenuDetailList(List<String> menuDetailList)
    {
        mMenuDetailList = menuDetailList;
    }

    public void setOperationTimeList(List<Integer> operationTimeList)
    {
        mOperationTimeList = operationTimeList;
    }

    public List<Integer> getOperationTimeList()
    {
        return mOperationTimeList;
    }
}