package com.twoheart.dailyhotel.model;

import android.os.Parcel;

import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class StayCurationOption extends PlaceCurationOption
{
    public int person;
    public int flagBedTypeFilters;
    public int flagAmenitiesFilters; // luxuries
    public int flagRoomAmenitiesFilters; // room luxuries

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

        person = StayFilter.DEFAULT_PERSON;
        flagBedTypeFilters = StayFilter.FLAG_HOTEL_FILTER_BED_NONE;
        flagAmenitiesFilters = StayFilter.FLAG_HOTEL_FILTER_AMENITIES_NONE;
        flagRoomAmenitiesFilters = StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_NONE;
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
        flagRoomAmenitiesFilters = stayCurationOption.flagRoomAmenitiesFilters;
    }

    @Override
    public boolean isDefaultFilter()
    {
        return isDefaultSortType() == true//
            && person == StayFilter.DEFAULT_PERSON//
            && flagBedTypeFilters == StayFilter.FLAG_HOTEL_FILTER_BED_NONE//
            && flagAmenitiesFilters == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_NONE//
            && flagRoomAmenitiesFilters == StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_NONE;
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
    public String toBedTypeString(char delimiter)
    {
        if (ADJUST_DELIMITER != delimiter)
        {
            delimiter = GA_DELIMITER;
        }

        StringBuilder result = new StringBuilder();

        if (flagBedTypeFilters == StayFilter.FLAG_HOTEL_FILTER_BED_NONE)
        {
            result.append(AnalyticsManager.Label.SORTFILTER_NONE);
        } else
        {
            if ((flagBedTypeFilters & StayFilter.FLAG_HOTEL_FILTER_BED_DOUBLE) == StayFilter.FLAG_HOTEL_FILTER_BED_DOUBLE)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_DOUBLE).append(delimiter);
            }

            if ((flagBedTypeFilters & StayFilter.FLAG_HOTEL_FILTER_BED_TWIN) == StayFilter.FLAG_HOTEL_FILTER_BED_TWIN)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_TWIN).append(delimiter);
            }

            if ((flagBedTypeFilters & StayFilter.FLAG_HOTEL_FILTER_BED_HEATEDFLOORS) == StayFilter.FLAG_HOTEL_FILTER_BED_HEATEDFLOORS)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_ONDOL).append(delimiter);
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
        if (ADJUST_DELIMITER != delimiter)
        {
            delimiter = GA_DELIMITER;
        }

        StringBuilder result = new StringBuilder();

        if (flagAmenitiesFilters == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_NONE)
        {
            result.append(AnalyticsManager.Label.SORTFILTER_NONE);
        } else
        {
            if ((flagAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_AMENITIES_PARKING) == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_PARKING)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_PARKINGAVAILABLE).append(delimiter);
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_AMENITIES_POOL) == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_POOL)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_POOL).append(delimiter);
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_AMENITIES_FITNESS) == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_FITNESS)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_FITNESS).append(delimiter);
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_AMENITIES_SAUNA) == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_SAUNA)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_SAUNA).append(delimiter);
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_AMENITIES_BUSINESS_CENTER) == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_BUSINESS_CENTER)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_BUSINESS_CENTER).append(delimiter);
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_AMENITIES_KIDS_PLAY_ROOM) == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_KIDS_PLAY_ROOM)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_KIDS_PLAY_ROOM).append(delimiter);
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_AMENITIES_SHARED_BBQ) == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_SHARED_BBQ)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_BBQ).append(delimiter);
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_AMENITIES_PET) == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_PET)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_PET).append(delimiter);
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
    public String toRoomAmenitiesString(char delimiter)
    {
        if (ADJUST_DELIMITER != delimiter)
        {
            delimiter = GA_DELIMITER;
        }

        StringBuilder result = new StringBuilder();

        if (flagRoomAmenitiesFilters == StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_NONE)
        {
            result.append(AnalyticsManager.Label.SORTFILTER_NONE);
        } else
        {
            if ((flagRoomAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_BREAKFAST) == StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_BREAKFAST)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_FREE_BREAKFAST).append(delimiter);
            }

            if ((flagRoomAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_WIFI) == StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_WIFI)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_WIFI).append(delimiter);
            }

            if ((flagRoomAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_COOKING) == StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_COOKING)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_KITCHEN).append(delimiter);
            }

            if ((flagRoomAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_PC) == StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_PC)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_PC).append(delimiter);
            }

            if ((flagRoomAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_BATHTUB) == StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_BATHTUB)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_BATHTUB).append(delimiter);
            }

            if ((flagRoomAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_TV) == StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_TV)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_TV).append(delimiter);
            }

            if ((flagRoomAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_SPA_WHIRLPOOL) == StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_SPA_WHIRLPOOL)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_SPA_WHIRLPOOL).append(delimiter);
            }

            if ((flagRoomAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_PRIVATE_BBQ) == StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_PRIVATE_BBQ)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_PRIVATE_BBQ).append(delimiter);
            }

            if ((flagRoomAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_KARAOKE) == StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_KARAOKE)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_KARAOKE).append(delimiter);
            }

            if ((flagRoomAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_PARTY_ROOM) == StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_PARTY_ROOM)
            {
                result.append(AnalyticsManager.Label.SORTFILTER_PARTYROOM).append(delimiter);
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
        result.append(person);
        result.append('-');
        result.append(toBedTypeString(GA_DELIMITER));
        result.append('-');
        result.append(toAmenitiesString(GA_DELIMITER));
        result.append('-');
        result.append(toRoomAmenitiesString(GA_DELIMITER));

        return result.toString();
    }

    @Override
    public String toAdjustString()
    {
        StringBuilder result = new StringBuilder();

        result.append("[sort:");
        result.append(toSortString());
        result.append(",persons:");
        result.append(person);
        result.append(",type:");
        result.append(toBedTypeString(ADJUST_DELIMITER));
        result.append(",facility:");
        result.append(toAmenitiesString(ADJUST_DELIMITER));
        result.append(",Room facility:");
        result.append(toRoomAmenitiesString(ADJUST_DELIMITER));
        result.append("]");

        return result.toString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);

        dest.writeInt(person);
        dest.writeInt(flagBedTypeFilters);
        dest.writeInt(flagAmenitiesFilters);
        dest.writeInt(flagRoomAmenitiesFilters);
    }

    protected void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

        person = in.readInt();
        flagBedTypeFilters = in.readInt();
        flagAmenitiesFilters = in.readInt();
        flagRoomAmenitiesFilters = in.readInt();
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
