package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class HotelFilters implements Parcelable
{
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

    public boolean isFiltered(StayCurationOption curationOption)
    {
        for (HotelFilter hotelFilter : mHotelFilterArray)
        {
            if (hotelFilter.isPersonFiltered(curationOption.person) == true//
                && hotelFilter.isBedTypeFiltered(curationOption.flagBedTypeFilters) == true//
                && hotelFilter.isAmenitiesFiltered(curationOption.flagAmenitiesFilters) == true)
            {
                return true;
            }
        }

        return false;

        //        return (isPersonFiltered(curationOption.person) == true//
        //            && isBedTypeFiltered(curationOption.flagBedTypeFilters) == true//
        //            && isAmenitiesFiltered(curationOption.flagAmenitiesFilters) == true);
    }

    public boolean isFiltered(HotelCurationOption curationOption)
    {
        for (HotelFilter hotelFilter : mHotelFilterArray)
        {
            if (hotelFilter.isPersonFiltered(curationOption.person) == true//
                && hotelFilter.isBedTypeFiltered(curationOption.flagBedTypeFilters) == true//
                && hotelFilter.isAmenitiesFiltered(curationOption.flagAmenitiesFilters) == true)
            {
                return true;
            }
        }

        return false;

        //        return (isPersonFiltered(curationOption.person) == true//
        //            && isBedTypeFiltered(curationOption.flagBedTypeFilters) == true//
        //            && isAmenitiesFiltered(curationOption.flagAmenitiesFilters) == true);
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
        if (flagBedTypeFilters == HotelFilter.FLAG_HOTEL_FILTER_BED_NONE)
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
