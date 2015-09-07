package com.twoheart.dailyhotel.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class PlaceBookingDetail implements Parcelable
{
	public int index;
	public String address;
	public double latitude;
	public double longitude;
	private LinkedHashMap<String, List<String>> mSpecification;
	public String placeName;
	public Place.Grade grade;
	public String guestName;
	public String guestPhone;
	public String guestEmail;
	public String addressSummary;

	public abstract void setData(JSONObject jsonObject) throws Exception;

	public PlaceBookingDetail()
	{
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeInt(index);
		dest.writeString(address);
		dest.writeDouble(latitude);
		dest.writeDouble(longitude);
		dest.writeMap(mSpecification);
		dest.writeString(placeName);
		dest.writeString(grade.name());
		dest.writeString(guestName);
		dest.writeString(guestPhone);
		dest.writeString(guestEmail);
		dest.writeString(addressSummary);
	}

	protected void readFromParcel(Parcel in)
	{
		index = in.readInt();
		address = in.readString();
		latitude = in.readDouble();
		longitude = in.readDouble();
		in.readMap(mSpecification, Map.class.getClassLoader());
		placeName = in.readString();
		grade = Place.Grade.valueOf(in.readString());
		guestName = in.readString();
		guestPhone = in.readString();
		guestEmail = in.readString();
		addressSummary = in.readString();
	}

	public Map<String, List<String>> getSpecification()
	{
		return mSpecification;
	}

	protected void setSpecification(JSONArray jsonArray) throws Exception
	{
		if (jsonArray == null)
		{
			return;
		}

		int length = jsonArray.length();

		mSpecification = new LinkedHashMap<String, List<String>>(length);

		for (int i = 0; i < length; i++)
		{
			JSONObject specObj = jsonArray.getJSONObject(i);

			if (specObj == null || specObj.has("key") == false || specObj.has("value") == false)
			{
				continue;
			}

			String key = specObj.getString("key");
			JSONArray valueArr = specObj.getJSONArray("value");
			List<String> valueList = new ArrayList<String>(valueArr.length());

			for (int j = 0; j < valueArr.length(); j++)
			{
				JSONObject valueObj = valueArr.getJSONObject(j);

				if (valueObj == null || valueObj.has("value") == false)
				{
					continue;
				}

				String value = valueObj.getString("value");
				valueList.add(value);
			}

			mSpecification.put(key, valueList);
		}
	}

	@Override
	public int describeContents()
	{
		return 0;
	}
}
