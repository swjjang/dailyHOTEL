package com.twoheart.dailyhotel.model;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class SaleRoomInformation implements Parcelable
{
	public int saleIndex;
	public int discount;
	public String roomName;
	public String option;
	public String roomBenefit;
	public int availableRooms;

	public SaleRoomInformation(Parcel in)
	{
		readFromParcel(in);
	}

	public SaleRoomInformation(JSONObject jsonObject) throws Exception
	{
		saleIndex = jsonObject.getInt("sale_idx");
		discount = jsonObject.getInt("discount");
		roomName = jsonObject.getString("room_name");
		option = jsonObject.getString("option");

		if (jsonObject.has("room_benefit") == true)
		{
			roomBenefit = jsonObject.getString("room_benefit");
		}

		availableRooms = jsonObject.getInt("available_rooms");
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeInt(saleIndex);
		dest.writeInt(discount);
		dest.writeString(roomName);
		dest.writeString(option);
		dest.writeString(roomBenefit);
		dest.writeInt(availableRooms);
	}

	protected void readFromParcel(Parcel in)
	{
		saleIndex = in.readInt();
		discount = in.readInt();
		roomName = in.readString();
		option = in.readString();
		roomBenefit = in.readString();
		availableRooms = in.readInt();
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
