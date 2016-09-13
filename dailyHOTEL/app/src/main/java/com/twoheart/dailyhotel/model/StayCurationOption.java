package com.twoheart.dailyhotel.model;

import android.os.Parcel;

import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class StayCurationOption extends PlaceCurationOption
{
    public int person;
    public int flagBedTypeFilters;
    public int flagAmenitiesFilters; // luxuries

    public StayCurationOption()
    {
        clear();
    }

    public StayCurationOption(Parcel in)
    {
        clear();

        readFromParcel(in);
    }

    public void clear()
    {
        super.clear();

        person = StayFilter.MIN_PERSON;
        flagBedTypeFilters = StayFilter.FLAG_HOTEL_FILTER_BED_NONE;
        flagAmenitiesFilters = StayFilter.FLAG_HOTEL_FILTER_AMENITIES_NONE;
    }

    protected void setCurationOption(StayCurationOption stayCurationOption)
    {
        if (stayCurationOption == null)
        {
            return;
        }

        setSortType(stayCurationOption.getSortType());

        person = stayCurationOption.person;
        flagBedTypeFilters = stayCurationOption.flagBedTypeFilters;
        flagAmenitiesFilters = stayCurationOption.flagAmenitiesFilters;
    }

    @Override
    public boolean isDefaultFilter()
    {
        if (isDefaultSortType() == false//
            || person != StayFilter.MIN_PERSON//
            || flagBedTypeFilters != StayFilter.FLAG_HOTEL_FILTER_BED_NONE//
            || flagAmenitiesFilters != StayFilter.FLAG_HOTEL_FILTER_AMENITIES_NONE)
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

        if (flagBedTypeFilters == StayFilter.FLAG_HOTEL_FILTER_BED_NONE)
        {
            result.append(AnalyticsManager.Label.SORTFILTER_NONE);
        } else
        {
            if ((flagBedTypeFilters & StayFilter.FLAG_HOTEL_FILTER_BED_DOUBLE) == StayFilter.FLAG_HOTEL_FILTER_BED_DOUBLE)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_DOUBLE).append(',');
            }

            if ((flagBedTypeFilters & StayFilter.FLAG_HOTEL_FILTER_BED_TWIN) == StayFilter.FLAG_HOTEL_FILTER_BED_TWIN)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_TWIN).append(',');
            }

            if ((flagBedTypeFilters & StayFilter.FLAG_HOTEL_FILTER_BED_HEATEDFLOORS) == StayFilter.FLAG_HOTEL_FILTER_BED_HEATEDFLOORS)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_ONDOL).append(',');
            }

            if (result.charAt(result.length() - 1) == ',')
            {
                result.setLength(result.length() - 1);
            }
        }

        result.append('-');

        if (flagAmenitiesFilters == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_NONE)
        {
            result.append(AnalyticsManager.Label.SORTFILTER_NONE);
        } else
        {
            if ((flagAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_AMENITIES_WIFI) == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_WIFI)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_WIFI).append(',');
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_AMENITIES_BREAKFAST) == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_BREAKFAST)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_FREEBREAKFAST).append(',');
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_AMENITIES_COOKING) == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_COOKING)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_KITCHEN).append(',');
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_AMENITIES_BATH) == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_BATH)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_BATHTUB).append(',');
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_AMENITIES_PARKING) == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_PARKING)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_PARKINGAVAILABLE).append(',');
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_AMENITIES_POOL) == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_POOL)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_POOL).append(',');
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_AMENITIES_FITNESS) == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_FITNESS)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_FITNESS).append(',');
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_AMENITIES_NOPARKING) == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_NOPARKING)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_PARKINGDISABLE).append(',');
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_AMENITIES_PET) == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_PET)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_PET).append(',');
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_AMENITIES_SHAREDBBQ) == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_SHAREDBBQ)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_SHAREDBBQ).append(',');
            }

            if (result.charAt(result.length() - 1) == ',')
            {
                result.setLength(result.length() - 1);
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
        dest.writeInt(flagAmenitiesFilters);
    }

    protected void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

        person = in.readInt();
        flagBedTypeFilters = in.readInt();
        flagAmenitiesFilters = in.readInt();
    }

    public static final Creator CREATOR = new Creator()
    {
        public StayCurationOption createFromParcel(Parcel in)
        {
            return new StayCurationOption(in);
        }

        @Override
        public StayCurationOption[] newArray(int size)
        {
            return new StayCurationOption[size];
        }
    };
}
