package com.twoheart.dailyhotel.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class Area extends Province
{
	public Province province;
	private int provinceIndex;
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

	public Province getProvince()
	{
		return province;
	}

	public void setProvince(Province province)
	{
		this.province = province;
	}

	public void setProvinceIndex(int provinceIndex)
	{
		this.provinceIndex = provinceIndex;
	}

	@Override
	public int getProvinceIndex()
	{
		return provinceIndex;
	}

	@Override
	public int getSaleWeek()
	{
		if (province == null)
		{
			return 1;
		} else
		{
			return province.getSaleWeek();
		}
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		super.writeToParcel(dest, flags);

		dest.writeInt(provinceIndex);
		dest.writeString(tag);

		province.writeToParcel(dest, flags);
	}

	protected void readFromParcel(Parcel in)
	{
		super.readFromParcel(in);

		provinceIndex = in.readInt();
		tag = in.readString();

		province = new Province(in);
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
