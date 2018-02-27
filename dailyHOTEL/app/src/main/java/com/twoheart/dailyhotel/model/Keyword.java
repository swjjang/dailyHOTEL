package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonIgnore;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class Keyword implements Parcelable
{
    public static final int DEFAULT_ICON = 0;
    public static final int HOTEL_ICON = 1;
    public static final int GOURMET_ICON = 2;

    @JsonField(name = "displayText")
    public String name;

    @JsonIgnore
    public int icon;

    public Keyword()
    {
    }

    public Keyword(int icon, String name)
    {
        this.icon = icon;
        this.name = name;
    }

    public Keyword(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(icon);
        dest.writeString(name);
    }

    protected void readFromParcel(Parcel in)
    {
        icon = in.readInt();
        name = in.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @JsonIgnore
    public static final Creator CREATOR = new Creator()
    {
        public Keyword createFromParcel(Parcel in)
        {
            return new Keyword(in);
        }

        @Override
        public Keyword[] newArray(int size)
        {
            return new Keyword[size];
        }

    };
}
