package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class SaleRoomInformation implements Parcelable
{
	public int roomIndex;
	public String roomName;
	public String option;
	public String roomBenefit;
	public boolean isOverseas;
	public String hotelName;
	public int averageDiscount;
	public int totalDiscount;
	public int nights;

	public SaleRoomInformation(Parcel in)
	{
		readFromParcel(in);
	}

	public SaleRoomInformation(String hotelName, JSONObject jsonObject, boolean isOverseas, int nights) throws Exception
	{
		roomIndex = jsonObject.getInt("room_idx");
		averageDiscount = jsonObject.getInt("discount_avg");
		totalDiscount = jsonObject.getInt("discount_total");
		roomName = jsonObject.getString("room_name").trim();
		option = jsonObject.getString("option").trim();

		if (jsonObject.has("room_benefit") == true)
		{
			roomBenefit = jsonObject.getString("room_benefit").trim();
		}

		this.isOverseas = isOverseas;
		this.hotelName = hotelName;
		this.nights = nights;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeInt(roomIndex);
		dest.writeInt(averageDiscount);
		dest.writeInt(totalDiscount);
		dest.writeString(roomName);
		dest.writeString(option);
		dest.writeString(roomBenefit);
		dest.writeInt(isOverseas ? 1 : 0);
		dest.writeString(hotelName);
		dest.writeInt(nights);
	}

	protected void readFromParcel(Parcel in)
	{
		roomIndex = in.readInt();
		averageDiscount = in.readInt();
		totalDiscount = in.readInt();
		roomName = in.readString();
		option = in.readString();
		roomBenefit = in.readString();
		isOverseas = in.readInt() == 1 ? true : false;
		hotelName = in.readString();
		nights = in.readInt();
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
