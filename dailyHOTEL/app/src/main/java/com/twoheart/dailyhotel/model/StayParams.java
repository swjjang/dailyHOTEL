package com.twoheart.dailyhotel.model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

/**
 * Created by android_sam on 2016. 6. 30..
 */
public class StayParams extends PlaceParams
{
    protected String dateCheckIn;
    protected int stays;
    protected Category category;
    protected String bedType;
    protected String luxury;

    public StayParams(PlaceCuration placeCuration)
    {
        super(placeCuration);
    }

    public StayParams(Parcel in)
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

        StayCuration stayCuration = (StayCuration) placeCuration;

        clear();

        SaleTime checkInSaleTime = stayCuration.getCheckInSaleTime();
        SaleTime checkOutSaleTime = stayCuration.getCheckOutSaleTime();

        if (checkInSaleTime != null && checkOutSaleTime != null)
        {
            dateCheckIn = checkInSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd");
            stays = checkOutSaleTime.getOffsetDailyDay() - checkInSaleTime.getOffsetDailyDay();
        }

        Province province = stayCuration.getProvince();

        if (province != null)
        {
            provinceIdx = province.getProvinceIndex();

            if (province instanceof Area)
            {
                areaIdx = ((Area) province).index;
            }
        }

        category = stayCuration.getCategory();

        StayCurationOption stayCurationOption = (StayCurationOption) stayCuration.getCurationOption();

        if (stayCurationOption != null)
        {
            persons = stayCurationOption.person;
            bedType = toParamStringByBedTypes(stayCurationOption.flagBedTypeFilters);
            luxury = toParamStingByAmenities(stayCurationOption.flagAmenitiesFilters);
        }

        mSort = stayCurationOption.getSortType();
        setSortType(mSort);

        Location location = stayCuration.getLocation();
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
        StringBuilder sb = new StringBuilder();

        sb.append(getParamString("dateCheckIn", dateCheckIn)).append("&");
        sb.append(getParamString("stays", stays)).append("&");
        sb.append(getParamString("provinceIdx", provinceIdx)).append("&");

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

        if (Constants.SortType.DEFAULT != mSort)
        {
            sb.append(getParamString("sortProperty", sortProperty)).append("&");
            sb.append(getParamString("sortDirection", sortDirection)).append("&");

            if (Constants.SortType.DISTANCE == mSort && hasLocation() == true)
            {
                sb.append(getParamString("latitude", latitude)).append("&");
                sb.append(getParamString("longitude", longitude)).append("&");
            }
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

    protected String toParamStringByBedTypes(int flagBedTypeFilters)
    {
        if (flagBedTypeFilters == StayFilter.FLAG_HOTEL_FILTER_BED_NONE)
        {
            return null;
        }

        String prefix = "bedType=";
        StringBuilder sb = new StringBuilder();

        if ((flagBedTypeFilters & StayFilter.FLAG_HOTEL_FILTER_BED_DOUBLE) == StayFilter.FLAG_HOTEL_FILTER_BED_DOUBLE)
        {
            sb.append(prefix).append("Double").append("&");
        }

        if ((flagBedTypeFilters & StayFilter.FLAG_HOTEL_FILTER_BED_TWIN) == StayFilter.FLAG_HOTEL_FILTER_BED_TWIN)
        {
            sb.append(prefix).append("Twin").append("&");
        }

        if ((flagBedTypeFilters & StayFilter.FLAG_HOTEL_FILTER_BED_HEATEDFLOORS) == StayFilter.FLAG_HOTEL_FILTER_BED_HEATEDFLOORS)
        {
            sb.append(prefix).append("Ondol").append("&");
        }

        int length = sb.length();
        if (length > 0)
        {
            sb.setLength(length - 1);
        }

        return sb.toString();
    }

    protected String toParamStingByAmenities(int flagAmenitiesFilters)
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

        if ((flagAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_AMENITIES_NOPARKING) == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_NOPARKING)
        {
            sb.append(prefix).append("NoParking").append("&");
        }

        if ((flagAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_AMENITIES_PET) == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_PET)
        {
            sb.append(prefix).append("Pet").append("&");
        }

        if ((flagAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_AMENITIES_SHAREDBBQ) == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_SHAREDBBQ)
        {
            sb.append(prefix).append("SharedBbq").append("&");
        }

        int length = sb.length();
        if (length > 0)
        {
            sb.setLength(length - 1);
        }

        return sb.toString();
    }

    protected String getCategoryString()
    {
        if (category == null)
        {
            return "";
        }

        if (Category.ALL.code.equalsIgnoreCase(category.code))
        {
            // 전체일경우 안보내면 전체임
            return "";
        }

        return getParamString("category", category.code);
    }

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
                sortProperty = "Price";
                sortDirection = "Asc";
                break;

            case HIGH_PRICE:
                sortProperty = "Price";
                sortDirection = "Desc";
                break;

            case SATISFACTION:
                sortProperty = "Rating";
                sortDirection = null;
                break;
        }
    }

    @Override
    protected void clear()
    {
        super.clear();

        dateCheckIn = null;
        stays = 1;
        category = Category.ALL;
        bedType = null;
        luxury = null;
    }

    protected void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

        dateCheckIn = in.readString();
        stays = in.readInt();
        category = in.readParcelable(Category.class.getClassLoader());
        bedType = in.readString();
        luxury = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);

        dest.writeString(dateCheckIn);
        dest.writeInt(stays);
        dest.writeParcelable(category, flags);
        dest.writeString(bedType);
        dest.writeString(luxury);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public StayParams createFromParcel(Parcel in)
        {
            return new StayParams(in);
        }

        @Override
        public StayParams[] newArray(int size)
        {
            return new StayParams[size];
        }
    };
}
