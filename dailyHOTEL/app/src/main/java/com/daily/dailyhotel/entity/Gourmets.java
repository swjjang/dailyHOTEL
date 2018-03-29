package com.daily.dailyhotel.entity;

import java.util.LinkedHashMap;
import java.util.List;

public class Gourmets
{
    public int totalCount;
    public int searchMaxCount;

    private List<Gourmet> mGourmetList;
    private LinkedHashMap<String, GourmetFilter.Category> mCategoryMap;

    public void setGourmetList(List<Gourmet> gourmetList)
    {
        mGourmetList = gourmetList;
    }

    public List<Gourmet> getGourmetList()
    {
        return mGourmetList;
    }

    public void setCategoryMap(LinkedHashMap<String, GourmetFilter.Category> categoryMap)
    {
        mCategoryMap = categoryMap;
    }

    public LinkedHashMap<String, GourmetFilter.Category> getCategoryMap()
    {
        return mCategoryMap;
    }
}
