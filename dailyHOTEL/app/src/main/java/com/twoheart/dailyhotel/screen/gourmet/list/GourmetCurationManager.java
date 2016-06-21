package com.twoheart.dailyhotel.screen.gourmet.list;

import android.location.Location;

import com.twoheart.dailyhotel.model.GourmetFilter;
import com.twoheart.dailyhotel.model.GourmetFilters;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.HashMap;

public class GourmetCurationManager
{
    private static GourmetCurationManager mInstance;

    private Constants.SortType sortType = Constants.SortType.DEFAULT;

    private Province mProvince;
    private Location mLocation;

    private HashMap<String, Integer> mFilterMap;
    private HashMap<String, Integer> mCategoryCodeMap;
    private HashMap<String, Integer> mCategorySequenceMap;

    public int flagTimeFilter;
    public int flagAmenitiesFilters;

    private ArrayList<GourmetFilters> mGourmetFiltersList;

    private SaleTime mSaleTime;

    public static synchronized GourmetCurationManager getInstance()
    {
        if (mInstance == null)
        {
            mInstance = new GourmetCurationManager();
        }

        return mInstance;
    }

    private GourmetCurationManager()
    {
        mSaleTime = new SaleTime();
        mFilterMap = new HashMap<>();
        mCategoryCodeMap = new HashMap<>();
        mCategorySequenceMap = new HashMap<>();
        mGourmetFiltersList = new ArrayList<>();

        clear();
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

    public void clear()
    {
        sortType = Constants.SortType.DEFAULT;

        if (mFilterMap != null)
        {
            mFilterMap.clear();
        }

        flagTimeFilter = GourmetFilter.FLAG_GOURMET_FILTER_TIME_NONE;
        flagAmenitiesFilters = GourmetFilters.FLAG_HOTEL_FILTER_AMENITIES_NONE;
    }

    public void setSaleTime(long currentDateTime, long dailyDateTime)
    {
        mSaleTime.setCurrentTime(currentDateTime);
        mSaleTime.setDailyTime(dailyDateTime);
    }

    public void setSaleTime(SaleTime saleTime)
    {
        mSaleTime = saleTime;
    }

    public SaleTime getSaleTime()
    {
        return mSaleTime;
    }

    public void setSortType(Constants.SortType sortType)
    {
        if (sortType == null)
        {
            sortType = Constants.SortType.DEFAULT;
        }

        this.sortType = sortType;
    }

    public Constants.SortType getSortType()
    {
        return sortType;
    }

    public Province getProvince()
    {
        return mProvince;
    }

    public void setProvince(Province province)
    {
        mProvince = province;
    }

    public Location getLocation()
    {
        return mLocation;
    }

    public void setLocation(Location location)
    {
        mLocation = location;
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
}
