package com.twoheart.dailyhotel.model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

import java.net.URLEncoder;

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
        return toParamsString(true);
    }

    public String toParamsString(boolean isTermEncode)
    {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(getParamString("dateCheckIn", dateCheckIn)).append("&");
        stringBuilder.append(getParamString("stays", stays)).append("&");

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

        String categoryString = getCategoryString();
        if (Util.isTextEmpty(categoryString) == false)
        {
            stringBuilder.append(categoryString).append("&");
        }

        if (Util.isTextEmpty(bedType) == false)
        {
            stringBuilder.append(bedType).append("&");
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
            stringBuilder.append(getParamString("term", isTermEncode == true ? URLEncoder.encode(term) : term)).append("&");
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
        if (length > 0)
        {
            stringBuilder.setLength(length - 1);
        }

        //        ExLog.d(" params : " + sb.toString());
        return stringBuilder.toString();
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
