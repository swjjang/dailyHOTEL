package com.daily.dailyhotel.entity;

import java.util.List;

public class StayAreaGroup extends AreaGroup<StayArea> implements AreaCategoryInterface
{
    private List<Category> mCategoryList;

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
