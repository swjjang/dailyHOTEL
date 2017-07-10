package com.twoheart.dailyhotel.network.model;

import android.os.Parcel;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonIgnore;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.twoheart.dailyhotel.model.Keyword;

@JsonObject
public class StayKeyword extends Keyword
{
    @JsonField(name = "hotelIdx")
    public int index;

    @JsonField(name = "availableRooms")
    public int availableRooms;

    @JsonField(name = "discountAvg")
    public int price;

    public StayKeyword()
    {
    }

    public StayKeyword(int icon, String name)
    {
        super(icon, name);
    }

    public StayKeyword(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);

        dest.writeInt(index);
        dest.writeInt(availableRooms);
        dest.writeInt(price);
    }

    @Override
    protected void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

        index = in.readInt();
        availableRooms = in.readInt();
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
        public StayKeyword createFromParcel(Parcel in)
        {
            return new StayKeyword(in);
        }

        @Override
        public StayKeyword[] newArray(int size)
        {
            return new StayKeyword[size];
        }

    };
}
