package com.daily.dailyhotel.entity;

import java.util.List;
import java.util.Map;

public class Gourmets
{
    public int totalCount;
    public int searchMaxCount;

    private List<Gourmet> mGourmetList;
    private Map<String, GourmetFilter.Category> mCategoryMap;

    public void setGourmetList(List<Gourmet> gourmetList)
    {
        mGourmetList = gourmetList;
    }

    public List<Gourmet> getGourmetList()
    {
        return mGourmetList;
    }

    public void setCategoryMap(Map<String, GourmetFilter.Category> categoryMap)
    {
        mCategoryMap = categoryMap;
    }

    public Map<String, GourmetFilter.Category> getCategoryMap()
    {
        return mCategoryMap;
    }
}
