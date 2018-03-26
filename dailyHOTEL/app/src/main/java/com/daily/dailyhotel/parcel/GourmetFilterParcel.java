package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.entity.GourmetFilter;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class GourmetFilterParcel implements Parcelable
{
    private GourmetFilter mFilter;

    public GourmetFilterParcel(@NonNull GourmetFilter filter)
    {
        if (filter == null)
        {
            throw new NullPointerException("stayFilter == null");
        }

        mFilter = filter;
    }

    public GourmetFilterParcel(Parcel in)
    {
        readFromParcel(in);
    }

    public GourmetFilter getFilter()
    {
        return mFilter;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeSerializable(mFilter.getCategoryFilterMap());
        dest.writeSerializable(mFilter.getCategoryMap());
        dest.writeInt(mFilter.flagTimeFilter);
        dest.writeInt(mFilter.flagAmenitiesFilters);
        dest.writeString(mFilter.sortType == null ? null : mFilter.sortType.name());
        dest.writeString(mFilter.defaultSortType == null ? null : mFilter.defaultSortType.name());
    }

    private void readFromParcel(Parcel in)
    {
        mFilter = new GourmetFilter();

        mFilter.getCategoryFilterMap().putAll((HashMap) in.readSerializable());
        mFilter.getCategoryMap().putAll((LinkedHashMap) in.readSerializable());
        mFilter.flagTimeFilter = in.readInt();
        mFilter.flagAmenitiesFilters = in.readInt();

        String sortType = in.readString();

        if (DailyTextUtils.isTextEmpty(sortType) == false)
        {
            mFilter.sortType = GourmetFilter.SortType.valueOf(sortType);
        }

        String defaultSortType = in.readString();

        if (DailyTextUtils.isTextEmpty(defaultSortType) == false)
        {
            mFilter.defaultSortType = GourmetFilter.SortType.valueOf(defaultSortType);
        }
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public GourmetFilterParcel createFromParcel(Parcel in)
        {
            return new GourmetFilterParcel(in);
        }

        @Override
        public GourmetFilterParcel[] newArray(int size)
        {
            return new GourmetFilterParcel[size];
        }

    };
}
