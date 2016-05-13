package com.twoheart.dailyhotel.model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.Constants;

public abstract class PlaceCurationOption implements Parcelable
{
    private Constants.SortType sortType = Constants.SortType.DEFAULT;

    private Province mProvince;
    private Location mLocation; // Not Parcelable

    public void clear()
    {
        sortType = Constants.SortType.DEFAULT;
    }

    public void setSortType(Constants.SortType sortType)
    {
        if (sortType == null)
        {
            sortType = Constants.SortType.DEFAULT;
        }

        this.sortType = sortType;
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
        dest.writeParcelable(mProvince, flags);
    }

    protected void readFromParcel(Parcel in)
    {
        sortType = Constants.SortType.valueOf(in.readString());
        mProvince = in.readParcelable(Province.class.getClassLoader());
    }

    @Override
    public int describeContents()
    {
        return 0;
    }
}
