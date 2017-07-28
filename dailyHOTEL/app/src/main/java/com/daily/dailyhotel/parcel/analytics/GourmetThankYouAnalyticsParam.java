package com.daily.dailyhotel.parcel.analytics;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class GourmetThankYouAnalyticsParam implements Parcelable
{
    public Map<String, String> params;

    public GourmetThankYouAnalyticsParam()
    {
    }

    public GourmetThankYouAnalyticsParam(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeMap(params);
    }

    void readFromParcel(Parcel in)
    {
        params = in.readHashMap(HashMap.class.getClassLoader());
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public GourmetThankYouAnalyticsParam createFromParcel(Parcel in)
        {
            return new GourmetThankYouAnalyticsParam(in);
        }

        @Override
        public GourmetThankYouAnalyticsParam[] newArray(int size)
        {
            return new GourmetThankYouAnalyticsParam[size];
        }
    };
}
