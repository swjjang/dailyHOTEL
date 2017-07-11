package com.daily.dailyhotel.parcel.analytics;

import android.os.Parcel;
import android.os.Parcelable;

public class StayOutboundListAnalyticsParam implements Parcelable
{
    public String keyword;

    public StayOutboundListAnalyticsParam()
    {
    }

    public StayOutboundListAnalyticsParam(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(keyword);
    }

    void readFromParcel(Parcel in)
    {
        keyword = in.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public StayOutboundListAnalyticsParam createFromParcel(Parcel in)
        {
            return new StayOutboundListAnalyticsParam(in);
        }

        @Override
        public StayOutboundListAnalyticsParam[] newArray(int size)
        {
            return new StayOutboundListAnalyticsParam[size];
        }
    };
}
