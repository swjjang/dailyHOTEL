package com.twoheart.dailyhotel.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class FnBBookingDetail extends PlaceBookingDetail
{
	public int ticketCount;
	public String ticketName;
	public String sday;

	public FnBBookingDetail()
	{
	}

	public FnBBookingDetail(Parcel in)
	{
		readFromParcel(in);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		super.writeToParcel(dest, flags);

		dest.writeInt(ticketCount);
		dest.writeString(ticketName);
		dest.writeString(sday);
	}

	protected void readFromParcel(Parcel in)
	{
		super.readFromParcel(in);

		ticketCount = in.readInt();
		ticketName = in.readString();
		sday = in.readString();
	}

	public void setData(JSONObject jsonObject) throws Exception
	{
		index = jsonObject.getInt("idx");
		address = jsonObject.getString("address");
		latitude = jsonObject.getDouble("latitude");
		longitude = jsonObject.getDouble("longitude");
		placeName = jsonObject.getString("restaurant_name");

		grade = Place.Grade.valueOf(jsonObject.getString("grade"));
		guestName = jsonObject.getString("customer_name");
		guestPhone = jsonObject.getString("customer_phone");
		guestEmail = jsonObject.getString("customer_email");
		addressSummary = jsonObject.getString("customer_email");

		//
		JSONObject wrapJSONObject = new JSONObject(jsonObject.getString("description"));
		JSONArray jsonArray = wrapJSONObject.getJSONArray("wrap");

		setSpecification(jsonArray);

		ticketCount = jsonObject.getInt("ticket_count");
		ticketName = jsonObject.getString("ticket_name");
		long day = jsonObject.getLong("sday");

		SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd(EEE)", Locale.KOREA);
		format.setTimeZone(TimeZone.getTimeZone("GMT"));

		sday = format.format(new Date(day));
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
	{
		public FnBBookingDetail createFromParcel(Parcel in)
		{
			return new FnBBookingDetail(in);
		}

		@Override
		public FnBBookingDetail[] newArray(int size)
		{
			return new FnBBookingDetail[size];
		}
	};
}
