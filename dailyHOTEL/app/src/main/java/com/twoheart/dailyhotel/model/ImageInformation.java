package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageInformation implements Parcelable
{
    public String url;
    public String description;

    public ImageInformation(String url, String description)
    {
        this.url = url;
        this.description = description;
    }

    public ImageInformation(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(url);
        dest.writeString(description);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    protected void readFromParcel(Parcel in)
    {
        url = in.readString();
        description = in.readString();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public ImageInformation createFromParcel(Parcel in)
        {
            return new ImageInformation(in);
        }

        @Override
        public ImageInformation[] newArray(int size)
        {
            return new ImageInformation[size];
        }

    };
}
