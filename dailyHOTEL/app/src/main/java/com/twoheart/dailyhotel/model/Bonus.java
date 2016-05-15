package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Bonus implements Parcelable
{
    public String content;
    public int bonus;
    public String expires;

    public Bonus(Parcel in)
    {
        readFromParcel(in);
    }

    public Bonus(String content, int bonus, String expires)
    {
        this.content = content;
        this.bonus = bonus;
        this.expires = expires;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(content);
        dest.writeInt(bonus);
        dest.writeString(expires);
    }

    private void readFromParcel(Parcel in)
    {
        content = in.readString();
        bonus = in.readInt();
        expires = in.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public Bonus createFromParcel(Parcel in)
        {
            return new Bonus(in);
        }

        @Override
        public Bonus[] newArray(int size)
        {
            return new Bonus[size];
        }
    };
}
