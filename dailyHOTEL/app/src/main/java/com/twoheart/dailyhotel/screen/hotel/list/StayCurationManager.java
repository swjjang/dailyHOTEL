package com.twoheart.dailyhotel.screen.hotel.list;

import android.location.Location;

import com.twoheart.dailyhotel.model.Category;
import com.twoheart.dailyhotel.model.HotelFilter;
import com.twoheart.dailyhotel.model.HotelFilters;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;

public class StayCurationManager
{
    private static StayCurationManager mInstance;


    private Constants.SortType sortType = Constants.SortType.DEFAULT;

    private Province mProvince;
    private Location mLocation; // Not Parcelable

    public int person;
    public int flagBedTypeFilters;
    public int flagAmenitiesFilters;
    private Category mCategory;

    private ArrayList<HotelFilters> mHotelFiltersList;

    private SaleTime mCheckInSaleTime;
    private SaleTime mCheckOutSaleTime;

    public void setCheckInSaleTime(long currentDateTime, long dailyDateTime)
    {
        mCheckInSaleTime.setCurrentTime(currentDateTime);
        mCheckInSaleTime.setDailyTime(dailyDateTime);
    }

    public void setCheckInSaleTime(SaleTime saleTime)
    {
        mCheckInSaleTime = saleTime;
    }

    public void setCheckOutSaleTime(SaleTime saleTime)
    {
        mCheckOutSaleTime = saleTime;
    }

    public SaleTime getCheckInSaleTime()
    {
        return mCheckInSaleTime;
    }

    public SaleTime getCheckOutSaleTime()
    {
        return mCheckOutSaleTime;
    }

    public static synchronized StayCurationManager getInstance()
    {
        if (mInstance == null)
        {
            mInstance = new StayCurationManager();
        }

        return mInstance;
    }

