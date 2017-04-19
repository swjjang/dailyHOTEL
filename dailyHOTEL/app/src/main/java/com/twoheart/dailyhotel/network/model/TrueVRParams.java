package com.twoheart.dailyhotel.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by android_sam on 2017. 2. 15..
 */
@JsonObject
public class TrueVRParams implements Parcelable
{
    @JsonField(name = "name")
    public String name;

    @JsonField(name = "url")
    public String url;

    public TrueVRParams()
    {
    }

    public TrueVRParams(String name, String url)
    {
        this.name = name;
        this.url = url;
    }

    public TrueVRParams(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(name);
        dest.writeString(url);
    }

    protected void readFromParcel(Parcel in)
    {
        name = in.readString();
        url = in.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public TrueVRParams createFromParcel(Parcel in)
        {
            return new TrueVRParams(in);
        }

        @Override
        public TrueVRParams[] newArray(int size)
        {
            return new TrueVRParams[size];
        }
    };
}
