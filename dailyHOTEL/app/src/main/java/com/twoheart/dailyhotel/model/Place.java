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
    public int entryPosition;
    public boolean truevr;
    public String stickerUrl;
    public int stickerIndex;

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
        dest.writeInt(index);
        dest.writeString(imageUrl);
        dest.writeString(name);
        dest.writeInt(price);
        dest.writeInt(discountPrice);
        dest.writeString(addressSummary);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeInt(isDailyChoice ? 1 : 0);
        dest.writeInt(isSoldOut ? 1 : 0);
        dest.writeInt(satisfaction);
        dest.writeString(districtName);
        dest.writeInt(entryPosition);
        dest.writeInt(truevr ? 1 : 0);
        dest.writeString(stickerUrl);
        dest.writeInt(stickerIndex);
    }

    protected void readFromParcel(Parcel in)
    {
        index = in.readInt();
        imageUrl = in.readString();
        name = in.readString();
        price = in.readInt();
        discountPrice = in.readInt();
        addressSummary = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        isDailyChoice = in.readInt() == 1;
        isSoldOut = in.readInt() == 1;
        satisfaction = in.readInt();
        districtName = in.readString();
        entryPosition = in.readInt();
        truevr = in.readInt() == 1;
        stickerUrl = in.readString();
        stickerIndex = in.readInt();
    }
}
