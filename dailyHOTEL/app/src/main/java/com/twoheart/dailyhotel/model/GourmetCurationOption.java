package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.HashMap;

public class GourmetCurationOption extends PlaceCurationOption
{
    private HashMap<String, Integer> mFilterMap;
    private HashMap<String, Integer> mCategoryCodeMap;
    private HashMap<String, Integer> mCategorySequenceMap;

    public int flagTimeFilter;
    public int flagAmenitiesFilters;

    private ArrayList<GourmetFilters> mGourmetFiltersList;

    public GourmetCurationOption()
    {
        mFilterMap = new HashMap<>();
        mCategoryCodeMap = new HashMap<>();
        mCategorySequenceMap = new HashMap<>();
        mGourmetFiltersList = new ArrayList<>();

        clear();
    }

    public GourmetCurationOption(Parcel in)
    {
        mGourmetFiltersList = new ArrayList<>();

        clear();

        readFromParcel(in);
    }

    public void setFiltersList(ArrayList<GourmetFilters> arrayList)
    {
        mGourmetFiltersList.clear();

        if (arrayList != null)
        {
            mGourmetFiltersList.addAll(arrayList);
        }
    }

    public ArrayList<GourmetFilters> getFiltersList()
    {
        return mGourmetFiltersList;
    }

    @Override
    public void clear()
    {
        super.clear();

        if (mFilterMap != null)
        {
            mFilterMap.clear();
        }

        flagTimeFilter = GourmetFilter.FLAG_GOURMET_FILTER_TIME_NONE;
        flagAmenitiesFilters = GourmetFilters.FLAG_HOTEL_FILTER_AMENITIES_NONE;
    }

    public boolean isDefaultFilter()
    {
        if (getSortType() != Constants.SortType.DEFAULT//
            || mFilterMap.size() != 0//
            || flagTimeFilter != GourmetFilter.FLAG_GOURMET_FILTER_TIME_NONE//
            || flagAmenitiesFilters != GourmetFilters.FLAG_HOTEL_FILTER_AMENITIES_NONE)
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

    public String toString()
    {
        StringBuilder result = new StringBuilder();

        switch (getSortType())
        {
            case DEFAULT:
                result.append(AnalyticsManager.Label.SORTFILTER_DISTRICT);
                break;

            case DISTANCE:
                result.append(AnalyticsManager.Label.SORTFILTER_DISTANCE);
                break;

            case LOW_PRICE:
                result.append(AnalyticsManager.Label.SORTFILTER_LOWTOHIGHPRICE);
                break;

            case HIGH_PRICE:
                result.append(AnalyticsManager.Label.SORTFILTER_HIGHTOLOWPRICE);
                break;

            case SATISFACTION:
                result.append(AnalyticsManager.Label.SORTFILTER_RATING);
                break;
        }

        result.append('-');

        if (mCategoryCodeMap.size() == 0)
        {
            result.append(AnalyticsManager.Label.SORTFILTER_NONE);
        } else
        {
            ArrayList<String> categoryArrayList = new ArrayList(mCategoryCodeMap.keySet());

            int size = categoryArrayList.size();

            for (int i = 0; i < size; i++)
            {
                if (i == 0)
                {
                    result.append(categoryArrayList.get(i));
                } else
                {
                    result.append(',');
                    result.append(categoryArrayList.get(i));
                }
            }
        }

        result.append('-');

        if (flagTimeFilter == GourmetFilter.FLAG_GOURMET_FILTER_TIME_NONE)
        {
            result.append(AnalyticsManager.Label.SORTFILTER_NONE);
        } else
        {
            boolean isFirst = true;

            if ((flagTimeFilter & GourmetFilter.FLAG_GOURMET_FILTER_TIME_06_11) == GourmetFilter.FLAG_GOURMET_FILTER_TIME_06_11)
            {
                isFirst = false;
                result.append(AnalyticsManager.Label.SORTFILTER_0611);
            }

            if ((flagTimeFilter & GourmetFilter.FLAG_GOURMET_FILTER_TIME_11_15) == GourmetFilter.FLAG_GOURMET_FILTER_TIME_11_15)
            {
                if (isFirst == true)
                {
                    isFirst = false;
                } else
                {
                    result.append(',');
                }

                result.append(AnalyticsManager.Label.SORTFILTER_1115);
            }

            if ((flagTimeFilter & GourmetFilter.FLAG_GOURMET_FILTER_TIME_15_17) == GourmetFilter.FLAG_GOURMET_FILTER_TIME_15_17)
            {
                if (isFirst == true)
                {
                    isFirst = false;
                } else
                {
                    result.append(',');
                }

                result.append(AnalyticsManager.Label.SORTFILTER_1517);
            }

            if ((flagTimeFilter & GourmetFilter.FLAG_GOURMET_FILTER_TIME_17_21) == GourmetFilter.FLAG_GOURMET_FILTER_TIME_17_21)
            {
                if (isFirst == true)
                {
                    isFirst = false;
                } else
                {
                    result.append(',');
                }

                result.append(AnalyticsManager.Label.SORTFILTER_1721);
            }

            if ((flagTimeFilter & GourmetFilter.FLAG_GOURMET_FILTER_TIME_21_06) == GourmetFilter.FLAG_GOURMET_FILTER_TIME_21_06)
            {
                if (isFirst == true)
                {
                    isFirst = false;
                } else
                {
                    result.append(',');
                }

                result.append(AnalyticsManager.Label.SORTFILTER_2106);
            }
        }

        result.append('-');

        if (flagAmenitiesFilters == GourmetFilters.FLAG_HOTEL_FILTER_AMENITIES_NONE)
        {
            result.append(AnalyticsManager.Label.SORTFILTER_NONE);
        } else
        {
            boolean isFirst = true;

            if ((flagAmenitiesFilters & GourmetFilters.FLAG_HOTEL_FILTER_AMENITIES_PARKING) == GourmetFilters.FLAG_HOTEL_FILTER_AMENITIES_PARKING)
            {
                isFirst = false;
                result.append(AnalyticsManager.Label.SORTFILTER_PARKINGAVAILABEL);
            }
        }

        return result.toString();
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
        dest.writeInt(flagAmenitiesFilters);
    }

    @Override
    protected void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

        mFilterMap = (HashMap<String, Integer>) in.readSerializable();
        mCategoryCodeMap = (HashMap<String, Integer>) in.readSerializable();
        mCategorySequenceMap = (HashMap<String, Integer>) in.readSerializable();
        in.readTypedList(mGourmetFiltersList, GourmetFilters.CREATOR);
        flagTimeFilter = in.readInt();
        flagAmenitiesFilters = in.readInt();
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
