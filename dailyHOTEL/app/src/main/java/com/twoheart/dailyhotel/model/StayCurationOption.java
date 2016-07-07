package com.twoheart.dailyhotel.model;

import android.os.Parcel;

import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StayCurationOption extends PlaceCurationOption
{
    public int person;
    public int flagBedTypeFilters;
    public int flagAmenitiesFilters; // luxuries

    private ArrayList<StayFilters> mStayFiltersList;

    public StayCurationOption()
    {
        mStayFiltersList = new ArrayList<>();

        clear();
    }

    public StayCurationOption(Parcel in)
    {
        mStayFiltersList = new ArrayList<>();

        clear();

        readFromParcel(in);
    }

    public void setFiltersList(ArrayList<StayFilters> arrayList)
    {
        mStayFiltersList.clear();

        if (arrayList != null)
        {
            mStayFiltersList.addAll(arrayList);
        }
    }

    public void setFiltersListByJson(JSONArray jsonArray)
    {
        mStayFiltersList.clear();

        if (jsonArray == null)
        {
            return;
        }

        int length = jsonArray.length();
        if (length == 0)
        {
            return;
        }

        for (int i = 0; i < length; i++)
        {
            try
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String category = jsonObject.getString("category");

                mStayFiltersList.add(makeHotelFilters(category, jsonObject));
            } catch (JSONException e)
            {
                ExLog.d(e.toString());
            }
        }
    }

    private StayFilters makeHotelFilters(String categoryCode, JSONObject jsonObject) throws JSONException
    {
        if (jsonObject == null)
        {
            return null;
        }

        JSONArray jsonArray = jsonObject.getJSONArray("hotelRoomElementList");

        if (jsonArray == null || jsonArray.length() == 0)
        {
            return null;
        }

        int length = jsonArray.length();
        StayFilters hotelFilters = new StayFilters(length);
        hotelFilters.categoryCode = categoryCode;

        for (int i = 0; i < length; i++)
        {
            JSONObject jsonFilter = jsonArray.getJSONObject(i);
            jsonFilter.put("parking", jsonObject.getString("parking"));
            jsonFilter.put("pool", jsonObject.getString("pool"));
            jsonFilter.put("fitness", jsonObject.getString("fitness"));

            hotelFilters.setHotelFilter(i, jsonFilter);
        }

        return hotelFilters;
    }

    public ArrayList<StayFilters> getFiltersList()
    {
        return mStayFiltersList;
    }

    public void clear()
    {
        super.clear();

        person = StayFilter.MIN_PERSON;
        flagBedTypeFilters = StayFilter.FLAG_HOTEL_FILTER_BED_NONE;
        flagAmenitiesFilters = StayFilter.FLAG_HOTEL_FILTER_AMENITIES_NONE;
    }

    public void setCurationOption(StayCurationOption option)
    {
        setSortType(option.getSortType());
        setFiltersList(option.getFiltersList());

        person = option.person;
        flagBedTypeFilters = option.flagBedTypeFilters;
        flagAmenitiesFilters = option.flagAmenitiesFilters;

    }

    public boolean isDefaultFilter()
    {
        if (getSortType() != Constants.SortType.DEFAULT//
            || person != StayFilter.MIN_PERSON//
            || flagBedTypeFilters != StayFilter.FLAG_HOTEL_FILTER_BED_NONE//
            || flagAmenitiesFilters != StayFilter.FLAG_HOTEL_FILTER_AMENITIES_NONE)
        {
            return false;
        }

        return true;
    }

    public String getParamStringByBedTypes()
    {
        if (flagBedTypeFilters == StayFilter.FLAG_HOTEL_FILTER_BED_NONE)
        {
            return null;
        }

        String prefix = "bedType=";
        StringBuilder sb = new StringBuilder();

        if ((flagBedTypeFilters & StayFilter.FLAG_HOTEL_FILTER_BED_DOUBLE) == StayFilter.FLAG_HOTEL_FILTER_BED_DOUBLE)
        {
            sb.append(prefix).append(AnalyticsManager.Label.SORTFILTER_DOUBLE).append("&");
        }

        if ((flagBedTypeFilters & StayFilter.FLAG_HOTEL_FILTER_BED_TWIN) == StayFilter.FLAG_HOTEL_FILTER_BED_TWIN)
        {
            sb.append(prefix).append(AnalyticsManager.Label.SORTFILTER_TWIN).append("&");
        }

        if ((flagBedTypeFilters & StayFilter.FLAG_HOTEL_FILTER_BED_HEATEDFLOORS) == StayFilter.FLAG_HOTEL_FILTER_BED_HEATEDFLOORS)
        {
            sb.append(prefix).append(AnalyticsManager.Label.SORTFILTER_ONDOL).append("&");
        }

        int length = sb.length();
        if (length > 0)
        {
            sb.setLength(length - 1);
            //            sb.delete(length - 1, length);
        }

        return sb.toString();
    }

    public String getParamStingByAmenities()
    {
        if (flagAmenitiesFilters == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_NONE)
        {
            return null;
        }

        String prefix = "luxury=";
        StringBuilder sb = new StringBuilder();

        if ((flagAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_AMENITIES_WIFI) == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_WIFI)
        {
            sb.append(prefix).append("Wifi").append("&");
        }

        if ((flagAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_AMENITIES_BREAKFAST) == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_BREAKFAST)
        {
            sb.append(prefix).append("Breakfast").append("&");
        }

        if ((flagAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_AMENITIES_COOKING) == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_COOKING)
        {
            sb.append(prefix).append("Cooking").append("&");
        }

        if ((flagAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_AMENITIES_BATH) == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_BATH)
        {
            sb.append(prefix).append("Bath").append("&");
        }

        if ((flagAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_AMENITIES_PARKING) == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_PARKING)
        {
            sb.append(prefix).append("Parking").append("&");
        }

        if ((flagAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_AMENITIES_POOL) == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_POOL)
        {
            sb.append(prefix).append("Pool").append("&");
        }

        if ((flagAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_AMENITIES_FITNESS) == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_FITNESS)
        {
            sb.append(prefix).append("Fitness").append("&");
        }

        int length = sb.length();
        if (length > 0)
        {
            sb.setLength(length - 1);
            //            sb.delete(length - 1, length);
        }

        return sb.toString();
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
            boolean isFirst = true;

            if ((flagBedTypeFilters & StayFilter.FLAG_HOTEL_FILTER_BED_DOUBLE) == StayFilter.FLAG_HOTEL_FILTER_BED_DOUBLE)
            {
                isFirst = false;
                result.append(AnalyticsManager.Label.SORTFILTER_DOUBLE);
            }

            if ((flagBedTypeFilters & StayFilter.FLAG_HOTEL_FILTER_BED_TWIN) == StayFilter.FLAG_HOTEL_FILTER_BED_TWIN)
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

            if ((flagBedTypeFilters & StayFilter.FLAG_HOTEL_FILTER_BED_HEATEDFLOORS) == StayFilter.FLAG_HOTEL_FILTER_BED_HEATEDFLOORS)
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

        if (flagAmenitiesFilters == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_NONE)
        {
            result.append(AnalyticsManager.Label.SORTFILTER_NONE);
        } else
        {
            boolean isFirst = true;

            if ((flagAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_AMENITIES_WIFI) == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_WIFI)
            {
                isFirst = false;
                result.append(AnalyticsManager.Label.SORTFILTER_WIFI);
            }

            if ((flagAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_AMENITIES_BREAKFAST) == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_BREAKFAST)
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

            if ((flagAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_AMENITIES_COOKING) == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_COOKING)
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

            if ((flagAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_AMENITIES_BATH) == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_BATH)
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

            if ((flagAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_AMENITIES_PARKING) == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_PARKING)
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

            if ((flagAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_AMENITIES_POOL) == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_POOL)
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

            if ((flagAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_AMENITIES_FITNESS) == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_FITNESS)
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
        dest.writeTypedList(mStayFiltersList);
        dest.writeInt(flagAmenitiesFilters);
    }

    protected void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

        person = in.readInt();
        flagBedTypeFilters = in.readInt();
        in.readTypedList(mStayFiltersList, StayFilters.CREATOR);
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
