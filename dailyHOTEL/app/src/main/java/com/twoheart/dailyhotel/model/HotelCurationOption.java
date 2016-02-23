package com.twoheart.dailyhotel.model;

import android.location.Location;

import com.twoheart.dailyhotel.util.Constants;

import java.util.ArrayList;

public class HotelCurationOption
{
    private Constants.SortType prevSortType = Constants.SortType.DEFAULT;
    private Constants.SortType sortType = Constants.SortType.DEFAULT;

    public int person;
    public int flagFilters;
    private Category mCategory;
    private Province mProvince;
    private Location mLocation;

    private ArrayList<HotelFilters> mHotelFilterList;

    public HotelCurationOption()
    {
        mHotelFilterList = new ArrayList<>();

        clear();
    }

    public void setFilterList(ArrayList<HotelFilters> arrayList)
    {
        mHotelFilterList.clear();
        mHotelFilterList.addAll(arrayList);
    }

    public int getFilterCount(int flag, int person)
    {
        flagFilters = flag;

        int count = 0;

        for (HotelFilters hotelFilters : mHotelFilterList)
        {
            if (hotelFilters.isFiltered(flag, person) == true)
            {
                count++;
                continue;
            }
        }

        return count;
    }

    public void clear()
    {
        prevSortType = Constants.SortType.DEFAULT;
        sortType = Constants.SortType.DEFAULT;

        person = 2;
        flagFilters = 0;

        mCategory = Category.ALL;
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

    public Category getCategory()
    {
        return mCategory;
    }

    public void setCategory(Category category)
    {
        mCategory = category;
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
}
