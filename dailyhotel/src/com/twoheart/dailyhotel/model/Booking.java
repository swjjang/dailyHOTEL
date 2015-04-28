package com.twoheart.dailyhotel.model;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.ExLog;

public class Booking implements Parcelable
{
	private String sday;
	private String hotel_idx;
	private String hotel_name;
	private String bedType;
	private int payType;
	private String tid;
	public String ment;
	public int saleIdx;

	public Booking()
	{
	}

	public Booking(Parcel in)
	{
		readFromParcel(in);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeString(sday);
		dest.writeString(hotel_idx);
		dest.writeString(hotel_name);
		dest.writeString(bedType);
		dest.writeInt(payType);
		dest.writeString(tid);
		dest.writeString(ment);
		dest.writeInt(saleIdx);
	}

	private void readFromParcel(Parcel in)
	{
		sday = in.readString();
		hotel_idx = in.readString();
		hotel_name = in.readString();
		bedType = in.readString();
		payType = in.readInt();
		tid = in.readString();
		ment = in.readString();
		saleIdx = in.readInt();
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
			hotel_name = jsonObject.getString("hotel_name");
			sday = jsonObject.getString("sday");
			hotel_idx = jsonObject.getString("hotel_idx");
			bedType = jsonObject.getString("bed_type");
			payType = jsonObject.getInt("pay_type");
			tid = jsonObject.getString("tid");
			ment = jsonObject.getString("comment");
			saleIdx = jsonObject.getInt("saleidx");
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
