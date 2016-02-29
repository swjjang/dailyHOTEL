package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;

public class GourmetCurationOption extends PlaceCurationOption
{
    private HashMap<String, Integer> mFilterMap;
    private HashMap<String, Integer> mCategoryCodeMap;
    private HashMap<String, Integer> mCategorySequenceMap;

    public int flagTimeFilter;
    public boolean isParking;

    private ArrayList<GourmetFilters> mGourmetFiltersList;

    public GourmetCurationOption()
    {
        super();

        mFilterMap = new HashMap<>();
        mCategoryCodeMap = new HashMap<>();
        mCategorySequenceMap = new HashMap<>();

        mGourmetFiltersList = new ArrayList<>();

        clear();
    }

    public GourmetCurationOption(Parcel in)
    {
        readFromParcel(in);
    }

    public void setFiltersList(ArrayList<GourmetFilters> arrayList)
    {
        mGourmetFiltersList.clear();
        mGourmetFiltersList.addAll(arrayList);
    }

    public ArrayList<GourmetFilters> getFiltersList()
    {
        return mGourmetFiltersList;
    }

    @Override
    public void clear()
    {
        super.clear();

        mFilterMap.clear();
    }

    public boolean isDefaultFilter()
    {
        if (getSortType() != Constants.SortType.DEFAULT//
            || mFilterMap.size() != 0//
            || flagTimeFilter != GourmetFilter.FLAG_GOURMET_FILTER_TIME_NONE//
            || isParking == true)
        {
            return false;
        }

        return true;
    }

    public void setFilterMap(HashMap<String, Integer> filterMap)
    {
        mFilterMap.clear();
        mFilterMap.putAll(filterMap);
    }

    public HashMap<String, Integer> getFilterMap()
    {
        return mFilterMap;
    }

    public void setCategoryCoderMap(HashMap<String, Integer> categoryIconrMap)
    {
        mCategoryCodeMap.clear();
        mCategoryCodeMap.putAll(categoryIconrMap);
    }

    public HashMap<String, Integer> getCategoryCoderMap()
    {
        return mCategoryCodeMap;
    }

    public void setCategorySequenceMap(HashMap<String, Integer> categoryIconrMap)
    {
        mCategorySequenceMap.clear();
        mCategorySequenceMap.putAll(categoryIconrMap);
    }

    public HashMap<String, Integer> getCategorySequenceMap()
    {
        return mCategorySequenceMap;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);

        dest.writeSerializable(mFilterMap);
        dest.writeSerializable(mCategoryCodeMap);
        dest.writeSerializable(mCategorySequenceMap);
        dest.writeTypedList(mGourmetFiltersList);
        dest.writeInt(flagTimeFilter);
        dest.writeInt(isParking ? 1 : 0);
    }

    @Override
    protected void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

        mFilterMap = (HashMap<String, Integer>) in.readSerializable();
        mCategoryCodeMap = (HashMap<String, Integer>) in.readSerializable();
        mCategorySequenceMap = (HashMap<String, Integer>) in.readSerializable();

        mGourmetFiltersList = new ArrayList<>();
        in.readTypedList(mGourmetFiltersList, GourmetFilters.CREATOR);

        flagTimeFilter = in.readInt();
        isParking = in.readInt() == 1 ? true : false;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public GourmetCurationOption createFromParcel(Parcel in)
        {
            return new GourmetCurationOption(in);
        }

        @Override
        public GourmetCurationOption[] newArray(int size)
        {
            return new GourmetCurationOption[size];
        }
    };
}
