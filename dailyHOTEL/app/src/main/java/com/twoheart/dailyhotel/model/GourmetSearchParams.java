package com.twoheart.dailyhotel.model;

import android.location.Location;
import android.os.Parcel;

import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;
import java.util.HashMap;

public class GourmetSearchParams extends GourmetParams
{
    private String term;
    private double radius;

    public GourmetSearchParams(PlaceCuration placeCuration)
    {
        super(placeCuration);
    }

    public GourmetSearchParams(Parcel in)
    {
        super(in);
    }

    @Override
    public void setPlaceParams(PlaceCuration placeCuration)
    {
        if (placeCuration == null)
        {
            return;
        }

        GourmetSearchCuration gourmetSearchCuration = (GourmetSearchCuration) placeCuration;

        clear();

        SaleTime saleTime = gourmetSearchCuration.getSaleTime();

        if (saleTime != null)
        {
            date = saleTime.getDayOfDaysDateFormat("yyyy-MM-dd");
        }

        GourmetCurationOption gourmetCurationOption = (GourmetCurationOption) gourmetSearchCuration.getCurationOption();

        if (gourmetCurationOption != null)
        {
            category = toParamStringByCategory(gourmetCurationOption.getFilterMap());
            time = toParamStringByTime(gourmetCurationOption.flagTimeFilter);
            luxury = toParamStingByAmenities(gourmetCurationOption.flagAmenitiesFilters);
        }

        mSort = gourmetCurationOption.getSortType();
        setSortType(mSort);

        term = gourmetSearchCuration.getKeyword() == null ? null : gourmetSearchCuration.getKeyword().name;
        radius = gourmetSearchCuration.getRadius();

        Location location = gourmetSearchCuration.getLocation();
        if (location != null)
        {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    /**
     * http://dev.dailyhotel.me/goodnight/api/v3/hotels/sales?
     * dateCheckIn=2016-06-18
     * &stays=3
     * &provinceIdx=5000
     * &areaIdx=2
     * &persons=3
     * &category=Hotel&category=Boutique&category=GuestHouse
     * &bedType=Double&bedType=Twin&bedType=Ondol
     * &luxury=Breakfast&luxury=Cooking&luxury=Bath
     * &longitude=37.505067
     * &latitude=127.057053
     * &page=1
     * &limit=20
     * &sortProperty=Price
     * &sortDirection=Desc
     * &details=true
     */
    @Override
    public String toParamsString()
    {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(getParamString("reserveDate", date)).append("&");

        if (provinceIdx != 0)
        {
            stringBuilder.append(getParamString("provinceIdx", provinceIdx)).append("&");
        }

        if (areaIdx != 0)
        {
            stringBuilder.append(getParamString("areaIdx", areaIdx)).append("&");
        }

        if (persons != 0)
        {
            stringBuilder.append(getParamString("persons", persons)).append("&");
        }

        if (Util.isTextEmpty(category) == false)
        {
            stringBuilder.append(category).append("&");
        }

        if (Util.isTextEmpty(time) == false)
        {
            stringBuilder.append(time).append("&");
        }

        if (Util.isTextEmpty(luxury) == false)
        {
            stringBuilder.append(luxury).append("&");
        }

        if (page > 0)
        {
            stringBuilder.append(getParamString("page", page)).append("&");
            stringBuilder.append(getParamString("limit", limit)).append("&");
        }

        if (Util.isTextEmpty(term) == false)
        {
            stringBuilder.append(getParamString("term", term)).append("&");
        }

        boolean isNeedLocation = false;

        if (radius != 0d)
        {
            stringBuilder.append(getParamString("radius", radius)).append("&");

            isNeedLocation = true;
        }

        if (Constants.SortType.DEFAULT != mSort)
        {
            stringBuilder.append(getParamString("sortProperty", sortProperty)).append("&");
            stringBuilder.append(getParamString("sortDirection", sortDirection)).append("&");

            if (Constants.SortType.DISTANCE == mSort)
            {
                isNeedLocation = true;
            }
        }

        if (hasLocation() == true && isNeedLocation == true)
        {
            stringBuilder.append(getParamString("latitude", latitude)).append("&");
            stringBuilder.append(getParamString("longitude", longitude)).append("&");
        }

        stringBuilder.append(getParamString("details", details)).append("&");

        int length = stringBuilder.length();
        if (stringBuilder.charAt(length - 1) == '&')
        {
            stringBuilder.setLength(length - 1);
        }

        return stringBuilder.toString();
    }

    @Override
    public void setSortType(Constants.SortType sortType)
    {
        switch (sortType)
        {
            case DEFAULT:
                sortProperty = null;
                sortDirection = null;
                break;

            case DISTANCE:
                sortProperty = "Distance";
                sortDirection = "Asc";
                break;

            case LOW_PRICE:
                sortProperty = "PricePerPerson";
                sortDirection = "Asc";
                break;

            case HIGH_PRICE:
                sortProperty = "PricePerPerson";
                sortDirection = "Desc";
                break;

            case SATISFACTION:
                sortProperty = "Rating";
                sortDirection = null;
                break;
        }
    }

    private String toParamStringByCategory(HashMap<String, Integer> map)
    {
        if (map == null || map.size() == 0)
        {
            return null;
        }

        String prefix = "category=";
        StringBuilder stringBuilder = new StringBuilder();

        ArrayList<Integer> categoryCodeList = new ArrayList(map.values());

        for (int categoryCode : categoryCodeList)
        {
            stringBuilder.append(prefix).append(categoryCode).append("&");
        }

        int length = stringBuilder.length();
        if (stringBuilder.charAt(length - 1) == '&')
        {
            stringBuilder.setLength(length - 1);
        }

        return stringBuilder.toString();
    }

    private String toParamStringByTime(int flagTimeFilter)
    {
        if (flagTimeFilter == GourmetFilter.FLAG_GOURMET_FILTER_TIME_NONE)
        {
            return null;
        }

        String prefix = "timeFrame=";
        StringBuilder stringBuilder = new StringBuilder();

        if ((flagTimeFilter & GourmetFilter.FLAG_GOURMET_FILTER_TIME_06_11) == GourmetFilter.FLAG_GOURMET_FILTER_TIME_06_11)
        {
            stringBuilder.append(prefix).append("6_11").append("&");
        }

        if ((flagTimeFilter & GourmetFilter.FLAG_GOURMET_FILTER_TIME_11_15) == GourmetFilter.FLAG_GOURMET_FILTER_TIME_11_15)
        {
            stringBuilder.append(prefix).append("11_15").append("&");
        }

        if ((flagTimeFilter & GourmetFilter.FLAG_GOURMET_FILTER_TIME_15_17) == GourmetFilter.FLAG_GOURMET_FILTER_TIME_15_17)
        {
            stringBuilder.append(prefix).append("15_17").append("&");
        }

        if ((flagTimeFilter & GourmetFilter.FLAG_GOURMET_FILTER_TIME_17_21) == GourmetFilter.FLAG_GOURMET_FILTER_TIME_17_21)
        {
            stringBuilder.append(prefix).append("17_21").append("&");
        }

        if ((flagTimeFilter & GourmetFilter.FLAG_GOURMET_FILTER_TIME_21_06) == GourmetFilter.FLAG_GOURMET_FILTER_TIME_21_06)
        {
            stringBuilder.append(prefix).append("21_6").append("&");
        }

        int length = stringBuilder.length();
        if (stringBuilder.charAt(length - 1) == '&')
        {
            stringBuilder.setLength(length - 1);
        }

        return stringBuilder.toString();
    }

    private String toParamStingByAmenities(int flagAmenitiesFilters)
    {
        if (flagAmenitiesFilters == GourmetFilters.FLAG_GOURMET_FILTER_AMENITIES_NONE)
        {
            return null;
        }

        String prefix = "luxury=";
        StringBuilder stringBuilder = new StringBuilder();

        if ((flagAmenitiesFilters & GourmetFilters.FLAG_GOURMET_FILTER_AMENITIES_PARKING) == GourmetFilters.FLAG_GOURMET_FILTER_AMENITIES_PARKING)
        {
            stringBuilder.append(prefix).append("Parking").append("&");
        }

        int length = stringBuilder.length();
        if (stringBuilder.charAt(length - 1) == '&')
        {
            stringBuilder.setLength(length - 1);
        }

        return stringBuilder.toString();
    }

    @Override
    protected void clear()
    {
        super.clear();

        date = null;
        category = null;
        time = null;
        luxury = null;
    }

    protected void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

        term = in.readString();
        radius = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);

        dest.writeString(term);
        dest.writeDouble(radius);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public GourmetSearchParams createFromParcel(Parcel in)
        {
            return new GourmetSearchParams(in);
        }

        @Override
        public GourmetSearchParams[] newArray(int size)
        {
            return new GourmetSearchParams[size];
        }
    };
}