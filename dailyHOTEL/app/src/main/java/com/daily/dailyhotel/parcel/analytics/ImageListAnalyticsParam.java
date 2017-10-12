package com.daily.dailyhotel.parcel.analytics;

import android.os.Parcel;
import android.os.Parcelable;

import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.util.Constants;

/**
 * Created by android_sam on 2017. 6. 23..
 */

public class ImageListAnalyticsParam implements Parcelable
{
    public Constants.ServiceType serviceType;

    public ImageListAnalyticsParam()
    {
        super();
    }

    public ImageListAnalyticsParam(Parcel in)
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
        if (serviceType != null)
        {
            dest.writeString(serviceType.name());
        }
    }

    protected void readFromParcel(Parcel in)
    {
        String serviceTypeName = in.readString();

        if (DailyTextUtils.isTextEmpty(serviceTypeName) == false)
        {
            serviceType = Constants.ServiceType.valueOf(serviceTypeName);
        }
    }

    public static final Creator CREATOR = new Creator()
    {
        @Override
        public ImageListAnalyticsParam createFromParcel(Parcel source)
        {
            return new ImageListAnalyticsParam(source);
        }

        @Override
        public ImageListAnalyticsParam[] newArray(int size)
        {
            return new ImageListAnalyticsParam[size];
        }
    };
}
