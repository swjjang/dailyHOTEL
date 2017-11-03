package com.daily.dailyhotel.parcel.analytics;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class StayThankYouAnalyticsParam implements Parcelable
{
    public Map<String, String> params;
    public boolean provideRewardSticker;

    public StayThankYouAnalyticsParam()
    {
    }

    public StayThankYouAnalyticsParam(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeMap(params);
        dest.writeInt(provideRewardSticker == true ? 1 : 0);
    }

    void readFromParcel(Parcel in)
    {
        params = in.readHashMap(HashMap.class.getClassLoader());
        provideRewardSticker = in.readInt() == 1 ? true : false;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public StayThankYouAnalyticsParam createFromParcel(Parcel in)
        {
            return new StayThankYouAnalyticsParam(in);
        }

        @Override
        public StayThankYouAnalyticsParam[] newArray(int size)
        {
            return new StayThankYouAnalyticsParam[size];
        }
    };
}
