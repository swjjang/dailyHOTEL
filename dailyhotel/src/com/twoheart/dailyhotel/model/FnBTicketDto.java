package com.twoheart.dailyhotel.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.twoheart.dailyhotel.util.ExLog;

import android.os.Parcel;
import android.os.Parcelable;

public class FnBTicketDto extends BaseTicketDto implements Parcelable
{
	public String saleDay;

	public FnBTicketDto()
	{
		super();
	}

	public FnBTicketDto(Parcel in)
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

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
	{
		public FnBTicketDto createFromParcel(Parcel in)
		{
			return new FnBTicketDto(in);
		}

		@Override
		public FnBTicketDto[] newArray(int size)
		{
			return new FnBTicketDto[size];
		}
	};

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public boolean setTicket(JSONObject jsonObject)
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
				grade = HotelGrade.valueOf(jsonObject.getString("grade"));
			} catch (Exception e)
			{
				grade = HotelGrade.etc;
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
}
