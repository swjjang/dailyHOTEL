package com.daily.dailyhotel.parcel.analytics;

import android.os.Parcel;
import android.os.Parcelable;

public class StayOutboundPaymentAnalyticsParam implements Parcelable
{
    public boolean nrd;
    public boolean showOriginalPrice;
    public String grade;

    public StayOutboundPaymentAnalyticsParam()
    {
    }

    public StayOutboundPaymentAnalyticsParam(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(nrd == true ? 1 : 0);
        dest.writeInt(showOriginalPrice == true ? 1 : 0);
        dest.writeString(grade);
    }

    void readFromParcel(Parcel in)
    {
        nrd = in.readInt() == 1 ? true : false;
        showOriginalPrice = in.readInt() == 1 ? true : false;
        grade = in.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public StayOutboundPaymentAnalyticsParam createFromParcel(Parcel in)
        {
            return new StayOutboundPaymentAnalyticsParam(in);
        }

        @Override
        public StayOutboundPaymentAnalyticsParam[] newArray(int size)
        {
            return new StayOutboundPaymentAnalyticsParam[size];
        }
    };
}
