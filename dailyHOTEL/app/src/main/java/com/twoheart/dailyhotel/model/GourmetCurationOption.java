package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class GourmetCurationOption extends PlaceCurationOption
{
    private HashMap<String, Integer> mFilterMap;
    private HashMap<String, Integer> mCategoryIconMap;
    private HashMap<String, Integer> mCategoryMap;

    public GourmetCurationOption()
    {
        super();

        mCategoryMap = new HashMap<>();
        mFilterMap = new HashMap<>();
        mCategoryIconMap = new HashMap<>();

        clear();
    }

    public GourmetCurationOption(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public void clear()
    {
        super.clear();

        mFilterMap.clear();
    }

    public void setCategoryMap(HashMap<String, Integer> categoryCountMap)
    {
        mCategoryMap.clear();
        mCategoryMap.putAll(categoryCountMap);
    }

    public HashMap<String, Integer> getCategoryMap()
    {
        return mCategoryMap;
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

    public void setCategoryIconrMap(HashMap<String, Integer> categoryIconrMap)
    {
        mCategoryIconMap.clear();
        mCategoryIconMap.putAll(categoryIconrMap);
    }

    public HashMap<String, Integer> getCategoryIconrMap()
    {
        return mCategoryIconMap;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);

        dest.writeSerializable(mFilterMap);
        dest.writeSerializable(mCategoryMap);
        dest.writeSerializable(mCategoryIconMap);
    }

    @Override
    protected void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

        mFilterMap = (HashMap<String, Integer>) in.readSerializable();
        mCategoryMap = (HashMap<String, Integer>) in.readSerializable();
        mCategoryIconMap = (HashMap<String, Integer>) in.readSerializable();
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
