package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.Constants;

import java.util.ArrayList;

public class HotelCurationOption extends PlaceCurationOption
{
    public int person;
    public int flagBedTypeFilters;
    public int flagAmenitiesFilters;
    private Category mCategory;

    private ArrayList<HotelFilters> mHotelFiltersList;

    public HotelCurationOption()
    {
        mCategory = Category.ALL;
        mHotelFiltersList = new ArrayList<>();

        clear();
    }

    public HotelCurationOption(Parcel in)
    {
        mCategory = Category.ALL;
        mHotelFiltersList = new ArrayList<>();

        clear();

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

    public void setFiltersList(ArrayList<HotelFilters> arrayList)
    {
        mHotelFiltersList.clear();

        if (arrayList != null)
        {
            mHotelFiltersList.addAll(arrayList);
        }
    }

    public ArrayList<HotelFilters> getFiltersList()
    {
        return mHotelFiltersList;
    }

    public void clear()
    {
        super.clear();

        person = HotelFilter.MIN_PERSON;
        flagBedTypeFilters = HotelFilter.FLAG_HOTEL_FILTER_BED_NONE;
        flagAmenitiesFilters = HotelFilter.FLAG_HOTEL_FILTER_AMENITIES_NONE;
    }

    public boolean isDefaultFilter()
    {
        if (getSortType() != Constants.SortType.DEFAULT//
            || person != HotelFilter.MIN_PERSON//
            || flagBedTypeFilters != HotelFilter.FLAG_HOTEL_FILTER_BED_NONE || flagAmenitiesFilters != HotelFilter.FLAG_HOTEL_FILTER_AMENITIES_NONE)
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
        dest.writeInt(flagBedTypeFilters);
        dest.writeTypedList(mHotelFiltersList);
        dest.writeParcelable(mCategory, flags);
        dest.writeInt(flagAmenitiesFilters);
    }

    protected void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

        person = in.readInt();
        flagBedTypeFilters = in.readInt();
        in.readTypedList(mHotelFiltersList, HotelFilters.CREATOR);
        mCategory = in.readParcelable(Category.class.getClassLoader());
        flagAmenitiesFilters = in.readInt();
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
