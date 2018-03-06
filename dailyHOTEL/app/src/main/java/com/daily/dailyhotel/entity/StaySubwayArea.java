package com.daily.dailyhotel.entity;

import java.util.List;

public class StaySubwayArea extends Area implements AreaCategoryInterface
{
    private List<Category> mCategoryList;

    public StaySubwayArea(int index, String name)
    {
        this.index = index;
        this.name = name;
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
