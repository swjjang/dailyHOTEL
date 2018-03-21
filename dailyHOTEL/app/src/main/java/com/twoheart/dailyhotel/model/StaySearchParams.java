package com.twoheart.dailyhotel.model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.StaySuggestV2;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.util.Constants;

import java.util.HashMap;
import java.util.Map;

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

        StayBookingDay stayBookingDay = staySearchCuration.getStayBookingDay();

        if (stayBookingDay != null)
        {
            dateCheckIn = stayBookingDay.getCheckInDay("yyyy-MM-dd");

            try
            {
                stays = stayBookingDay.getNights();
            } catch (Exception e)
            {
                ExLog.e(e.toString());
            }
        }

        category = staySearchCuration.getCategory();

        StayCurationOption stayCurationOption = (StayCurationOption) staySearchCuration.getCurationOption();

        if (stayCurationOption != null)
        {
            persons = stayCurationOption.person;

            mBedTypeList = toParamListByBedTypes(stayCurationOption.flagBedTypeFilters);
            mLuxuryList = toParamListByAmenities(stayCurationOption.flagAmenitiesFilters);
            mRoomLuxuryList = toParamListByRoomAmenities(stayCurationOption.flagRoomAmenitiesFilters);
            mSort = stayCurationOption.getSortType();
        }

        setSortType(mSort);

        StaySuggestV2 suggest = staySearchCuration.getSuggest();

        if (suggest != null)
        {
            if (suggest.isLocationSuggestType() == true)
            {
                term = null;
            } else
            {
                term = suggest.getSuggestItem().name;
            }

            if (Constants.SortType.DISTANCE == mSort || suggest.isLocationSuggestType() == true)
            {
                radius = staySearchCuration.getRadius();

                Location location = staySearchCuration.getLocation();
                if (location != null)
                {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }
        }
    }

    @Override
    public Map<String, Object> toParamsMap()
    {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("dateCheckIn", dateCheckIn);
        hashMap.put("stays", stays);

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

        if (category != null && Category.ALL.code.equalsIgnoreCase(category.code) == false)
        {
            hashMap.put("category", category.code);
        }

        //        if(mBedTypeList != null && mBedTypeList.size() > 0)
        //        {
        //            hashMap.put("bedType", mBedTypeList);
        //        }
        //
        //        if(mLuxuryList != null && mLuxuryList.size() > 0)
        //        {
        //            hashMap.put("luxury", mLuxuryList);
        //        }

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

        //        ExLog.d("params : " + hashMap.toString());
        return hashMap;
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
