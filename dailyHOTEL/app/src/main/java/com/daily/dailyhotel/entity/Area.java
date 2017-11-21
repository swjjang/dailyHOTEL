package com.daily.dailyhotel.entity;

import java.util.List;

public class Area
{
    public int index;
    public String name;
    public int sequence;

    private Province mProvince;
    private List<Category> mCategoryList;

    public List<Category> getCategoryList()
    {
        return mCategoryList;
    }

    public void setCategoryList(List<Category> categoryList)
    {
        mCategoryList = categoryList;
    }

    public Province getProvince()
    {
        return mProvince;
    }

    public void setProvince(Province province)
    {
        mProvince = province;
    }
}
