package com.twoheart.dailyhotel.model;

import android.location.Location;
import android.os.Parcel;

import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;
import java.util.HashMap;

public class GourmetParams extends PlaceParams
{
    protected String date;
    protected String category;
    protected String time;
    protected String luxury;

    public GourmetParams(PlaceCuration placeCuration)
    {
        super(placeCuration);
    }

    public GourmetParams(Parcel in)
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

        GourmetCuration gourmetCuration = (GourmetCuration) placeCuration;

        clear();

        SaleTime saleTime = gourmetCuration.getSaleTime();

        if (saleTime != null)
        {
            date = saleTime.getDayOfDaysDateFormat("yyyy-MM-dd");
        }

        Province province = gourmetCuration.getProvince();

        if (province != null)
        {
            provinceIdx = province.getProvinceIndex();

            if (province instanceof Area)
            {
                areaIdx = ((Area) province).index;
            }
        }

        GourmetCurationOption gourmetCurationOption = (GourmetCurationOption) gourmetCuration.getCurationOption();

        if (gourmetCurationOption != null)
        {
            category = toParamStringByCategory(gourmetCurationOption.getFilterMap());
            time = toParamStringByTime(gourmetCurationOption.flagTimeFilter);
            luxury = toParamStingByAmenities(gourmetCurationOption.flagAmenitiesFilters);
        }

        mSort = gourmetCurationOption.getSortType();
        setSortType(mSort);

        if (Constants.SortType.DISTANCE == mSort)
        {
            Location location = gourmetCuration.getLocation();

            if (location != null)
            {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
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

        if (Constants.SortType.DEFAULT != mSort)
        {
            stringBuilder.append(getParamString("sortProperty", sortProperty)).append("&");
            stringBuilder.append(getParamString("sortDirection", sortDirection)).append("&");

            if (Constants.SortType.DISTANCE == mSort && hasLocation() == true)
            {
                stringBuilder.append(getParamString("latitude", latitude)).append("&");
                stringBuilder.append(getParamString("longitude", longitude)).append("&");
            }
        }

        stringBuilder.append(getParamString("details", details)).append("&");

        int length = stringBuilder.length();
        if (stringBuilder.charAt(length - 1) == '&')
        {
            stringBuilder.setLength(length - 1);
        }

        //        ExLog.d(" params : " + sb.toString());
        return stringBuilder.toString();
    }

    protected String toParamStringByCategory(HashMap<String, Integer> map)
    {
        if (map == null || map.size() == 0)
        {
            return null;
        }

        String prefix = "category=";
        StringBuilder stringBuilder = new StringBuilder();

        ArrayList<Integer> categoryCodeList = new ArrayList<>(map.values());

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

    protected String toParamStringByTime(int flagTimeFilter)
    {
        if (flagTimeFilter == GourmetFilter.FLAG_GOURMET_FILTER_TIME_NONE)
        {
            return null;
        }

        String prefix = "timeFrame=";
        StringBuilder stringBuilder = new StringBuilder();

        if ((flagTimeFilter & GourmetFilter.FLAG_GOURMET_FILTER_TIME_06_11) == GourmetFilter.FLAG_GOURMET_FILTER_TIME_06_11)
        {
            stringBuilder.append(prefix).append("06_11").append("&");
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
            stringBuilder.append(prefix).append("21_06").append("&");
        }

        int length = stringBuilder.length();
        if (stringBuilder.charAt(length - 1) == '&')
        {
            stringBuilder.setLength(length - 1);
        }

        return stringBuilder.toString();
    }

    protected String toParamStingByAmenities(int flagAmenitiesFilters)
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

        date = in.readString();
        category = in.readString();
        time = in.readString();
        luxury = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);

        dest.writeString(date);
        dest.writeString(category);
        dest.writeString(time);
        dest.writeString(luxury);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public GourmetParams createFromParcel(Parcel in)
        {
            return new GourmetParams(in);
        }

        @Override
        public GourmetParams[] newArray(int size)
        {
            return new GourmetParams[size];
        }
    };
}
