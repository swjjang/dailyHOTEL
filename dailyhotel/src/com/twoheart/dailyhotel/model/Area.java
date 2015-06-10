package com.twoheart.dailyhotel.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class Area extends Province implements Parcelable
{
	public int provinceIndex;
	public String tag;

	public Area()
	{
		super();
	}

	public Area(Parcel in)
	{
		readFromParcel(in);
	}

	public Area(JSONObject jsonObject) throws JSONException
	{
		super(jsonObject);

		provinceIndex = jsonObject.getInt("province_idx");
		tag = jsonObject.getString("tag");
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		super.writeToParcel(dest, flags);

		dest.writeInt(provinceIndex);
		dest.writeString(tag);
	}

	protected void readFromParcel(Parcel in)
	{
		super.readFromParcel(in);

		provinceIndex = in.readInt();
		tag = in.readString();
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
	{
		public Province createFromParcel(Parcel in)
		{
			return new Area(in);
		}

		@Override
		public Province[] newArray(int size)
		{
			return new Area[size];
		}
	};
}
