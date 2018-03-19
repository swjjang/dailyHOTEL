package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

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

    public GourmetCurationOption()
    {
        mFilterMap = new HashMap<>();
        mCategoryCodeMap = new HashMap<>();
        mCategorySequenceMap = new HashMap<>();

        clear();
    }

    public GourmetCurationOption(Parcel in)
    {
        clear();

        readFromParcel(in);
    }

    @Override
    public void clear()
    {
        super.clear();

        if (mFilterMap != null)
        {
            mFilterMap.clear();
        }

        flagTimeFilter = GourmetFilter.Time.FLAG_NONE;
        flagAmenitiesFilters = GourmetFilter.Amenities.FLAG_NONE;
    }

    @Override
    public boolean isDefaultFilter()
    {
        if (isDefaultSortType() == false//
            || mFilterMap.size() != 0//
            || flagTimeFilter != GourmetFilter.Time.FLAG_NONE//
            || flagAmenitiesFilters != GourmetFilter.Amenities.FLAG_NONE)
        {
            return false;
        }

        return true;
    }

    protected void setCurationOption(GourmetCurationOption gourmetCurationOption)
    {
        if (gourmetCurationOption == null)
        {
            return;
        }

        setSortType(gourmetCurationOption.getSortType());
        setFilterMap(gourmetCurationOption.getFilterMap());
        setCategoryCoderMap(gourmetCurationOption.getCategoryCoderMap());
        setCategorySequenceMap(gourmetCurationOption.getCategorySequenceMap());
        flagTimeFilter = gourmetCurationOption.flagTimeFilter;
        flagAmenitiesFilters = gourmetCurationOption.flagAmenitiesFilters;
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

    public void setCategoryCoderMap(HashMap<String, Integer> categoryIconMap)
    {
        mCategoryCodeMap.clear();
        mCategoryCodeMap.putAll(categoryIconMap);
    }

    public HashMap<String, Integer> getCategoryCoderMap()
    {
        return mCategoryCodeMap;
    }

    public void setCategorySequenceMap(HashMap<String, Integer> categoryIconMap)
    {
        mCategorySequenceMap.clear();
        mCategorySequenceMap.putAll(categoryIconMap);
    }

    public HashMap<String, Integer> getCategorySequenceMap()
    {
        return mCategorySequenceMap;
    }

    public String toSortString()
    {
        String result;

        switch (getSortType())
        {
            case DEFAULT:
                result = AnalyticsManager.Label.SORTFILTER_DISTRICT;
                break;

            case DISTANCE:
                result = AnalyticsManager.Label.SORTFILTER_DISTANCE;
                break;

            case LOW_PRICE:
                result = AnalyticsManager.Label.SORTFILTER_LOWTOHIGHPRICE;
                break;

            case HIGH_PRICE:
                result = AnalyticsManager.Label.SORTFILTER_HIGHTOLOWPRICE;
                break;

            case SATISFACTION:
                result = AnalyticsManager.Label.SORTFILTER_RATING;
                break;

            default:
                result = AnalyticsManager.Label.SORTFILTER_DISTRICT;
                break;
        }

        return result;
    }

    /**
     * @param delimiter GA_DELIMITER or ADJUST_DELIMITER, default GA_DELIMITER;
     * @return
     */
    public String toCategoryString(char delimiter)
    {
        StringBuilder result = new StringBuilder();

        if (mFilterMap.size() == 0)
        {
            result.append(AnalyticsManager.Label.SORTFILTER_NONE);
        } else
        {
            ArrayList<String> categoryArrayList = new ArrayList<>(mFilterMap.keySet());

            int size = categoryArrayList.size();

            for (int i = 0; i < size; i++)
            {
                if (i == 0)
                {
                    result.append(categoryArrayList.get(i));
                } else
                {
                    result.append(delimiter);
                    result.append(categoryArrayList.get(i));
                }
            }
        }

        return result.toString();
    }

    /**
     * @param delimiter GA_DELIMITER or ADJUST_DELIMITER, default GA_DELIMITER;
     * @return
     */
    public String toReservationTimeString(char delimiter)
    {
        StringBuilder result = new StringBuilder();

        if (flagTimeFilter == GourmetFilter.Time.FLAG_NONE)
        {
            result.append(AnalyticsManager.Label.SORTFILTER_NONE);
        } else
        {
            if ((flagTimeFilter & GourmetFilter.Time.FLAG_06_11) == GourmetFilter.Time.FLAG_06_11)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_0611).append(delimiter);
            }

            if ((flagTimeFilter & GourmetFilter.Time.FLAG_11_15) == GourmetFilter.Time.FLAG_11_15)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_1115).append(delimiter);
            }

            if ((flagTimeFilter & GourmetFilter.Time.FLAG_15_17) == GourmetFilter.Time.FLAG_15_17)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_1517).append(delimiter);
            }

            if ((flagTimeFilter & GourmetFilter.Time.FLAG_17_21) == GourmetFilter.Time.FLAG_17_21)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_1721).append(delimiter);
            }

            if ((flagTimeFilter & GourmetFilter.Time.FLAG_21_06) == GourmetFilter.Time.FLAG_21_06)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_2106).append(delimiter);
            }

            if (result.charAt(result.length() - 1) == delimiter)
            {
                result.setLength(result.length() - 1);
            }
        }

        return result.toString();
    }

    /**
     * @param delimiter GA_DELIMITER or ADJUST_DELIMITER, default GA_DELIMITER;
     * @return
     */
    public String toAmenitiesString(char delimiter)
    {
        StringBuilder result = new StringBuilder();

        if (flagAmenitiesFilters == GourmetFilter.Amenities.FLAG_NONE)
        {
            result.append(AnalyticsManager.Label.SORTFILTER_NONE);
        } else
        {
            if ((flagAmenitiesFilters & GourmetFilter.Amenities.FLAG_PARKING) == GourmetFilter.Amenities.FLAG_PARKING)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_PARKINGAVAILABLE).append(delimiter);
            }

            if ((flagAmenitiesFilters & GourmetFilter.Amenities.FLAG_VALET) == GourmetFilter.Amenities.FLAG_VALET)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_VALET).append(delimiter);
            }

            if ((flagAmenitiesFilters & GourmetFilter.Amenities.FLAG_PRIVATEROOM) == GourmetFilter.Amenities.FLAG_PRIVATEROOM)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_PRIVATEROOM).append(delimiter);
            }

            if ((flagAmenitiesFilters & GourmetFilter.Amenities.FLAG_GROUPBOOKING) == GourmetFilter.Amenities.FLAG_GROUPBOOKING)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_GROUP).append(delimiter);
            }

            if ((flagAmenitiesFilters & GourmetFilter.Amenities.FLAG_BABYSEAT) == GourmetFilter.Amenities.FLAG_BABYSEAT)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_BABYSEAT).append(delimiter);
            }

            if ((flagAmenitiesFilters & GourmetFilter.Amenities.FLAG_CORKAGE) == GourmetFilter.Amenities.FLAG_CORKAGE)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_CORKAGE).append(delimiter);
            }

            if (result.charAt(result.length() - 1) == delimiter)
            {
                result.setLength(result.length() - 1);
            }
        }

        return result.toString();
    }

    public String toString()
    {
        StringBuilder result = new StringBuilder();

        result.append(toSortString());
        result.append('-');
        result.append(toCategoryString(GA_DELIMITER));
        result.append('-');
        result.append(toReservationTimeString(GA_DELIMITER));
        result.append('-');
        result.append(toAmenitiesString(GA_DELIMITER));

        return result.toString();
    }

    @Override
    public String toAdjustString()
    {
        StringBuilder result = new StringBuilder();
        result.append("[sort:");
        result.append(toSortString());
        result.append(",category:");
        result.append(toCategoryString(ADJUST_DELIMITER));
        result.append(",reservationTime:");
        result.append(toReservationTimeString(ADJUST_DELIMITER));
        result.append(",facility:");
        result.append(toAmenitiesString(ADJUST_DELIMITER));
        result.append("]");

        return result.toString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);

        dest.writeSerializable(mFilterMap);
        dest.writeSerializable(mCategoryCodeMap);
        dest.writeSerializable(mCategorySequenceMap);
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
