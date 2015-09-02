package com.twoheart.dailyhotel.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.twoheart.dailyhotel.util.ExLog;

import android.os.Parcel;
import android.os.Parcelable;

public class FnB extends Place implements Parcelable
{
	public String saleDay;

	public FnB()
	{
		super();
	}

	public FnB(Parcel in)
	{
		readFromParcel(in);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		super.writeToParcel(dest, flags);

		dest.writeString(saleDay);
	}

	protected void readFromParcel(Parcel in)
	{
		super.readFromParcel(in);

		saleDay = in.readString();
	}

	@Override
	public boolean setData(JSONObject jsonObject)
	{
		try
		{
			index = jsonObject.getInt("restaurant_idx");
			name = jsonObject.getString("restaurant_name");

			price = jsonObject.getInt("price");
			discountPrice = jsonObject.getInt("discount");
			address = jsonObject.getString("addr_summary");

			try
			{
				grade = Grade.valueOf(jsonObject.getString("grade"));
			} catch (Exception e)
			{
				grade = Grade.etc;
			}

			districtName = jsonObject.getString("district_name");
			imageUrl = jsonObject.getString("img");

			latitude = jsonObject.getDouble("latitude");
			longitude = jsonObject.getDouble("longitude");
			isDailyChoice = "Y".equalsIgnoreCase(jsonObject.getString("is_dailychoice"));
			isSoldOut = "Y".equalsIgnoreCase(jsonObject.getString("is_soldout"));

			saleDay = jsonObject.getString("sday");

		} catch (JSONException e)
		{
			ExLog.d(e.toString());

			return false;
		}

		return true;
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
	{
		public FnB createFromParcel(Parcel in)
		{
			return new FnB(in);
		}

		@Override
		public FnB[] newArray(int size)
		{
			return new FnB[size];
		}
	};

	@Override
	public int describeContents()
	{
		return 0;
	}
}
