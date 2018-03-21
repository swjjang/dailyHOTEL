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
            hashMap.put("categoryAreaIdx", areaIdx);
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

    @Override
    public String toString()
    {
        return "StayCategoryParams{" + "dateCheckIn='" + dateCheckIn + '\'' + ", stays=" + stays //
            + ", category=" + category + ", mBedTypeList=" + mBedTypeList + ", mLuxuryList=" + mLuxuryList //
            + ", mRoomLuxuryList=" + mRoomLuxuryList + "provinceIdx=" + provinceIdx //
            + ", areaIdx=" + areaIdx + ", persons=" + persons + ", longitude=" + longitude //
            + ", latitude=" + latitude + ", page=" + page + ", limit=" + limit //
            + ", sortProperty='" + sortProperty + '\'' + ", sortDirection='" + sortDirection + '\''//
            + ", details=" + details + ", mSort=" + mSort + "}";
    }
}
