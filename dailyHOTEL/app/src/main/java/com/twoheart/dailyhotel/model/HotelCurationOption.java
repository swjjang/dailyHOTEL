package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.Constants;

import java.util.ArrayList;

public class HotelCurationOption extends PlaceCurationOption
{
    public int person;
    public int flagFilters;
    private Category mCategory; // Not Parcelable

    private ArrayList<HotelFilters> mHotelFiltersList;

    public HotelCurationOption()
    {
        super();

        mHotelFiltersList = new ArrayList<>();

        clear();
    }

    public HotelCurationOption(Parcel in)
    {
        mHotelFiltersList = new ArrayList<>();

        readFromParcel(in);
    }

    public Category getCategory()
    {
        return mCategory;
    }

    public void setCategory(Category category)
    {
        mCategory = category;
    }

    public void setFilterList(ArrayList<HotelFilters> arrayList)
    {
        mHotelFiltersList.clear();
        mHotelFiltersList.addAll(arrayList);
    }

    public ArrayList<HotelFilters> getFilterList()
    {
        return mHotelFiltersList;
    }

    public void clear()
    {
        super.clear();

        person = HotelFilter.MIN_PERSON;
        flagFilters = HotelFilters.FLAG_HOTEL_FILTER_BED_NONE;

        mCategory = Category.ALL;
    }

    public boolean isDefaultFilter()
    {
        if (getSortType() != Constants.SortType.DEFAULT//
            || person != HotelFilter.MIN_PERSON//
            || flagFilters != HotelFilters.FLAG_HOTEL_FILTER_BED_NONE)
        {
            return false;
        }

        return true;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);

        dest.writeInt(person);
        dest.writeInt(flagFilters);
        dest.writeTypedList(mHotelFiltersList);
    }

    protected void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

        person = in.readInt();
        flagFilters = in.readInt();

        mHotelFiltersList = new ArrayList<>();
        in.readTypedList(mHotelFiltersList, HotelFilters.CREATOR);
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
