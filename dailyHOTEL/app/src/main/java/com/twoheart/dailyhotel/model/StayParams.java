package com.twoheart.dailyhotel.model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

/**
 * Created by android_sam on 2016. 6. 30..
 */
public class StayParams implements Parcelable
{
    public String dateCheckIn;
    public int stays;
    public int provinceIdx;
    public int areaIdx;
    public int persons;
    public Category category;
    public String bedType; // curationOption에서 가져온 스트링
    public String luxury; // curationOption에서 가져온 스트링
    public double longitude;
    public double latitude;
    public int page;
    public int limit;
    public String sortProperty;
    public String sortDirection;
    public boolean details;

    public StayParams()
    {
    }

    public StayParams(StayCuration stayCuration)
    {
        setStayParams(stayCuration);
    }

    public StayParams(Parcel in)
    {
        readFromParcel(in);
    }

    public void setStayParams(StayCuration stayCuration)
    {
        if (stayCuration == null)
        {
            return;
        }

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
            bedType = stayCurationOption.getParamStringByBedTypes(); // curationOption에서 가져온 스트링
            luxury = stayCurationOption.getParamStingByAmenities(); // curationOption에서 가져온 스트링
        }

        Constants.SortType sortType = stayCurationOption.getSortType();
        setSortType(sortType);

        if (Constants.SortType.DISTANCE == sortType)
        {
            Location location = stayCuration.getLocation();

            if (location != null)
            {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        }
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

    public Constants.SortType getSortType()
    {
        if (Util.isTextEmpty(sortProperty) == true)
        {
            return Constants.SortType.DEFAULT;
        }

        switch (sortProperty)
        {
            case "Distance":
                return Constants.SortType.DISTANCE;
            case "Price":
                if ("Desc".equalsIgnoreCase(sortDirection))
                {
                    return Constants.SortType.LOW_PRICE;
                }

                return Constants.SortType.HIGH_PRICE;
            case "Rating":
                return Constants.SortType.SATISFACTION;
        }

        return Constants.SortType.DEFAULT;
    }

    public boolean hasLocation()
    {
        return (latitude == 0d || longitude == 0d) ? false : true;
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
    public String toParamString()
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

        Constants.SortType sortType = getSortType();
        if (Constants.SortType.DEFAULT != sortType)
        {
            sb.append(getParamString("sortProperty", sortProperty)).append("&");
            sb.append(getParamString("sortDirection", sortDirection)).append("&");

            if (Constants.SortType.DISTANCE == sortType && hasLocation() == true)
            {
                sb.append(getParamString("latitude", latitude)).append("&");
                sb.append(getParamString("longitude", longitude)).append("&");
            }
        }

        sb.append(getParamString("details", details)).append("&");

        removeLastAndCoupler(sb);

        //        ExLog.d(" params : " + sb.toString());
        return sb.toString();
    }

    private String getCategoryString()
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

    private StringBuilder removeLastAndCoupler(StringBuilder sb)
    {
        if (Util.isTextEmpty(sb.toString()) == false)
        {
            int length = sb.length();
            if (length > 0)
            {
                String dest = sb.substring(length - 1);
                if ("&".equalsIgnoreCase(dest) == true)
                {
                    sb.setLength(length - 1);
                }
            }
        }

        return sb;
    }

    private String getParamString(String key, Object value)
    {
        String stringValue = String.valueOf(value);
        if (Util.isTextEmpty(stringValue))
        {
            return "";
        }

        return String.format("%s=%s", key, stringValue);
    }

    private void clear()
    {
        dateCheckIn = null;
        stays = 1;
        provinceIdx = 0;
        areaIdx = 0;
        persons = 0;
        category = Category.ALL;
        bedType = null;
        luxury = null;
        longitude = 0.0d;
        latitude = 0.0d;
        page = 0;
        limit = 0;
        sortProperty = null;
        sortDirection = null;
        details = false;
    }

    protected void readFromParcel(Parcel in)
    {
        dateCheckIn = in.readString();
        stays = in.readInt();
        provinceIdx = in.readInt();
        areaIdx = in.readInt();
        persons = in.readInt();
        category = in.readParcelable(Category.class.getClassLoader());
        bedType = in.readString();
        luxury = in.readString();
        longitude = in.readDouble();
        latitude = in.readDouble();
        page = in.readInt();
        limit = in.readInt();
        sortProperty = in.readString();
        sortDirection = in.readString();
        details = in.readInt() == 1 ? true : false;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(dateCheckIn);
        dest.writeInt(stays);
        dest.writeInt(provinceIdx);
        dest.writeInt(areaIdx);
        dest.writeInt(persons);
        dest.writeParcelable(category, flags);
        dest.writeString(bedType);
        dest.writeString(luxury);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
        dest.writeInt(page);
        dest.writeInt(limit);
        dest.writeString(sortProperty);
        dest.writeString(sortDirection);
        dest.writeInt(details ? 1 : 0);
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
