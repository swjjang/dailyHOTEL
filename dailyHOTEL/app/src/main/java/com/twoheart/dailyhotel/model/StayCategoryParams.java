package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.util.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by android_sam on 2017. 5. 18..
 */

public class StayCategoryParams extends StayParams
{
    public StayCategoryParams(PlaceCuration placeCuration)
    {
        super(placeCuration);
    }

    public StayCategoryParams(Parcel in)
    {
        super(in);
    }

    public String getCategoryCode()
    {
        if (category == null || Category.ALL.code.equalsIgnoreCase(category.code) == true)
        {
            return null;
        }

        return category.code;
    }

//    @Override
//    public void setPlaceParams(PlaceCuration placeCuration)
//    {
//        if (placeCuration == null)
//        {
//            return;
//        }
//
//        StayCategoryCuration stayCategoryCuration = (StayCategoryCuration) placeCuration;
//
//        clear();
//
//        StayBookingDay stayBookingDay = stayCategoryCuration.getStayBookingDay();
//
//        if (stayBookingDay != null)
//        {
//            dateCheckIn = stayBookingDay.getCheckInDay("yyyy-MM-dd");
//
//            try
//            {
//                stays = stayBookingDay.getNights();
//            } catch (Exception e)
//            {
//                ExLog.e(e.toString());
//            }
//        }
//
//        Province province = stayCategoryCuration.getProvince();
//
//        if (province != null)
//        {
//            provinceIdx = province.getProvinceIndex();
//
//            if (province instanceof Area)
//            {
//                areaIdx = ((Area) province).index;
//            }
//        }
//
//        category = stayCategoryCuration.getCategory();
//
//        StayCurationOption stayCurationOption = (StayCurationOption) stayCategoryCuration.getCurationOption();
//
//        if (stayCurationOption != null)
//        {
//            persons = stayCurationOption.person;
//
//            mBedTypeList = toParamListByBedTypes(stayCurationOption.flagBedTypeFilters);
//            mLuxuryList = toParamListByAmenities(stayCurationOption.flagAmenitiesFilters);
//            mRoomLuxuryList = toParamListByRoomAmenities(stayCurationOption.flagRoomAmenitiesFilters);
//        }
//
//        mSort = stayCurationOption.getSortType();
//        setSortType(mSort);
//
//        Location location = stayCategoryCuration.getLocation();
//        if (location != null)
//        {
//            latitude = location.getLatitude();
//            longitude = location.getLongitude();
//        }
//    }

    @Override
    public Map<String, Object> toParamsMap()
    {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("dateCheckIn", dateCheckIn);
        hashMap.put("stays", stays);
        hashMap.put("provinceIdx", provinceIdx);

        if (areaIdx != 0)
        {
            hashMap.put("areaIdx", areaIdx);
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

        //        ExLog.d("params : " + hashMap.toString());
        return hashMap;
    }

    public Map<String, Object> toLocalPlusParamsMap()
    {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("dateCheckIn", dateCheckIn);
        hashMap.put("stays", stays);
        hashMap.put("provinceIdx", provinceIdx);
        hashMap.put("category", getCategoryCode());

        if (areaIdx != 0)
        {
            hashMap.put("areaIdx", areaIdx);
        }

        return hashMap;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public StayCategoryParams createFromParcel(Parcel in)
        {
            return new StayCategoryParams(in);
        }

        @Override
        public StayCategoryParams[] newArray(int size)
        {
            return new StayCategoryParams[size];
        }
    };
}
