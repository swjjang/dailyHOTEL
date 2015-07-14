package com.twoheart.dailyhotel.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.ExLog;

public class BookingHotelDetail implements Parcelable
{
	private Hotel mHotel;
	private double mLatitude;
	private double mLongitude;
	private Map<String, List<String>> mSpecification = new HashMap<String, List<String>>();
	private List<String> mImageUrl = new ArrayList<String>();
	private int mSaleIdx;
	public int isOverseas; // 0 : 국내 , 1 : 해외 
	public String roomName;

	public BookingHotelDetail()
	{
	}

	public BookingHotelDetail(Parcel in)
	{
		readFromParcel(in);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeValue(mHotel);
		dest.writeDouble(mLatitude);
		dest.writeDouble(mLongitude);
		dest.writeMap(mSpecification);
		dest.writeList(mImageUrl);
		dest.writeInt(mSaleIdx);
		dest.writeInt(isOverseas);
		dest.writeString(roomName);
	}

	private void readFromParcel(Parcel in)
	{
		mHotel = (Hotel) in.readValue(Hotel.class.getClassLoader());
		mLatitude = in.readDouble();
		mLongitude = in.readDouble();
		in.readMap(mSpecification, Map.class.getClassLoader());
		in.readList(mImageUrl, List.class.getClassLoader());
		mSaleIdx = in.readInt();
		isOverseas = in.readInt();
		roomName = in.readString();
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
	{
		public BookingHotelDetail createFromParcel(Parcel in)
		{
			return new BookingHotelDetail(in);
		}

		@Override
		public BookingHotelDetail[] newArray(int size)
		{
			return new BookingHotelDetail[size];
		}

	};

	public List<String> getImageUrl()
	{
		return mImageUrl;
	}

	public void setImageUrl(List<String> imageUrl)
	{
		this.mImageUrl = imageUrl;
	}

	public Hotel getHotel()
	{
		return mHotel;
	}

	public void setHotel(Hotel hotel)
	{
		this.mHotel = hotel;
	}

	public double getLatitude()
	{
		return mLatitude;
	}

	public void setLatitude(double latitude)
	{
		this.mLatitude = latitude;
	}

	public double getLongitude()
	{
		return mLongitude;
	}

	public void setLongitude(double longitude)
	{
		this.mLongitude = longitude;
	}

	public Map<String, List<String>> getSpecification()
	{
		return mSpecification;
	}

	public void setSpecification(JSONArray jsonArray)
	{
		if (jsonArray == null)
		{
			return;
		}

		try
		{
			int length = jsonArray.length();

			Map<String, List<String>> contentList = new LinkedHashMap<String, List<String>>(length);

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

				contentList.put(key, valueList);
				setSpecification(contentList);
			}
		} catch (Exception e)
		{
			ExLog.d(e.toString());
		}
	}

	public void setSpecification(Map<String, List<String>> specification)
	{
		this.mSpecification = specification;
	}

	public int getSaleIdx()
	{
		return mSaleIdx;
	}

	public void setSaleIdx(int saleIdx)
	{
		this.mSaleIdx = saleIdx;
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

}
