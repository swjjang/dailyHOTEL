package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Guest implements Parcelable
{
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public Guest createFromParcel(Parcel in)
        {
            return new Guest(in);
        }

        @Override
        public Guest[] newArray(int size)
        {
            return new Guest[size];
        }

    };
    public String email;
    public String name;
    public String phone;

    public Guest()
    {
    }

    public Guest(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(email);
        dest.writeString(name);
        dest.writeString(phone);
    }

    private void readFromParcel(Parcel in)
    {
        email = in.readString();
        name = in.readString();
        phone = in.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }
}