    private StayCurationManager()
    {
        mCategory = Category.ALL;
        mHotelFiltersList = new ArrayList<>();

        mCheckInSaleTime = new SaleTime();
        mCheckOutSaleTime = new SaleTime();


        clear();
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

    public Category getCategory()
    {
        return mCategory;
    }

    public void setCategory(Category category)
    {
        mCategory = category;
    }

    public void setFiltersList(ArrayList<HotelFilters> arrayList)
    {
        mHotelFiltersList.clear();

        if (arrayList != null)
        {
            mHotelFiltersList.addAll(arrayList);
        }
    }

    public ArrayList<HotelFilters> getFiltersList()
    {
        return mHotelFiltersList;
    }

    public void clear()
    {
        sortType = Constants.SortType.DEFAULT;

        person = HotelFilter.MIN_PERSON;
        flagBedTypeFilters = HotelFilter.FLAG_HOTEL_FILTER_BED_NONE;
        flagAmenitiesFilters = HotelFilter.FLAG_HOTEL_FILTER_AMENITIES_NONE;
    }

    public boolean isDefaultFilter()
    {
        if (getSortType() != Constants.SortType.DEFAULT//
            || person != HotelFilter.MIN_PERSON//
            || flagBedTypeFilters != HotelFilter.FLAG_HOTEL_FILTER_BED_NONE//
            || flagAmenitiesFilters != HotelFilter.FLAG_HOTEL_FILTER_AMENITIES_NONE)
        {
            return false;
        }

        return true;
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
        result.append(person);

        result.append('-');

        if (flagBedTypeFilters == HotelFilter.FLAG_HOTEL_FILTER_BED_NONE)
        {
            result.append(AnalyticsManager.Label.SORTFILTER_NONE);
        } else
        {
            boolean isFirst = true;

            if ((flagBedTypeFilters & HotelFilter.FLAG_HOTEL_FILTER_BED_DOUBLE) == HotelFilter.FLAG_HOTEL_FILTER_BED_DOUBLE)
            {
                isFirst = false;
                result.append(AnalyticsManager.Label.SORTFILTER_DOUBLE);
            }

            if ((flagBedTypeFilters & HotelFilter.FLAG_HOTEL_FILTER_BED_TWIN) == HotelFilter.FLAG_HOTEL_FILTER_BED_TWIN)
            {
                if (isFirst == true)
                {
                    isFirst = false;
                } else
                {
                    result.append(',');
                }

                result.append(AnalyticsManager.Label.SORTFILTER_TWIN);
            }

            if ((flagBedTypeFilters & HotelFilter.FLAG_HOTEL_FILTER_BED_HEATEDFLOORS) == HotelFilter.FLAG_HOTEL_FILTER_BED_HEATEDFLOORS)
            {
                if (isFirst == true)
                {
                    isFirst = false;
                } else
                {
                    result.append(',');
                }

                result.append(AnalyticsManager.Label.SORTFILTER_ONDOL);
            }
        }

        result.append('-');

        if (flagAmenitiesFilters == HotelFilter.FLAG_HOTEL_FILTER_AMENITIES_NONE)
        {
            result.append(AnalyticsManager.Label.SORTFILTER_NONE);
        } else
        {
            boolean isFirst = true;

            if ((flagAmenitiesFilters & HotelFilter.FLAG_HOTEL_FILTER_AMENITIES_WIFI) == HotelFilter.FLAG_HOTEL_FILTER_AMENITIES_WIFI)
            {
                isFirst = false;
                result.append(AnalyticsManager.Label.SORTFILTER_WIFI);
            }

            if ((flagAmenitiesFilters & HotelFilter.FLAG_HOTEL_FILTER_AMENITIES_BREAKFAST) == HotelFilter.FLAG_HOTEL_FILTER_AMENITIES_BREAKFAST)
            {
                if (isFirst == true)
                {
                    isFirst = false;
                } else
                {
                    result.append(',');
                }

                result.append(AnalyticsManager.Label.SORTFILTER_FREEBREAKFAST);
            }

            if ((flagAmenitiesFilters & HotelFilter.FLAG_HOTEL_FILTER_AMENITIES_COOKING) == HotelFilter.FLAG_HOTEL_FILTER_AMENITIES_COOKING)
            {
                if (isFirst == true)
                {
                    isFirst = false;
                } else
                {
                    result.append(',');
                }

                result.append(AnalyticsManager.Label.SORTFILTER_KITCHEN);
            }

            if ((flagAmenitiesFilters & HotelFilter.FLAG_HOTEL_FILTER_AMENITIES_BATH) == HotelFilter.FLAG_HOTEL_FILTER_AMENITIES_BATH)
            {
                if (isFirst == true)
                {
                    isFirst = false;
                } else
                {
                    result.append(',');
                }

                result.append(AnalyticsManager.Label.SORTFILTER_BATHTUB);
            }

            if ((flagAmenitiesFilters & HotelFilter.FLAG_HOTEL_FILTER_AMENITIES_PARKING) == HotelFilter.FLAG_HOTEL_FILTER_AMENITIES_PARKING)
            {
                if (isFirst == true)
                {
                    isFirst = false;
                } else
                {
                    result.append(',');
                }

                result.append(AnalyticsManager.Label.SORTFILTER_PARKINGAVAILABEL);
            }

            if ((flagAmenitiesFilters & HotelFilter.FLAG_HOTEL_FILTER_AMENITIES_POOL) == HotelFilter.FLAG_HOTEL_FILTER_AMENITIES_POOL)
            {
                if (isFirst == true)
                {
                    isFirst = false;
                } else
                {
                    result.append(',');
                }

                result.append(AnalyticsManager.Label.SORTFILTER_POOL);
            }

            if ((flagAmenitiesFilters & HotelFilter.FLAG_HOTEL_FILTER_AMENITIES_FITNESS) == HotelFilter.FLAG_HOTEL_FILTER_AMENITIES_FITNESS)
            {
                if (isFirst == true)
                {
                    isFirst = false;
                } else
                {
                    result.append(',');
                }

                result.append(AnalyticsManager.Label.SORTFILTER_FITNESS);
            }
        }

        return result.toString();
    }
}
