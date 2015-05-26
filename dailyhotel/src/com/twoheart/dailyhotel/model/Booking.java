package com.twoheart.dailyhotel.model;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.ExLog;

public class Booking implements Parcelable
{
	public static final int TYPE_ENTRY = 0;
	public static final int TYPE_SECTION = 1;

	public int index; // 호텔 예약 고유 번호.
	public int type = TYPE_ENTRY;
	private String sday;
	private String hotel_idx;
	private String hotel_name;
	private String bedType;
	private int payType;
	private String tid;
	public String ment;
	public int saleIdx;

	public long checkinTime;
	public long checkoutTime;
	public String hotelImageUrl;
	public boolean isUsed;

	public Booking()
	{
	}

	public Booking(String sectionName)
	{
		hotel_name = sectionName;
		type = TYPE_SECTION;
	}

	public Booking(Parcel in)
	{
		readFromParcel(in);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeInt(index);
		dest.writeInt(type);
		dest.writeString(sday);
		dest.writeString(hotel_idx);
		dest.writeString(hotel_name);
		dest.writeString(bedType);
		dest.writeInt(payType);
		dest.writeString(tid);
		dest.writeString(ment);
		dest.writeInt(saleIdx);

		dest.writeLong(checkinTime);
		dest.writeLong(checkoutTime);
		dest.writeString(hotelImageUrl);
		dest.writeInt(isUsed ? 1 : 0);
	}

	private void readFromParcel(Parcel in)
	{
		index = in.readInt();
		type = in.readInt();
		sday = in.readString();
		hotel_idx = in.readString();
		hotel_name = in.readString();
		bedType = in.readString();
		payType = in.readInt();
		tid = in.readString();
		ment = in.readString();
		saleIdx = in.readInt();

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
			if (jsonObject.has("idx") == true)
			{
				index = jsonObject.getInt("idx");
			}
			
			hotel_name = jsonObject.getString("hotel_name");
			sday = jsonObject.getString("sday");
			hotel_idx = jsonObject.getString("hotel_idx");
			bedType = jsonObject.getString("bed_type");
			payType = jsonObject.getInt("pay_type");
			tid = jsonObject.getString("tid");
			ment = jsonObject.getString("comment");

			if (jsonObject.has("saleidx") == true)
			{
				saleIdx = jsonObject.getInt("saleidx");
			}

			checkinTime = jsonObject.getLong("checkin_time");
			checkoutTime = jsonObject.getLong("checkout_time");
			hotelImageUrl = jsonObject.getString("path");
		} catch (Exception e)
		{
			ExLog.d(e.toString());
		}
	}

	public String getSday()
	{
		return sday;
	}

	public void setSday(String sday)
	{
		this.sday = sday;
	}

	public String getHotel_idx()
	{
		return hotel_idx;
	}

	public void setHotel_idx(String hotel_idx)
	{
		this.hotel_idx = hotel_idx;
	}

	public String getHotel_name()
	{
		return hotel_name;
	}

	public void setHotel_name(String hotel_name)
	{
		this.hotel_name = hotel_name;
	}

	public String getBedType()
	{
		return bedType;
	}

	public void setBedType(String bedType)
	{
		this.bedType = bedType;
	}

	public int getPayType()
	{
		return payType;
	}

	public void setPayType(int payType)
	{
		this.payType = payType;
	}

	public String getTid()
	{
		return tid;
	}

	public void setTid(String tid)
	{
		this.tid = tid;
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

}
