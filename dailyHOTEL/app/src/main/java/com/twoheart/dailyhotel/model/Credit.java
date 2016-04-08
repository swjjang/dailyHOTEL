package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Credit implements Parcelable
{
    private String content;
    private int bonus;
    private String expires;

    public Credit(Parcel in)
    {
        readFromParcel(in);
    }

    public Credit(String content, int bonus, String expires)
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

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public int getBonus()
    {
        return bonus;
    }

    public void setBonus(int bonus)
    {
        this.bonus = bonus;
    }

    public String getExpires()
    {
        return expires;
    }

    public void setExpires(String expires)
    {
        this.expires = expires;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public Credit createFromParcel(Parcel in)
        {
            return new Credit(in);
        }

        @Override
        public Credit[] newArray(int size)
        {
            return new Credit[size];
        }

    };
}
