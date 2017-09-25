package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.entity.DetailImageInformation;

public class DetailImageInformationParcel implements Parcelable
{
    private DetailImageInformation mDetailImageInformation;

    public DetailImageInformationParcel(@NonNull DetailImageInformation detailImageInformation)
    {
        if (detailImageInformation == null)
        {
            throw new NullPointerException("detailImageInformation == null");
        }

        mDetailImageInformation = detailImageInformation;
    }

    public DetailImageInformationParcel(Parcel in)
    {
        readFromParcel(in);
    }

    public DetailImageInformation getGourmetMenuImage()
    {
        return mDetailImageInformation;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(mDetailImageInformation.caption);
        dest.writeString(mDetailImageInformation.url);
    }

    private void readFromParcel(Parcel in)
    {
        mDetailImageInformation = new DetailImageInformation();

        mDetailImageInformation.caption = in.readString();
        mDetailImageInformation.url = in.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public DetailImageInformationParcel createFromParcel(Parcel in)
        {
            return new DetailImageInformationParcel(in);
        }

        @Override
        public DetailImageInformationParcel[] newArray(int size)
        {
            return new DetailImageInformationParcel[size];
        }
    };
}
