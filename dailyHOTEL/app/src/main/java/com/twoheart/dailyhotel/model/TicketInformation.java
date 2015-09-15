package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class TicketInformation implements Parcelable
{
	public int index;
	public String name;
	public String option;
	public String benefit;
	public int discountPrice;
	public String placeName;

	public TicketInformation(Parcel in)
	{
		readFromParcel(in);
	}

	public TicketInformation(String placeName, JSONObject jsonObject) throws Exception
	{
		index = jsonObject.getInt("sale_reco_idx");
		name = jsonObject.getString("ticket_name").trim();
		option = jsonObject.getString("option").trim();
		benefit = jsonObject.getString("ticket_benefit").trim();
		discountPrice = jsonObject.getInt("discount");

		this.placeName = placeName;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeInt(index);
		dest.writeString(name);
		dest.writeString(option);
		dest.writeString(benefit);
		dest.writeInt(discountPrice);
		dest.writeString(placeName);
	}

	protected void readFromParcel(Parcel in)
	{
		index = in.readInt();
		name = in.readString();
		option = in.readString();
		benefit = in.readString();
		discountPrice = in.readInt();
		placeName = in.readString();
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
	{
		public TicketInformation createFromParcel(Parcel in)
		{
			return new TicketInformation(in);
		}

		@Override
		public TicketInformation[] newArray(int size)
		{
			return new TicketInformation[size];
		}
	};

	@Override
	public int describeContents()
	{
		return 0;
	}
}
