package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class Place implements Parcelable
{
    public int index;
    public String imageUrl;
    public String name;
    public int price;
    public int discountPrice;
    public String addressSummary;
    public double latitude;
    public double longitude;
    public boolean isDailyChoice;
    public boolean isSoldOut;
    public int satisfaction;
    public String districtName;
    public int entryIndex;

    public Place()
    {
        super();
    }

    public Place(Parcel in)
    {
        readFromParcel(in);
    }

    public abstract int getGradeMarkerResId();

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(imageUrl);
        dest.writeString(name);
        dest.writeInt(price);
        dest.writeInt(discountPrice);
        dest.writeString(addressSummary);
        dest.writeInt(index);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeInt(isDailyChoice ? 1 : 0);
        dest.writeInt(isSoldOut ? 1 : 0);
        dest.writeInt(satisfaction);
        dest.writeString(districtName);
        dest.writeInt(entryIndex);
    }

    protected void readFromParcel(Parcel in)
    {
        imageUrl = in.readString();
        name = in.readString();
        price = in.readInt();
        discountPrice = in.readInt();
        addressSummary = in.readString();
        index = in.readInt();
        latitude = in.readDouble();
        longitude = in.readDouble();
        isDailyChoice = in.readInt() == 1 ? true : false;
        isSoldOut = in.readInt() == 1 ? true : false;
        satisfaction = in.readInt();
        districtName = in.readString();
        entryIndex = in.readInt();
    }
}
