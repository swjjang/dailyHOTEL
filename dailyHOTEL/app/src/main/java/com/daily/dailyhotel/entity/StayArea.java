package com.daily.dailyhotel.entity;

import java.util.List;

public class StayArea extends Area implements AreaCategoryInterface
{
    public static final int ALL = -1;

    private List<Category> mCategoryList;

    public StayArea(int index, String name)
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
