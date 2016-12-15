package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.Util;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by android_sam on 2016. 10. 25..
 */

public class RecentStayParams extends StayParams
{
    private String targetIndices;

    public RecentStayParams()
    {
        super(new StayCuration());
    }

    public RecentStayParams(Parcel in)
    {
        super(in);
    }

    public void setCheckInTime(SaleTime checkInTime)
    {
        if (checkInTime == null)
        {
            return;
        }

        dateCheckIn = checkInTime.getDayOfDaysDateFormat("yyyy-MM-dd");
        stays = 1;
    }

    public void setTargetIndices(String targetIndices)
    {
        this.targetIndices = targetIndices;
    }

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

        return stringBuilder.toString();
    }

    @Override
    public Map<String, Object> toParamsMap()
    {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("dateCheckIn", dateCheckIn);
        hashMap.put("stays", stays);

        if (Util.isTextEmpty(targetIndices) == false)
        {
            hashMap.put("targetIndices", targetIndices);
        }

        return hashMap;
    }

    @Override
    protected void clear()
    {
        super.clear();

        targetIndices = null;
    }

    protected void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

        targetIndices = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);

        dest.writeString(targetIndices);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public RecentStayParams createFromParcel(Parcel in)
        {
            return new RecentStayParams(in);
        }

        @Override
        public RecentStayParams[] newArray(int size)
        {
            return new RecentStayParams[size];
        }
    };
}
