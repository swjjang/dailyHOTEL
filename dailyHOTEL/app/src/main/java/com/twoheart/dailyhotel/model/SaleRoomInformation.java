package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class SaleRoomInformation implements Parcelable
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
    public Stay.HotelGrade grade;
    public String address;
    //
    public String categoryCode; // GA를 위해서 payment로 진행시에 값을 넣는다

    public SaleRoomInformation(Parcel in)
    {
        readFromParcel(in);
    }

    public SaleRoomInformation(String hotelName, JSONObject jsonObject, boolean isOverseas, int nights) throws Exception
    {
        roomIndex = jsonObject.getInt("roomIdx");
        averageDiscount = jsonObject.getInt("discountAvg");
        price = jsonObject.getInt("price");
        totalDiscount = jsonObject.getInt("discountTotal");
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
        isOverseas = in.readInt() == 1 ? true : false;
        hotelName = in.readString();
        nights = in.readInt();

        try
        {
            grade = Stay.HotelGrade.valueOf(in.readString());
        } catch (Exception e)
        {
            grade = Stay.HotelGrade.etc;
        }

        address = in.readString();
        categoryCode = in.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public SaleRoomInformation createFromParcel(Parcel in)
        {
            return new SaleRoomInformation(in);
        }

        @Override
        public SaleRoomInformation[] newArray(int size)
        {
            return new SaleRoomInformation[size];
        }

    };
}
