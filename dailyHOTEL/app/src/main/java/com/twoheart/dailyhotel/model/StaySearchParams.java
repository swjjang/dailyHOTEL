package com.twoheart.dailyhotel.model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

/**
 * Created by android_sam on 2016. 7. 28..
 */
public class StaySearchParams extends StayParams
{
    private String term;
    private double radius;

    public StaySearchParams(PlaceCuration placeCuration)
    {
        super(placeCuration);
    }

    public StaySearchParams(Parcel in)
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

        StaySearchCuration staySearchCuration = (StaySearchCuration) placeCuration;

        clear();

        SaleTime checkInSaleTime = staySearchCuration.getCheckInSaleTime();
        SaleTime checkOutSaleTime = staySearchCuration.getCheckOutSaleTime();

        if (checkInSaleTime != null && checkOutSaleTime != null)
        {
            dateCheckIn = checkInSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd");
            stays = checkOutSaleTime.getOffsetDailyDay() - checkInSaleTime.getOffsetDailyDay();
        }

        Province province = staySearchCuration.getProvince();

        if (province != null)
        {
            provinceIdx = province.getProvinceIndex();

            if (province instanceof Area)
            {
                areaIdx = ((Area) province).index;
            }
        }

        category = staySearchCuration.getCategory();

        StayCurationOption stayCurationOption = (StayCurationOption) staySearchCuration.getCurationOption();

        if (stayCurationOption != null)
        {
            persons = stayCurationOption.person;
            bedType = toParamStringByBedTypes(stayCurationOption.flagBedTypeFilters);
            luxury = toParamStingByAmenities(stayCurationOption.flagAmenitiesFilters);
        }

        mSort = stayCurationOption.getSortType();
        setSortType(mSort);

        term = staySearchCuration.getKeyword() == null ? null : staySearchCuration.getKeyword().name;
        radius = staySearchCuration.getRadius();

        Location location = staySearchCuration.getLocation();
        if (location != null)
        {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    @Override
    public String toParamsString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append(getParamString("dateCheckIn", dateCheckIn)).append("&");
        sb.append(getParamString("stays", stays)).append("&");

        if (provinceIdx != 0)
        {
            sb.append(getParamString("provinceIdx", provinceIdx)).append("&");
        }

        if (areaIdx != 0)
        {
            sb.append(getParamString("areaIdx", areaIdx)).append("&");
        }

        if (persons != 0)
        {
            sb.append(getParamString("persons", persons)).append("&");
        }

        String categoryString = getCategoryString();
        if (Util.isTextEmpty(categoryString) == false)
        {
            sb.append(categoryString).append("&");
        }

        if (Util.isTextEmpty(bedType) == false)
        {
            sb.append(bedType).append("&");
        }

        if (Util.isTextEmpty(luxury) == false)
        {
            sb.append(luxury).append("&");
        }

        if (page > 0)
        {
            sb.append(getParamString("page", page)).append("&");
            sb.append(getParamString("limit", limit)).append("&");
        }

        if (Util.isTextEmpty(term) == false)
        {
            //            sb.append(getParamString("term", URLEncoder.encode(term))).append("&");
            sb.append(getParamString("term", term)).append("&");
        }

        boolean isNeedLocation = false;

        if (radius != 0d)
        {
            sb.append(getParamString("radius", radius)).append("&");

            isNeedLocation = true;
        }

        if (Constants.SortType.DEFAULT != mSort)
        {
            sb.append(getParamString("sortProperty", sortProperty)).append("&");
            sb.append(getParamString("sortDirection", sortDirection)).append("&");

            if (Constants.SortType.DISTANCE == mSort)
            {
                isNeedLocation = true;
            }
        }

        if (hasLocation() == true && isNeedLocation == true)
        {
            sb.append(getParamString("latitude", latitude)).append("&");
            sb.append(getParamString("longitude", longitude)).append("&");
        }

        sb.append(getParamString("details", details)).append("&");

        int length = sb.length();
        if (length > 0)
        {
            sb.setLength(length - 1);
        }

        //        ExLog.d(" params : " + sb.toString());
        return sb.toString();
    }

    @Override
    protected void clear()
    {
        super.clear();

        term = null;
        radius = 0d;
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

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public StaySearchParams createFromParcel(Parcel in)
        {
            return new StaySearchParams(in);
        }

        @Override
        public StaySearchParams[] newArray(int size)
        {
            return new StaySearchParams[size];
        }
    };
}
