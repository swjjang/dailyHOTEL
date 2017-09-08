package com.daily.dailyhotel.parcel.analytics;

import android.os.Parcel;
import android.os.Parcelable;

public class NavigatorAnalyticsParam implements Parcelable
{
    public String category;
    public String action;
    public String label;

    public NavigatorAnalyticsParam()
    {
    }

    public NavigatorAnalyticsParam(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(category);
        dest.writeString(action);
        dest.writeString(label);
    }

    void readFromParcel(Parcel in)
    {
        category = in.readString();
        action = in.readString();
        label = in.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public NavigatorAnalyticsParam createFromParcel(Parcel in)
        {
            return new NavigatorAnalyticsParam(in);
        }

        @Override
        public NavigatorAnalyticsParam[] newArray(int size)
        {
            return new NavigatorAnalyticsParam[size];
        }
    };
}
