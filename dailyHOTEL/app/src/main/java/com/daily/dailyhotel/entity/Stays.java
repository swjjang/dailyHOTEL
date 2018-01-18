package com.daily.dailyhotel.entity;

import java.util.List;

public class Stays extends Configurations
{
    public int totalCount;
    public int searchMaxCount;

    private List<StayCategory> mStayCategoryList;
    private List<Stay> mStayList;

    public void setStayCategoryList(List<StayCategory> stayCategoryList)
    {
        mStayCategoryList = stayCategoryList;
    }

    public List<StayCategory> getStayCategoryList()
    {
        return mStayCategoryList;
    }

    public void setStayList(List<Stay> stayList)
    {
        mStayList = stayList;
    }

    public List<Stay> getStayList()
    {
        return mStayList;
    }
}
