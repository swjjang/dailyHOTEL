package com.twoheart.dailyhotel.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class ImageInformation implements Parcelable
{
    @JsonField
    public String description;

    @JsonField
    public String name;

    private String mUrl;

    public ImageInformation()
    {
    }

    public ImageInformation(Parcel in)
    {
        readFromParcel(in);
    }

    public void setImageUrl(String url)
    {
        mUrl = url;
    }

    public String getImageUrl()
    {
        return mUrl;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(description);
        dest.writeString(name);
        dest.writeString(mUrl);
    }

    protected void readFromParcel(Parcel in)
    {
        description = in.readString();
        name = in.readString();
        mUrl = in.readString();
    }

    public static final Creator CREATOR = new Creator()
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
