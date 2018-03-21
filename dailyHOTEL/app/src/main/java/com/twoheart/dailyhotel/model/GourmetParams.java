package com.twoheart.dailyhotel.model;

import android.location.Location;
import android.os.Parcel;

import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GourmetParams extends PlaceParams
{
    protected String date;

    protected List<String> mTimeList;
    protected List<String> mCategoryList;
    protected List<String> mLuxuryList;

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

        GourmetBookingDay gourmetBookingDay = gourmetCuration.getGourmetBookingDay();

        if (gourmetBookingDay != null)
        {
            date = gourmetBookingDay.getVisitDay("yyyy-MM-dd");
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
            mCategoryList = toParamListByCategory(gourmetCurationOption.getFilterMap());
            mTimeList = toParamListByTime(gourmetCurationOption.flagTimeFilter);
            mLuxuryList = toParamListByAmenities(gourmetCurationOption.flagAmenitiesFilters);
            mSort = gourmetCurationOption.getSortType();
        }

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

        Map<String, Object> map = toParamsMap();

        for (Map.Entry<String, Object> entry : map.entrySet())
        {
            stringBuilder.append(entry.getKey()).append('=').append(entry.getValue()).append('&');
        }

        // 마지막 & 없애기
        stringBuilder.setLength(stringBuilder.length() - 1);

        List<String> categoryList = getCategoryList();

        if (categoryList != null && categoryList.size() > 0)
        {
            for (String category : categoryList)
            {
                stringBuilder.append("&category").append('=').append(category);
            }
        }

        List<String> timeList = getTimeList();

        if (timeList != null && timeList.size() > 0)
        {
            for (String time : timeList)
            {
                stringBuilder.append("&timeFrame").append('=').append(time);
            }
        }

        List<String> luxuryList = getLuxuryList();

        if (luxuryList != null && luxuryList.size() > 0)
        {
            for (String luxury : luxuryList)
            {
                stringBuilder.append("&luxury").append('=').append(luxury);
            }
        }

        return stringBuilder.toString();
    }

    public List<String> getCategoryList()
    {
        return mCategoryList;
    }

    public List<String> getTimeList()
    {
        return mTimeList;
    }

    public List<String> getLuxuryList()
    {
        return mLuxuryList;
    }

    public Map<String, Object> toParamsMap()
    {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("reserveDate", date);

        if (provinceIdx != 0)
        {
            hashMap.put("provinceIdx", provinceIdx);
        }

        if (areaIdx > 0)
        {
            hashMap.put("areaIdx", areaIdx);
        }

        if (subwayIndex != 0)
        {
            hashMap.put("subwayIdx", subwayIndex);
        }

        if (persons != 0)
        {
            hashMap.put("persons", persons);
        }

        if (page > 0)
        {
            hashMap.put("page", page);
            hashMap.put("limit", limit);
        }

        if (Constants.SortType.DEFAULT != mSort)
        {
            if (DailyTextUtils.isTextEmpty(sortProperty) == false)
            {
                hashMap.put("sortProperty", sortProperty);
            }

            if (DailyTextUtils.isTextEmpty(sortDirection) == false)
            {
                hashMap.put("sortDirection", sortDirection);
            }

            if (Constants.SortType.DISTANCE == mSort && hasLocation() == true)
            {
                hashMap.put("latitude", latitude);
                hashMap.put("longitude", longitude);
            }
        }

        hashMap.put("details", details);

        return hashMap;
    }

    protected List<String> toParamListByCategory(HashMap<String, Integer> map)
    {
        if (map == null || map.size() == 0)
        {
            return null;
        }

        ArrayList<String> arrayList = new ArrayList();
        ArrayList<Integer> categoryCodeList = new ArrayList<>(map.values());

        for (int categoryCode : categoryCodeList)
        {
            arrayList.add(Integer.toString(categoryCode));
        }

        return arrayList;
    }

    protected List<String> toParamListByTime(int flagTimeFilter)
    {
        if (flagTimeFilter == GourmetFilter.Time.FLAG_NONE)
        {
            return null;
        }

        ArrayList<String> arrayList = new ArrayList();

        if ((flagTimeFilter & GourmetFilter.Time.FLAG_06_11) == GourmetFilter.Time.FLAG_06_11)
        {
            arrayList.add("06_11");
        }

        if ((flagTimeFilter & GourmetFilter.Time.FLAG_11_15) == GourmetFilter.Time.FLAG_11_15)
        {
            arrayList.add("11_15");
        }

        if ((flagTimeFilter & GourmetFilter.Time.FLAG_15_17) == GourmetFilter.Time.FLAG_15_17)
        {
            arrayList.add("15_17");
        }

        if ((flagTimeFilter & GourmetFilter.Time.FLAG_17_21) == GourmetFilter.Time.FLAG_17_21)
        {
            arrayList.add("17_21");
        }

        if ((flagTimeFilter & GourmetFilter.Time.FLAG_21_06) == GourmetFilter.Time.FLAG_21_06)
        {
            arrayList.add("21_06");
        }

        return arrayList;
    }

    protected List<String> toParamListByAmenities(int flagAmenitiesFilters)
    {
        if (flagAmenitiesFilters == GourmetFilter.Amenities.FLAG_NONE)
        {
            return null;
        }

        ArrayList<String> arrayList = new ArrayList();

        if ((flagAmenitiesFilters & GourmetFilter.Amenities.FLAG_PARKING) == GourmetFilter.Amenities.FLAG_PARKING)
        {
            arrayList.add("Parking");
        }

        if ((flagAmenitiesFilters & GourmetFilter.Amenities.FLAG_VALET) == GourmetFilter.Amenities.FLAG_VALET)
        {
            arrayList.add("Valet");
        }

        if ((flagAmenitiesFilters & GourmetFilter.Amenities.FLAG_BABYSEAT) == GourmetFilter.Amenities.FLAG_BABYSEAT)
        {
            arrayList.add("BabySeat");
        }

        if ((flagAmenitiesFilters & GourmetFilter.Amenities.FLAG_PRIVATEROOM) == GourmetFilter.Amenities.FLAG_PRIVATEROOM)
        {
            arrayList.add("PrivateRoom");
        }

        if ((flagAmenitiesFilters & GourmetFilter.Amenities.FLAG_GROUPBOOKING) == GourmetFilter.Amenities.FLAG_GROUPBOOKING)
        {
            arrayList.add("GroupBooking");
        }

        if ((flagAmenitiesFilters & GourmetFilter.Amenities.FLAG_CORKAGE) == GourmetFilter.Amenities.FLAG_CORKAGE)
        {
            arrayList.add("Corkage");
        }

        return arrayList;
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

    @Override
    protected void clear()
    {
        super.clear();

        date = null;
        mCategoryList = null;
        mTimeList = null;
        mLuxuryList = null;
    }

    protected void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

        date = in.readString();
        mCategoryList = in.readArrayList(String.class.getClassLoader());
        mTimeList = in.readArrayList(String.class.getClassLoader());
        mLuxuryList = in.readArrayList(String.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);

        dest.writeString(date);
        dest.writeList(mCategoryList);
        dest.writeList(mTimeList);
        dest.writeList(mLuxuryList);
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
