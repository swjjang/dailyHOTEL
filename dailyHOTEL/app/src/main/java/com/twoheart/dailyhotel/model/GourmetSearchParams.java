package com.twoheart.dailyhotel.model;

import android.location.Location;
import android.os.Parcel;

import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.entity.GourmetSuggest;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.util.Constants;

import java.util.HashMap;
import java.util.Map;

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

        GourmetBookingDay gourmetBookingDay = gourmetSearchCuration.getGourmetBookingDay();

        if (gourmetBookingDay != null)
        {
            date = gourmetBookingDay.getVisitDay("yyyy-MM-dd");
        }

        GourmetCurationOption gourmetCurationOption = (GourmetCurationOption) gourmetSearchCuration.getCurationOption();

        if (gourmetCurationOption != null)
        {
            mCategoryList = toParamListByCategory(gourmetCurationOption.getFilterMap());
            mTimeList = toParamListByTime(gourmetCurationOption.flagTimeFilter);
            mLuxuryList = toParamListByAmenities(gourmetCurationOption.flagAmenitiesFilters);
        }

        mSort = gourmetCurationOption.getSortType();
        setSortType(mSort);

        GourmetSuggest gourmetSuggest = gourmetSearchCuration.getSuggest();

        if (gourmetSuggest == null || GourmetSuggest.CATEGORY_LOCATION.equalsIgnoreCase(gourmetSuggest.categoryKey) == true)
        {
            term = null;
        } else
        {
            term = gourmetSuggest.displayName;
        }

        if (Constants.SortType.DISTANCE == mSort || GourmetSuggest.CATEGORY_LOCATION.equalsIgnoreCase(gourmetSuggest.categoryKey) == true)
        {
            radius = gourmetSearchCuration.getRadius();

            Location location = gourmetSearchCuration.getLocation();
            if (location != null)
            {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        }
    }

    @Override
    public Map<String, Object> toParamsMap()
    {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("reserveDate", date);

        if (provinceIdx != 0)
        {
            hashMap.put("provinceIdx", provinceIdx);
        }

        if (areaIdx != 0)
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

        if (DailyTextUtils.isTextEmpty(term) == false)
        {
            hashMap.put("term", term);
        }

        boolean isNeedLocation = false;

        if (radius != 0d)
        {
            hashMap.put("radius", radius);

            isNeedLocation = true;
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

            if (Constants.SortType.DISTANCE == mSort)
            {
                isNeedLocation = true;
            }
        }

        if (hasLocation() == true && isNeedLocation == true)
        {
            hashMap.put("latitude", latitude);
            hashMap.put("longitude", longitude);
        }

        hashMap.put("details", details);

        return hashMap;
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