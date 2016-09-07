package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class RoomInformation implements Parcelable
{
    public int roomIndex;
    public String roomName;
    public String option;
    public String amenities;
    public String roomBenefit;
    public boolean isOverseas;
    public String hotelName;
    public int price;
    public int averageDiscount;
    public int totalDiscount;
    public int nights;
    public Stay.Grade grade;
    public String address;
    public boolean isNRD;
    //
    public String categoryCode; // GA를 위해서 payment로 진행시에 값을 넣는다

    public RoomInformation(Parcel in)
    {
        readFromParcel(in);
    }

    public RoomInformation(String hotelName, JSONObject jsonObject, boolean isOverseas, int nights) throws Exception
    {
        roomIndex = jsonObject.getInt("roomIdx");
        averageDiscount = jsonObject.getInt("discountAverage");
        totalDiscount = jsonObject.getInt("discountTotal");
        price = jsonObject.getInt("price");
        roomName = jsonObject.getString("roomName").trim();
        option = jsonObject.getString("description1").trim();
        amenities = jsonObject.getString("description2").trim();

        if (jsonObject.has("roomBenefit") == true)
        {
            roomBenefit = jsonObject.getString("roomBenefit").trim();
        }

        this.isOverseas = isOverseas;
        this.hotelName = hotelName;
        this.nights = nights;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(roomIndex);
        dest.writeInt(price);
        dest.writeInt(averageDiscount);
        dest.writeInt(totalDiscount);
        dest.writeString(roomName);
        dest.writeString(option);
        dest.writeString(amenities);
        dest.writeString(roomBenefit);
        dest.writeInt(isOverseas ? 1 : 0);
        dest.writeString(hotelName);
        dest.writeInt(nights);
        dest.writeString(grade.name());
        dest.writeString(address);
        dest.writeString(categoryCode);
        dest.writeInt(isNRD ? 1 : 0);
    }

    protected void readFromParcel(Parcel in)
    {
        roomIndex = in.readInt();
        price = in.readInt();
        averageDiscount = in.readInt();
        totalDiscount = in.readInt();
        roomName = in.readString();
        option = in.readString();
        amenities = in.readString();
        roomBenefit = in.readString();
        isOverseas = in.readInt() == 1;
        hotelName = in.readString();
        nights = in.readInt();

        try
        {
            grade = Stay.Grade.valueOf(in.readString());
        } catch (Exception e)
        {
            grade = Stay.Grade.etc;
        }

        address = in.readString();
        categoryCode = in.readString();
        isNRD = in.readInt() == 1;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public RoomInformation createFromParcel(Parcel in)
        {
            return new RoomInformation(in);
        }

        @Override
        public RoomInformation[] newArray(int size)
        {
            return new RoomInformation[size];
        }
    };
}
