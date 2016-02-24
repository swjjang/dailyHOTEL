package com.twoheart.dailyhotel.model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.Constants;

import java.util.HashMap;
import java.util.Map;

public class GourmetCurationOption implements Parcelable
{
    private Constants.SortType prevSortType = Constants.SortType.DEFAULT; // Not Parcelable
    private Constants.SortType sortType = Constants.SortType.DEFAULT;

    private Province mProvince; // Not Parcelable
    private Location mLocation; // Not Parcelable

    private HashMap<String, Integer> mFilterMap;
    private HashMap<String, Integer> mCategoryCountMap;

    public GourmetCurationOption()
    {
        mCategoryCountMap = new HashMap<>();

        clear();
    }

    public GourmetCurationOption(Parcel in)
    {
        readFromParcel(in);
    }

    public void setCategoryCountMap(HashMap<String, Integer> categoryCountMap)
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

    public HashMap<String, Integer> getFilterMap()
    {
        return mFilterMap;
    }

    public void setFilterMap(HashMap<String, Integer> filterMap)
    {
        mFilterMap = filterMap;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(sortType.name());
        dest.writeSerializable(mFilterMap);
        dest.writeSerializable(mCategoryCountMap);
    }

    private void readFromParcel(Parcel in)
    {
        sortType = Constants.SortType.valueOf(in.readString());

        mFilterMap = (HashMap<String, Integer>) in.readSerializable();
        mCategoryCountMap = (HashMap<String, Integer>) in.readSerializable();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public GourmetCurationOption createFromParcel(Parcel in)
        {
            return new GourmetCurationOption(in);
        }

        @Override
        public GourmetCurationOption[] newArray(int size)
        {
            return new GourmetCurationOption[size];
        }
    };
}
