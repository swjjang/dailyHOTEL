package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Keyword implements Parcelable
{
    public int index;
    public String name;
    public String price;

    public Keyword(String text)
    {
        name = text;
    }

    public Keyword(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(index);
        dest.writeString(name);
        dest.writeString(price);
    }

    private void readFromParcel(Parcel in)
    {
        index = in.readInt();
        name = in.readString();
        price = in.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

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
