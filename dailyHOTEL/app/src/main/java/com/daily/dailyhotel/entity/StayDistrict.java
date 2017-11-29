package com.daily.dailyhotel.entity;

import java.util.List;

public class StayDistrict extends District
{
    private List<Category> mCategoryList; // 스테이의 카테고리 목록

    private List<StayTown> mTownList;

    public List<Category> getCategoryList()
    {
        return mCategoryList;
    }

    public void setCategoryList(List<Category> categoryList)
    {
        mCategoryList = categoryList;
    }

    public List<StayTown> getTownList()
    {
        return mTownList;
    }

    public int getTownCount()
    {
        return mTownList == null ? 0 : mTownList.size();
    }

    public void setTownList(List<StayTown> townList)
    {
        mTownList = townList;
    }
}
