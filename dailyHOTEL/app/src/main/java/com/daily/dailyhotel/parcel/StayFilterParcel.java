package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.entity.StayFilter;

public class StayFilterParcel implements Parcelable
{
    private StayFilter mStayFilter;

    public StayFilterParcel(@NonNull StayFilter stayFilter)
    {
        if (stayFilter == null)
        {
            throw new NullPointerException("stayFilter == null");
        }

        mStayFilter = stayFilter;
    }

    public StayFilterParcel(Parcel in)
    {
        readFromParcel(in);
    }

    public StayFilter getStayFilter()
    {
        return mStayFilter;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(mStayFilter.person);
        dest.writeInt(mStayFilter.flagBedTypeFilters);
        dest.writeInt(mStayFilter.flagAmenitiesFilters);
        dest.writeInt(mStayFilter.flagRoomAmenitiesFilters);
        dest.writeString(mStayFilter.sortType == null ? null : mStayFilter.sortType.name());
        dest.writeString(mStayFilter.defaultSortType == null ? null : mStayFilter.defaultSortType.name());
    }

    private void readFromParcel(Parcel in)
    {
        mStayFilter = new StayFilter();

        mStayFilter.person = in.readInt();
        mStayFilter.flagBedTypeFilters = in.readInt();
        mStayFilter.flagAmenitiesFilters = in.readInt();
        mStayFilter.flagRoomAmenitiesFilters = in.readInt();

        String sortType = in.readString();

        if (DailyTextUtils.isTextEmpty(sortType) == false)
        {
            mStayFilter.sortType = StayFilter.SortType.valueOf(sortType);
        }

        String defaultSortType = in.readString();

        if (DailyTextUtils.isTextEmpty(defaultSortType) == false)
        {
            mStayFilter.defaultSortType = StayFilter.SortType.valueOf(defaultSortType);
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
