package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class HotelFilters implements Parcelable
{
    public static int FLAG_HOTEL_FILTER_BED_NONE = 0x00;

    private HotelFilter[] mHotelFilterArray;
    public String categoryCode;

    public HotelFilters(int size)
    {
        mHotelFilterArray = new HotelFilter[size];
    }

    public HotelFilters(Parcel in)
    {
        readFromParcel(in);
    }

    public void setHotelFilter(int index, JSONObject jsonObject) throws JSONException, ArrayIndexOutOfBoundsException
    {
        if (index < mHotelFilterArray.length)
        {
            mHotelFilterArray[index] = new HotelFilter(jsonObject);
        } else
        {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public boolean isFiltered(HotelCurationOption curationOption)
    {
        return isPersonFiltered(curationOption.person) & isBedTypeFiltered(curationOption.flagBedTypeFilters) & isAmenitiesFiltered(curationOption.flagAmenitiesFilters);
    }

    private boolean isPersonFiltered(int person)
    {
        if (person == HotelFilter.MIN_PERSON)
        {
            return true;
        }

        for (HotelFilter hotelFilter : mHotelFilterArray)
        {
            if (hotelFilter.isPersonFiltered(person) == true)
            {
                return true;
            }
        }

        return false;
    }

    private boolean isBedTypeFiltered(int flagBedTypeFilters)
    {
        if (flagBedTypeFilters == HotelFilters.FLAG_HOTEL_FILTER_BED_NONE)
        {
            return true;
        }

        for (HotelFilter hotelFilter : mHotelFilterArray)
        {
            if (hotelFilter.isBedTypeFiltered(flagBedTypeFilters) == true)
            {
                return true;
            }
        }

        return false;
    }

    private boolean isAmenitiesFiltered(int flagAmenitiesFilters)
    {
        if (flagAmenitiesFilters == HotelFilter.FLAG_HOTEL_FILTER_AMENITIES_NONE)
        {
            return true;
        }

        for (HotelFilter hotelFilter : mHotelFilterArray)
        {
            if (hotelFilter.isAmenitiesFiltered(flagAmenitiesFilters) == true)
            {
                return true;
            }
        }

        return false;
    }

    public void clear()
    {
        Arrays.fill(mHotelFilterArray, 0, mHotelFilterArray.length, null);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeTypedArray(mHotelFilterArray, flags);
        dest.writeString(categoryCode);
    }

    private void readFromParcel(Parcel in)
    {
        mHotelFilterArray = (HotelFilter[]) in.createTypedArray(HotelFilter.CREATOR);
        categoryCode = in.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public HotelFilters createFromParcel(Parcel in)
        {
            return new HotelFilters(in);
        }

        @Override
        public HotelFilters[] newArray(int size)
        {
            return new HotelFilters[size];
        }
    };
}
