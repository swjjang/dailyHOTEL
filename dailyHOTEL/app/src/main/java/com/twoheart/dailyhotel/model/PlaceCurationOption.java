package com.twoheart.dailyhotel.model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.Constants;

public abstract class PlaceCurationOption implements Parcelable
{
    private Constants.SortType prevSortType = Constants.SortType.DEFAULT; // Not Parcelable
    private Constants.SortType sortType = Constants.SortType.DEFAULT;

    private Province mProvince; // Not Parcelable
    private Location mLocation; // Not Parcelable

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

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(sortType.name());
    }

    protected void readFromParcel(Parcel in)
    {
        sortType = Constants.SortType.valueOf(in.readString());
    }

    @Override
    public int describeContents()
    {
        return 0;
    }
}
