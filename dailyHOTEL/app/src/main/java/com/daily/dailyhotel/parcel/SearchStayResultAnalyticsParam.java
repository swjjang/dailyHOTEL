package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;

public class SearchStayResultAnalyticsParam implements Parcelable
{
    public String mCallByScreen;

    public SearchStayResultAnalyticsParam()
    {
    }

    public SearchStayResultAnalyticsParam(Parcel in)
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
        public SearchStayResultAnalyticsParam createFromParcel(Parcel in)
        {
            return new SearchStayResultAnalyticsParam(in);
        }

        @Override
        public SearchStayResultAnalyticsParam[] newArray(int size)
        {
            return new SearchStayResultAnalyticsParam[size];
        }

    };
}
