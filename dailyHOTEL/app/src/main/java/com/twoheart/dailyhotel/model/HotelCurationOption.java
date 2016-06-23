package com.twoheart.dailyhotel.model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;

public class HotelCurationOption extends PlaceCurationOption
{
    public int person;
    public int flagBedTypeFilters;
    public int flagAmenitiesFilters;
    private Category mCategory;

    private ArrayList<HotelFilters> mHotelFiltersList;

    private Province mProvince; // Not Parcelable
    private Location mLocation; // Not Parcelable

    public HotelCurationOption()
    {
        mCategory = Category.ALL;
        mHotelFiltersList = new ArrayList<>();

        clear();
    }

    public HotelCurationOption(Parcel in)
    {
        mCategory = Category.ALL;
        mHotelFiltersList = new ArrayList<>();

        clear();

        readFromParcel(in);
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
        super.clear();

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

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);

        dest.writeInt(person);
        dest.writeInt(flagBedTypeFilters);
        dest.writeTypedList(mHotelFiltersList);
        dest.writeParcelable(mCategory, flags);
        dest.writeInt(flagAmenitiesFilters);
    }

    protected void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

        person = in.readInt();
        flagBedTypeFilters = in.readInt();
        in.readTypedList(mHotelFiltersList, HotelFilters.CREATOR);
        mCategory = in.readParcelable(Category.class.getClassLoader());
        flagAmenitiesFilters = in.readInt();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public HotelCurationOption createFromParcel(Parcel in)
        {
            return new HotelCurationOption(in);
        }

        @Override
        public HotelCurationOption[] newArray(int size)
        {
            return new HotelCurationOption[size];
        }
    };
}
