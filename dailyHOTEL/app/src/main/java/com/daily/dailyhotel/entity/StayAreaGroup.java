package com.daily.dailyhotel.entity;

import java.util.List;

public class StayAreaGroup extends Area implements AreaCategoryInterface
{
    private List<Category> mCategoryList;

    private List<StayArea> mAreaList;

    public List<StayArea> getAreaList()
    {
        return mAreaList;
    }

    public int getAreaCount()
    {
        return mAreaList == null ? 0 : mAreaList.size();
    }

    public void setAreaList(List<StayArea> areaList)
    {
        mAreaList = areaList;
    }

    @Override
    public List<Category> getCategoryList()
    {
        return mCategoryList;
    }

    @Override
    public void setCategoryList(List<Category> categoryList)
    {
        mCategoryList = categoryList;
    }

    @Override
    public int getCategoryCount()
    {
        return mCategoryList == null ? 0 : mCategoryList.size();
    }
}
