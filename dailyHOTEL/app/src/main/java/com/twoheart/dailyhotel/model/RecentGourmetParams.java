package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.Util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by android_sam on 2016. 10. 25..
 */

public class RecentGourmetParams extends GourmetParams
{
    private String targetIndices;

    public RecentGourmetParams()
    {
        super(new GourmetCuration());
    }

    public RecentGourmetParams(Parcel in)
    {
        super(in);
    }

    public void setSaleTime(SaleTime saleTime)
    {
        if (saleTime == null)
        {
            return;
        }

        date = saleTime.getDayOfDaysDateFormat("yyyy-MM-dd");
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

        hashMap.put("reserveDate", date);

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
        public RecentGourmetParams createFromParcel(Parcel in)
        {
            return new RecentGourmetParams(in);
        }

        @Override
        public RecentGourmetParams[] newArray(int size)
        {
            return new RecentGourmetParams[size];
        }
    };
}
