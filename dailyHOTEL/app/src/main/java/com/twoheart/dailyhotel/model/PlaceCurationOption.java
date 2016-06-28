package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.Constants;

public abstract class PlaceCurationOption implements Parcelable
{
    private Constants.SortType sortType = Constants.SortType.DEFAULT;

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
