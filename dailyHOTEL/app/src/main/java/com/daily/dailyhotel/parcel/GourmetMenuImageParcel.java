package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.entity.GourmetMenuImage;

public class GourmetMenuImageParcel implements Parcelable
{
    private GourmetMenuImage mGourmetMenuImage;

    public GourmetMenuImageParcel(@NonNull GourmetMenuImage gourmetMenuImage)
    {
        if (gourmetMenuImage == null)
        {
            throw new NullPointerException("reservation == null");
        }

        mGourmetMenuImage = gourmetMenuImage;
    }

    public GourmetMenuImageParcel(Parcel in)
    {
        readFromParcel(in);
    }

    public GourmetMenuImage getGourmetMenuImage()
    {
        return mGourmetMenuImage;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(mGourmetMenuImage.caption);
        dest.writeString(mGourmetMenuImage.url);
    }

    private void readFromParcel(Parcel in)
    {
        mGourmetMenuImage = new GourmetMenuImage();

        mGourmetMenuImage.caption = in.readString();
        mGourmetMenuImage.url = in.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public GourmetMenuImageParcel createFromParcel(Parcel in)
        {
            return new GourmetMenuImageParcel(in);
        }

        @Override
        public GourmetMenuImageParcel[] newArray(int size)
        {
            return new GourmetMenuImageParcel[size];
        }
    };
}
