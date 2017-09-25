package com.daily.dailyhotel.parcel.analytics;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by android_sam on 2017. 6. 23..
 */

public class TrueReviewAnalyticsParam implements Parcelable
{
    public String category;

    public TrueReviewAnalyticsParam()
    {
        super();
    }

    public TrueReviewAnalyticsParam(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(category);
    }

    protected void readFromParcel(Parcel in)
    {
        category = in.readString();
    }

    public static final Creator CREATOR = new Creator()
    {
        @Override
        public TrueReviewAnalyticsParam createFromParcel(Parcel source)
        {
            return new TrueReviewAnalyticsParam(source);
        }

        @Override
        public TrueReviewAnalyticsParam[] newArray(int size)
        {
            return new TrueReviewAnalyticsParam[size];
        }
    };
}
