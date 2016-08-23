package com.twoheart.dailyhotel.model;

import android.os.Parcel;

public class GourmetCategory extends Category
{
    public String sequence;

    public GourmetCategory(String name, String code, String sequence)
    {
        super(name, code);
        this.sequence = sequence;
    }

    public GourmetCategory(Parcel in)
    {
        super(in);
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);
        dest.writeString(sequence);
    }

    @Override
    protected void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);
        sequence = in.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public GourmetCategory createFromParcel(Parcel in)
        {
            return new GourmetCategory(in);
        }

        @Override
        public GourmetCategory[] newArray(int size)
        {
            return new GourmetCategory[size];
        }

    };
}
