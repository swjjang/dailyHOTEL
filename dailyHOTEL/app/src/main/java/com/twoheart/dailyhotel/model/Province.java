package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class Province implements Parcelable
{
	public int index;
	public String name;
	public int sequence;
	private int saleWeek = 1; // 1 : 1주일,  2 : 2주일
	public boolean isOverseas;

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

		if (jsonObject.has("seq") == true)
		{
			sequence = jsonObject.getInt("seq");
		} else
		{
			sequence = 0;
		}

		// 2주는 당분간 하지 않음
		//		if (jsonObject.has("sale_week") == true)
		//		{
		//			saleWeek = jsonObject.getInt("sale_week");
		//		} else
		{
			saleWeek = 1;
		}

		if (jsonObject.has("is_overseas") == true)
		{
			isOverseas = jsonObject.getBoolean("is_overseas");
		} else
		{
			isOverseas = false;
		}
	}

	public int getProvinceIndex()
	{
		return index;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeInt(index);
		dest.writeString(name);
		dest.writeInt(sequence);
		dest.writeInt(saleWeek);
		dest.writeInt(isOverseas ? 1 : 0);
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
		isOverseas = in.readInt() == 1 ? true : false;
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
