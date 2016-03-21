package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class GourmetFilters implements Parcelable
{
    public static final int FLAG_HOTEL_FILTER_AMENITIES_NONE = 0x00;
    public static final int FLAG_HOTEL_FILTER_AMENITIES_PARKING = 0x01;

    private GourmetFilter[] mGourmetFilterArray;
    public String category;
    public int amenitiesFlag;

    public GourmetFilters(int size)
    {
        mGourmetFilterArray = new GourmetFilter[size];
    }

    public GourmetFilters(Parcel in)
    {
        readFromParcel(in);
    }

    public void setGourmetFilter(int index, JSONObject jsonObject) throws JSONException, ArrayIndexOutOfBoundsException
    {
        if (index < mGourmetFilterArray.length)
        {
            mGourmetFilterArray[index] = new GourmetFilter(jsonObject);
        } else
        {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public boolean isFiltered(GourmetCurationOption curationOption)
    {
        return (isCategoryFiltered(curationOption.getFilterMap()) == true//
            && isTimeFiltered(curationOption.flagTimeFilter) == true//
            && isAmenitiesFiltered(curationOption.flagAmenitiesFilters) == true);
    }

    private boolean isCategoryFiltered(HashMap<String, Integer> categoryMap)
    {
        if (categoryMap == null || categoryMap.size() == 0)
        {
            return true;
        }

        return categoryMap.containsKey(category);
    }

    private boolean isTimeFiltered(int flagTimeFilters)
    {
        if (flagTimeFilters == GourmetFilter.FLAG_GOURMET_FILTER_TIME_NONE)
        {
            return true;
        }

        for (GourmetFilter gourmetFilter : mGourmetFilterArray)
        {
            if (gourmetFilter.isTimeFiltered(flagTimeFilters) == true)
            {
                return true;
            }
        }

        return false;
    }

    private boolean isAmenitiesFiltered(int flagAmenitiesFilters)
    {
        if (flagAmenitiesFilters == FLAG_HOTEL_FILTER_AMENITIES_NONE)
        {
            return true;
        }

        return (amenitiesFlag & flagAmenitiesFilters) == flagAmenitiesFilters;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeTypedArray(mGourmetFilterArray, flags);
        dest.writeString(category);
        dest.writeInt(amenitiesFlag);
    }

    private void readFromParcel(Parcel in)
    {
        mGourmetFilterArray = (GourmetFilter[]) in.createTypedArray(GourmetFilter.CREATOR);
        category = in.readString();
        amenitiesFlag = in.readInt();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public GourmetFilters createFromParcel(Parcel in)
        {
            return new GourmetFilters(in);
        }

        @Override
        public GourmetFilters[] newArray(int size)
        {
            return new GourmetFilters[size];
        }
    };
}
