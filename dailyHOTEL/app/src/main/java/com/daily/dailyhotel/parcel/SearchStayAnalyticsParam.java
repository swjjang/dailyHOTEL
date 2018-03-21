package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;

public class SearchStayAnalyticsParam implements Parcelable
{
    public String mCallByScreen;

    public SearchStayAnalyticsParam()
    {
    }

    public SearchStayAnalyticsParam(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(mCallByScreen);
    }

    private void readFromParcel(Parcel in)
    {
        mCallByScreen = in.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public SearchStayAnalyticsParam createFromParcel(Parcel in)
        {
            return new SearchStayAnalyticsParam(in);
        }

        @Override
        public SearchStayAnalyticsParam[] newArray(int size)
        {
            return new SearchStayAnalyticsParam[size];
        }

    };
}
