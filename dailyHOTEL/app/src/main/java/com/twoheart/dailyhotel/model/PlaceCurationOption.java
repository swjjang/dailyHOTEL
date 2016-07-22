package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.Constants;

public abstract class PlaceCurationOption implements Parcelable
{
    private Constants.SortType mDefaultSortType = Constants.SortType.DEFAULT;
    private Constants.SortType mSortType = mDefaultSortType;

    public abstract boolean isDefaultFilter();

    public void clear()
    {
        mSortType = mDefaultSortType;
    }

    public void setSortType(Constants.SortType mSortType)
    {
        if (mSortType == null)
        {
            mSortType = mDefaultSortType;
        }

        this.mSortType = mSortType;
    }

    public Constants.SortType getSortType()
    {
        if (mSortType == null)
        {
            mSortType = mDefaultSortType;
        }

        return mSortType;
    }

    public void setDefaultSortType(Constants.SortType defaultSortType)
    {
        mDefaultSortType = defaultSortType;
    }

    public boolean isDefaultSortType()
    {
        return mSortType == mDefaultSortType;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(mSortType.name());
    }

    protected void readFromParcel(Parcel in)
    {
        mSortType = Constants.SortType.valueOf(in.readString());
    }

    @Override
    public int describeContents()
    {
        return 0;
    }
}
