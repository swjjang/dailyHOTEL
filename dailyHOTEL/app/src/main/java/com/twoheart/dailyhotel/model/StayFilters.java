package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class StayFilters implements Parcelable
{
    private StayFilter[] mStayFilterArray;
    public String categoryCode;

    public StayFilters(int size)
    {
        mStayFilterArray = new StayFilter[size];
    }

    public StayFilters(Parcel in)
    {
        readFromParcel(in);
    }

    public void setHotelFilter(int index, JSONObject jsonObject) throws JSONException, ArrayIndexOutOfBoundsException
    {
        if (index < mStayFilterArray.length)
        {
            mStayFilterArray[index] = new StayFilter(jsonObject);
        } else
        {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public boolean isFiltered(StayCurationOption curationOption)
    {
        for (StayFilter stayFilter : mStayFilterArray)
        {
            if (stayFilter.isPersonFiltered(curationOption.person) == true//
                && stayFilter.isBedTypeFiltered(curationOption.flagBedTypeFilters) == true//
                && stayFilter.isAmenitiesFiltered(curationOption.flagAmenitiesFilters) == true)
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
        if (person == StayFilter.MIN_PERSON)
        {
            return true;
        }

        for (StayFilter stayFilter : mStayFilterArray)
        {
            if (stayFilter.isPersonFiltered(person) == true)
            {
                return true;
            }
        }

        return false;
    }

    private boolean isBedTypeFiltered(int flagBedTypeFilters)
    {
        if (flagBedTypeFilters == StayFilter.FLAG_HOTEL_FILTER_BED_NONE)
        {
            return true;
        }

        for (StayFilter stayFilter : mStayFilterArray)
        {
            if (stayFilter.isBedTypeFiltered(flagBedTypeFilters) == true)
            {
                return true;
            }
        }

        return false;
    }

    private boolean isAmenitiesFiltered(int flagAmenitiesFilters)
    {
        if (flagAmenitiesFilters == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_NONE)
        {
            return true;
        }

        for (StayFilter stayFilter : mStayFilterArray)
        {
            if (stayFilter.isAmenitiesFiltered(flagAmenitiesFilters) == true)
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeTypedArray(mStayFilterArray, flags);
        dest.writeString(categoryCode);
    }

    private void readFromParcel(Parcel in)
    {
        mStayFilterArray = (StayFilter[]) in.createTypedArray(StayFilter.CREATOR);
        categoryCode = in.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public StayFilters createFromParcel(Parcel in)
        {
            return new StayFilters(in);
        }

        @Override
        public StayFilters[] newArray(int size)
        {
            return new StayFilters[size];
        }
    };
}
