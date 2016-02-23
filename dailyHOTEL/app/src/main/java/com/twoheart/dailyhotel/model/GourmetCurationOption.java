package com.twoheart.dailyhotel.model;

import android.location.Location;

import com.twoheart.dailyhotel.util.Constants;

import java.util.HashMap;
import java.util.Map;

public class GourmetCurationOption
{
    private Constants.SortType prevSortType = Constants.SortType.DEFAULT;
    private Constants.SortType sortType = Constants.SortType.DEFAULT;

    private Province mProvince;
    private Location mLocation;

    private Map<String, Integer> mFilterMap;
    private Map<String, Integer> mCategoryCountMap;

    public GourmetCurationOption()
    {
        mCategoryCountMap = new HashMap<>();

        clear();
    }

    public void setCategoryCountMap(Map<String, Integer> categoryCountMap)
    {
        mCategoryCountMap.clear();

        if (mCategoryCountMap != null)
        {
            mCategoryCountMap.putAll(categoryCountMap);
        }
    }

    public void clear()
    {
        prevSortType = Constants.SortType.DEFAULT;
        sortType = Constants.SortType.DEFAULT;
    }

    public void setSortType(Constants.SortType sortType)
    {
        prevSortType = this.sortType;
        this.sortType = sortType;
    }

    public void restoreSortType()
    {
        sortType = prevSortType;
    }

    public Constants.SortType getSortType()
    {
        if (sortType == Constants.SortType.DISTANCE && mLocation == null)
        {
            sortType = Constants.SortType.DEFAULT;
        }

        return sortType;
    }

    public Province getProvince()
    {
        return mProvince;
    }

    public void setProvince(Province province)
    {
        mProvince = province;
    }

    public Location getLocation()
    {
        return mLocation;
    }

    public void setLocation(Location location)
    {
        mLocation = location;
    }

    public Map<String, Integer> getFilterMap()
    {
        return mFilterMap;
    }
}
