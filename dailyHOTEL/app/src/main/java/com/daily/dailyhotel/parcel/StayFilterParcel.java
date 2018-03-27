package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.entity.StayFilter;

public class StayFilterParcel implements Parcelable
{
    private StayFilter mFilter;

    public StayFilterParcel(@NonNull StayFilter filter)
    {
        if (filter == null)
        {
            throw new NullPointerException("stayFilter == null");
        }

        mFilter = filter;
    }

    public StayFilterParcel(Parcel in)
    {
        readFromParcel(in);
    }

    public StayFilter getFilter()
    {
        return mFilter;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(mFilter.person);
        dest.writeInt(mFilter.flagBedTypeFilters);
        dest.writeInt(mFilter.flagAmenitiesFilters);
        dest.writeInt(mFilter.flagRoomAmenitiesFilters);
        dest.writeString(mFilter.sortType == null ? null : mFilter.sortType.name());
        dest.writeString(mFilter.defaultSortType == null ? null : mFilter.defaultSortType.name());
    }

    private void readFromParcel(Parcel in)
    {
        mFilter = new StayFilter();

        mFilter.person = in.readInt();
        mFilter.flagBedTypeFilters = in.readInt();
        mFilter.flagAmenitiesFilters = in.readInt();
        mFilter.flagRoomAmenitiesFilters = in.readInt();

        String sortType = in.readString();

        if (DailyTextUtils.isTextEmpty(sortType) == false)
        {
            mFilter.sortType = StayFilter.SortType.valueOf(sortType);
        }

        String defaultSortType = in.readString();

        if (DailyTextUtils.isTextEmpty(defaultSortType) == false)
        {
            mFilter.defaultSortType = StayFilter.SortType.valueOf(defaultSortType);
        }
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public StayFilterParcel createFromParcel(Parcel in)
        {
            return new StayFilterParcel(in);
        }

        @Override
        public StayFilterParcel[] newArray(int size)
        {
            return new StayFilterParcel[size];
        }

    };
}
