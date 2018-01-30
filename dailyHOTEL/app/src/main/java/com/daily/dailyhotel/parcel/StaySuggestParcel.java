package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.entity.StaySuggest;

/**
 * Created by android_sam on 2018. 1. 26..
 */

public class StaySuggestParcel implements Parcelable
{
    private StaySuggest mStaySuggest;

    public StaySuggestParcel(@NonNull StaySuggest staySuggest)
    {
        if (staySuggest == null)
        {
            throw new NullPointerException("staySuggest == null");
        }

        mStaySuggest = staySuggest;
    }

    public StaySuggestParcel(Parcel in)
    {
        readFromParcel(in);
    }

    public StaySuggest getSuggest()
    {
        return mStaySuggest;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(mStaySuggest.stayIndex);
        dest.writeString(mStaySuggest.stayName);
        dest.writeString(mStaySuggest.regionName);
        dest.writeString(mStaySuggest.provinceName);
        dest.writeString(mStaySuggest.displayName);
        dest.writeInt(mStaySuggest.discountAveragePrice);
        dest.writeInt(mStaySuggest.availableRooms);
        dest.writeDouble(mStaySuggest.latitude);
        dest.writeDouble(mStaySuggest.longitude);
        dest.writeString(mStaySuggest.categoryKey);
        dest.writeInt(mStaySuggest.menuType);
    }

    private void readFromParcel(Parcel in)
    {
        mStaySuggest = new StaySuggest();

        mStaySuggest.stayIndex = in.readInt();
        mStaySuggest.stayName = in.readString();
        mStaySuggest.regionName = in.readString();
        mStaySuggest.provinceName = in.readString();
        mStaySuggest.displayName = in.readString();
        mStaySuggest.discountAveragePrice = in.readInt();
        mStaySuggest.availableRooms = in.readInt();
        mStaySuggest.latitude = in.readDouble();
        mStaySuggest.longitude = in.readDouble();
        mStaySuggest.categoryKey = in.readString();
        mStaySuggest.menuType = in.readInt();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public StaySuggestParcel createFromParcel(Parcel in)
        {
            return new StaySuggestParcel(in);
        }

        @Override
        public StaySuggestParcel[] newArray(int size)
        {
            return new StaySuggestParcel[size];
        }

    };
}
