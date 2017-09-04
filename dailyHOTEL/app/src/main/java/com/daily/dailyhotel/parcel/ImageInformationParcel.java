package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.entity.ImageInformation;

public class ImageInformationParcel implements Parcelable
{
    private ImageInformation mImageInformation;

    public ImageInformationParcel(@NonNull ImageInformation imageInformation)
    {
        if (imageInformation == null)
        {
            throw new NullPointerException("reservation == null");
        }

        mImageInformation = imageInformation;
    }

    public ImageInformationParcel(Parcel in)
    {
        readFromParcel(in);
    }

    public ImageInformation getGourmetMenuImage()
    {
        return mImageInformation;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(mImageInformation.caption);
        dest.writeString(mImageInformation.url);
    }

    private void readFromParcel(Parcel in)
    {
        mImageInformation = new ImageInformation();

        mImageInformation.caption = in.readString();
        mImageInformation.url = in.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public ImageInformationParcel createFromParcel(Parcel in)
        {
            return new ImageInformationParcel(in);
        }

        @Override
        public ImageInformationParcel[] newArray(int size)
        {
            return new ImageInformationParcel[size];
        }
    };
}
