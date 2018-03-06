package com.twoheart.dailyhotel.model;

import android.location.Location;
import android.os.Parcel;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.util.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by android_sam on 2016. 7. 28..
 */
public class StayCategoryNearByParams extends StayCategoryParams
{
    private String term;
    private double radius;

    public StayCategoryNearByParams(PlaceCuration placeCuration)
    {
        super(placeCuration);
    }

    public StayCategoryNearByParams(Parcel in)
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

        StayCategoryNearByCuration stayCategoryNearByCuration = (StayCategoryNearByCuration) placeCuration;

        clear();

        StayBookingDay stayBookingDay = stayCategoryNearByCuration.getStayBookingDay();

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

        category = stayCategoryNearByCuration.getCategory();

        StayCurationOption stayCurationOption = (StayCurationOption) stayCategoryNearByCuration.getCurationOption();

        if (stayCurationOption != null)
        {
            persons = stayCurationOption.person;

            mBedTypeList = toParamListByBedTypes(stayCurationOption.flagBedTypeFilters);
            mLuxuryList = toParamListByAmenities(stayCurationOption.flagAmenitiesFilters);
            mRoomLuxuryList = toParamListByRoomAmenities(stayCurationOption.flagRoomAmenitiesFilters);
            mSort = stayCurationOption.getSortType();
        }

        setSortType(mSort);

        term = null;
        radius = stayCategoryNearByCuration.getRadius();

        Location location = stayCategoryNearByCuration.getLocation();
        if (location != null)
        {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
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

    public static final Creator CREATOR = new Creator()
    {
        public StayCategoryNearByParams createFromParcel(Parcel in)
        {
            return new StayCategoryNearByParams(in);
        }

        @Override
        public StayCategoryNearByParams[] newArray(int size)
        {
            return new StayCategoryNearByParams[size];
        }
    };
}
