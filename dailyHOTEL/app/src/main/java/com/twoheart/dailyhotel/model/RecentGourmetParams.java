package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import java.net.URLEncoder;

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
        return toParamsString(true);
    }

    public String toParamsString(boolean isTargetIndicesEncode)
    {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(getParamString("reserveDate", date)).append("&");

        //        if (page > 0)
        //        {
        //            stringBuilder.append(getParamString("page", page)).append("&");
        //            stringBuilder.append(getParamString("limit", limit)).append("&");
        //        }
        //
        if (Util.isTextEmpty(targetIndices) == false)
        {
            stringBuilder.append(getParamString("targetIndices", isTargetIndicesEncode == true ? URLEncoder.encode(targetIndices) : targetIndices)).append("&");
        }

        int length = stringBuilder.length();
        if (stringBuilder.charAt(length - 1) == '&')
        {
            stringBuilder.setLength(length - 1);
        }

        ExLog.d(" params : " + stringBuilder.toString());
        return stringBuilder.toString();
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
