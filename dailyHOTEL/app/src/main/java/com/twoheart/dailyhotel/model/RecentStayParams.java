package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.Util;

import java.net.URLEncoder;

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
        return toParamsString(true);
    }

    public String toParamsString(boolean isTargetIndicesEncode)
    {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(getParamString("dateCheckIn", dateCheckIn)).append("&");
        stringBuilder.append(getParamString("stays", stays)).append("&");

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
        if (length > 0)
        {
            stringBuilder.setLength(length - 1);
        }

        //        ExLog.d(" params : " + stringBuilder.toString());
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
