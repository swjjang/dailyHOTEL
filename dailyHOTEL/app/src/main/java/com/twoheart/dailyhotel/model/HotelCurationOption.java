package com.twoheart.dailyhotel.model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.Constants;

import java.util.ArrayList;

public class HotelCurationOption implements Parcelable
{
    private Constants.SortType prevSortType = Constants.SortType.DEFAULT; // Not Parcelable
    private Constants.SortType sortType = Constants.SortType.DEFAULT;

    public int person;
    public int flagFilters;
    private Category mCategory; // Not Parcelable
    private Province mProvince; // Not Parcelable
    private Location mLocation; // Not Parcelable

    private ArrayList<HotelFilters> mHotelFilterList;

    public HotelCurationOption()
    {
        mHotelFilterList = new ArrayList<>();

        clear();
    }

    public HotelCurationOption(Parcel in)
    {
        mHotelFilterList = new ArrayList<>();

        readFromParcel(in);
    }

    public void setFilterList(ArrayList<HotelFilters> arrayList)
    {
        mHotelFilterList.clear();
        mHotelFilterList.addAll(arrayList);
    }

    public ArrayList<HotelFilters> getFilterList()
    {
        return mHotelFilterList;
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

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(person);
        dest.writeInt(flagFilters);

        dest.writeString(sortType.name());

        dest.writeTypedList(mHotelFilterList);
    }

    private void readFromParcel(Parcel in)
    {
        person = in.readInt();
        flagFilters = in.readInt();

        sortType = Constants.SortType.valueOf(in.readString());

        mHotelFilterList = new ArrayList<>();
        in.readTypedList(mHotelFilterList, HotelFilters.CREATOR);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public HotelCurationOption createFromParcel(Parcel in)
        {
            return new HotelCurationOption(in);
        }

        @Override
        public HotelCurationOption[] newArray(int size)
        {
            return new HotelCurationOption[size];
        }
    };
}
