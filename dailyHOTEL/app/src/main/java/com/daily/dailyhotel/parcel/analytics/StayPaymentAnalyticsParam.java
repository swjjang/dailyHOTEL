package com.daily.dailyhotel.parcel.analytics;

import android.os.Parcel;
import android.os.Parcelable;

public class StayPaymentAnalyticsParam implements Parcelable
{
    public boolean nrd;
    public boolean showOriginalPrice;
    public String grade;
    public int rankingPosition;
    public String rating;
    public boolean benefit;


    public StayPaymentAnalyticsParam()
    {
    }

    public StayPaymentAnalyticsParam(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(nrd == true ? 1 : 0);
        dest.writeInt(showOriginalPrice == true ? 1 : 0);
        dest.writeString(grade);
        dest.writeInt(rankingPosition);
        dest.writeString(rating);
    }

    void readFromParcel(Parcel in)
    {
        nrd = in.readInt() == 1 ? true : false;
        showOriginalPrice = in.readInt() == 1 ? true : false;
        grade = in.readString();
        rankingPosition = in.readInt();
        rating = in.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public StayPaymentAnalyticsParam createFromParcel(Parcel in)
        {
            return new StayPaymentAnalyticsParam(in);
        }

        @Override
        public StayPaymentAnalyticsParam[] newArray(int size)
        {
            return new StayPaymentAnalyticsParam[size];
        }
    };
}
