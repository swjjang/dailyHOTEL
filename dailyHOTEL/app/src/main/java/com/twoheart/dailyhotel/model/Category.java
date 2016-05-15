package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Category implements Parcelable
{
    public static final Category ALL = new Category("전체", "all");

    public String name;
    public String code;

    public Category(String name, String code)
    {
        this.name = name;
        this.code = code;
    }

    public Category(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(name);
        dest.writeString(code);
    }

    protected void readFromParcel(Parcel in)
    {
        name = in.readString();
        code = in.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public Category createFromParcel(Parcel in)
        {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size)
        {
            return new Category[size];
        }

    };
}
