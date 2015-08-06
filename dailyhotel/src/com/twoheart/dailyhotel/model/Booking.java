package com.twoheart.dailyhotel.model;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.ExLog;

public class Booking implements Parcelable
{
	public static final int TYPE_ENTRY = 0;
	public static final int TYPE_SECTION = 1;

	public int reservationIndex; // 호텔 예약 고유 번호.
	public int type = TYPE_ENTRY;
	private String hotelName;
	private int payType;
	private String tid;
	public String ment;
	public long checkinTime;
	public long checkoutTime;
	public String hotelImageUrl;
	public boolean isUsed;

	public Booking()
	{
	}

	public Booking(String sectionName)
	{
		hotelName = sectionName;
		type = TYPE_SECTION;
	}

	public Booking(Parcel in)
	{
		readFromParcel(in);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeInt(reservationIndex);
		dest.writeInt(type);
		dest.writeString(hotelName);
		dest.writeInt(payType);
		dest.writeString(tid);
		dest.writeString(ment);

		dest.writeLong(checkinTime);
		dest.writeLong(checkoutTime);
		dest.writeString(hotelImageUrl);
		dest.writeInt(isUsed ? 1 : 0);
	}

	private void readFromParcel(Parcel in)
	{
		reservationIndex = in.readInt();
		type = in.readInt();
		hotelName = in.readString();
		payType = in.readInt();
		tid = in.readString();
		ment = in.readString();

		checkinTime = in.readLong();
		checkoutTime = in.readLong();
		hotelImageUrl = in.readString();
		isUsed = in.readInt() == 1;
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
	{
		public Booking createFromParcel(Parcel in)
		{
			return new Booking(in);
		}

		@Override
		public Booking[] newArray(int size)
		{
			return new Booking[size];
		}
	};

	public Booking(JSONObject jsonObject)
	{
		try
		{
			if (jsonObject.has("reserv_idx") == true)
			{
				reservationIndex = jsonObject.getInt("reserv_idx");
			}

			hotelName = jsonObject.getString("hotel_name");
			payType = jsonObject.getInt("pay_type");
			ment = jsonObject.getString("comment");
			tid = jsonObject.getString("tid");
			checkinTime = jsonObject.getLong("checkin_time");
			checkoutTime = jsonObject.getLong("checkout_time");

			JSONArray jsonArray = jsonObject.getJSONArray("img");
			hotelImageUrl = jsonArray.getJSONObject(0).getString("path");
		} catch (Exception e)
		{
			ExLog.d(e.toString());
		}
	}

	public String getHotelName()
	{
		return hotelName;
	}

	public int getPayType()
	{
		return payType;
	}

	public String getTid()
	{
		return tid;
	}

	@Override
	public int describeContents()
	{
		return 0;
	}
}
