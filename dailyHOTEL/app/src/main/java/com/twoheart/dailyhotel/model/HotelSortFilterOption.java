package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.Constants;

import java.util.ArrayList;

public class HotelSortFilterOption implements Parcelable
{
    private Constants.SortType prevSortType = Constants.SortType.DEFAULT;
    private Constants.SortType sortType = Constants.SortType.DEFAULT;

    public int person;
    public int flagFilters;
    private Category mCategory;
    public Province mProvince;

    private ArrayList<HotelFilters> mHotelFilterList;

    public HotelSortFilterOption()
    {
        clear();
    }

    public HotelSortFilterOption(Parcel in)
    {
        readFromParcel(in);

        mHotelFilterList = new ArrayList<>();
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

    public boolean equals(HotelSortFilterOption sortFilterInformation)
    {
        if (sortFilterInformation == null)
        {
            return false;
        }

        boolean result = true;

        if (sortType != sortFilterInformation.sortType)
        {
            return false;
        }

        if (person != sortFilterInformation.person)
        {
            return false;
        }

        if (flagFilters != sortFilterInformation.flagFilters)
        {
            return false;
        }

        return result;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(person);
        dest.writeInt(flagFilters);

        dest.writeString(prevSortType.name());
        dest.writeString(sortType.name());

        dest.writeTypedList(mHotelFilterList);
    }

    private void readFromParcel(Parcel in)
    {
        person = in.readInt();
        flagFilters = in.readInt();

        prevSortType = Constants.SortType.valueOf(in.readString());
        sortType = Constants.SortType.valueOf(in.readString());

        mHotelFilterList = new ArrayList<>();
        in.readTypedList(mHotelFilterList, HotelFilters.CREATOR);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public HotelSortFilterOption createFromParcel(Parcel in)
        {
            return new HotelSortFilterOption(in);
        }

        @Override
        public HotelSortFilterOption[] newArray(int size)
        {
            return new HotelSortFilterOption[size];
        }
    };
}
