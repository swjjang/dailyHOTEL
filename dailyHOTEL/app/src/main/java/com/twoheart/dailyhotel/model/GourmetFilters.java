package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

public class GourmetFilters implements Parcelable
{

    private GourmetFilter[] mGourmetFilterArray;
    public String category;

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
        if (isCategoryFiltered(curationOption.getFilterMap()) == false//
            || isTimeFiltered(curationOption.flagTimeFilter) == false//
            || isParkingFiltered(curationOption.isParking) == false)
        {
            return false;
        } else
        {
            return true;
        }
    }

    private boolean isCategoryFiltered(HashMap<String, Integer> categoryMap)
    {
        if (categoryMap == null || categoryMap.size() == 0)
        {
            return true;
        }

        return categoryMap.containsKey(category);
    }

    private boolean isTimeFiltered(int flagTimeFilter)
    {
        if (flagTimeFilter == GourmetFilter.FLAG_GOURMET_FILTER_TIME_NONE)
        {
            return true;
        }

        for (GourmetFilter gourmetFilter : mGourmetFilterArray)
        {
            if (gourmetFilter.isTimeFiltered(flagTimeFilter) == true)
            {
                return true;
            }
        }

        return false;
    }

    private boolean isParkingFiltered(boolean isParking)
    {
        if (isParking == false)
        {
            return true;
        }

        for (GourmetFilter gourmetFilter : mGourmetFilterArray)
        {
            if (gourmetFilter.isParkingFiltered(isParking) == true)
            {
                return true;
            }
        }

        return false;
    }

    public void clear()
    {
        Arrays.fill(mGourmetFilterArray, 0, mGourmetFilterArray.length, null);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeTypedArray(mGourmetFilterArray, flags);
        dest.writeString(category);
    }

    private void readFromParcel(Parcel in)
    {
        mGourmetFilterArray = (GourmetFilter[]) in.createTypedArray(GourmetFilter.CREATOR);
        category = in.readString();
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
