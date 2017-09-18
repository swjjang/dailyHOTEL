package com.daily.dailyhotel.parcel.analytics;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by android_sam on 2017. 6. 23..
 */

public class GourmetTrueReviewAnalyticsParam implements Parcelable
{
    public String category;

    public GourmetTrueReviewAnalyticsParam()
    {
        super();
    }

    public GourmetTrueReviewAnalyticsParam(Parcel in)
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
        public GourmetTrueReviewAnalyticsParam createFromParcel(Parcel source)
        {
            return new GourmetTrueReviewAnalyticsParam(source);
        }

        @Override
        public GourmetTrueReviewAnalyticsParam[] newArray(int size)
        {
            return new GourmetTrueReviewAnalyticsParam[size];
        }
    };
}
