package com.twoheart.dailyhotel.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class Province implements Parcelable
{
	public int index;
	public String name;
	public int sequence;
	private int saleWeek = 1; // 1 : 1주일,  2 : 2주일

	public boolean isSelected;

	public Province()
	{
		super();
	}

	public Province(Parcel in)
	{
		readFromParcel(in);
	}

	public Province(JSONObject jsonObject) throws JSONException
	{
		index = jsonObject.getInt("idx");
		name = jsonObject.getString("name");
		sequence = jsonObject.getInt("seq");

		if (jsonObject.has("sale_week") == true)
		{
			saleWeek = jsonObject.getInt("sale_week");
		} else
		{
			saleWeek = 1;
		}
	}

	public int getProvinceIndex()
	{
		return index;
	}

	public int getSaleWeek()
	{
		return saleWeek;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeInt(index);
		dest.writeString(name);
		dest.writeInt(sequence);
		dest.writeInt(saleWeek);
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	protected void readFromParcel(Parcel in)
	{
		index = in.readInt();
		name = in.readString();
		sequence = in.readInt();
		saleWeek = in.readInt();
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
	{
		public Province createFromParcel(Parcel in)
		{
			return new Province(in);
		}

		@Override
		public Province[] newArray(int size)
		{
			return new Province[size];
		}

	};
}
