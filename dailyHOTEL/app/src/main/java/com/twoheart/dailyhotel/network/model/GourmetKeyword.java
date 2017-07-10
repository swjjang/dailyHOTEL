package com.twoheart.dailyhotel.network.model;

import android.os.Parcel;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonIgnore;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.twoheart.dailyhotel.model.Keyword;

@JsonObject
public class GourmetKeyword extends Keyword
{
    @JsonField(name = "restaurantIdx")
    public int index;

    @JsonField(name = "availableTickets")
    public int availableTickets;

    @JsonField(name = "discount")
    public int price;

    public GourmetKeyword()
    {
    }

    public GourmetKeyword(int icon, String name)
    {
        super(icon, name);
    }

    public GourmetKeyword(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);

        dest.writeInt(index);
        dest.writeInt(availableTickets);
        dest.writeInt(price);
    }

    @Override
    protected void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

        index = in.readInt();
        availableTickets = in.readInt();
        price = in.readInt();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @JsonIgnore
    public static final Creator CREATOR = new Creator()
    {
        public GourmetKeyword createFromParcel(Parcel in)
        {
            return new GourmetKeyword(in);
        }

        @Override
        public GourmetKeyword[] newArray(int size)
        {
            return new GourmetKeyword[size];
        }

    };
}
