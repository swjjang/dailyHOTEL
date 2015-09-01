package com.twoheart.dailyhotel.model;

import org.json.JSONObject;

import com.twoheart.dailyhotel.R;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

public abstract class BaseTicketDto implements Parcelable
{
	public int index;
	public String imageUrl;
	public String name;
	public int price;
	public int discountPrice;
	public String address;
	public HotelGrade grade;
	public String districtName;
	public double latitude;
	public double longitude;
	public boolean isDailyChoice;
	public boolean isSoldOut;

	public enum HotelGrade
	{
		biz(
				R.string.grade_biz,
				R.color.grade_hotel,
				R.drawable.bg_hotel_price_055870), //
		hostel(
				R.string.grade_hostel,
				R.color.grade_hotel,
				R.drawable.bg_hotel_price_055870), //
		grade1(R.string.grade_1, R.color.grade_hotel, R.drawable.bg_hotel_price_055870),
		grade2(
				R.string.grade_2,
				R.color.grade_hotel,
				R.drawable.bg_hotel_price_055870),
		grade3(
				R.string.grade_3,
				R.color.grade_hotel,
				R.drawable.bg_hotel_price_055870),
		boutique(
				R.string.grade_boutique,
				R.color.grade_boutique,
				R.drawable.bg_hotel_price_9f2d58),
		residence(
				R.string.grade_residence,
				R.color.grade_residence,
				R.drawable.bg_hotel_price_407f67),
		resort(
				R.string.grade_resort,
				R.color.grade_resort_pension_condo,
				R.drawable.bg_hotel_price_cf8d14),
		pension(
				R.string.grade_pension,
				R.color.grade_resort_pension_condo,
				R.drawable.bg_hotel_price_cf8d14),
		condo(
				R.string.grade_condo,
				R.color.grade_resort_pension_condo,
				R.drawable.bg_hotel_price_cf8d14),
		special(
				R.string.grade_special,
				R.color.grade_special,
				R.drawable.bg_hotel_price_ab380a),
		etc(
				R.string.grade_not_yet,
				R.color.grade_not_yet,
				R.drawable.bg_hotel_price_808080);

		private int mNameResId;
		private int mColorResId;
		private int mMarkerResId;

		private HotelGrade(int nameResId, int colorResId, int markerResId)
		{
			mNameResId = nameResId;
			mColorResId = colorResId;
			mMarkerResId = markerResId;
		}

		public String getName(Context context)
		{
			return context.getString(mNameResId);
		}

		public int getColorResId()
		{
			return mColorResId;
		}

		public int getMarkerResId()
		{
			return mMarkerResId;
		}
	};

	public BaseTicketDto()
	{
		super();
	}

	public BaseTicketDto(Parcel in)
	{
		readFromParcel(in);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeString(imageUrl);
		dest.writeString(name);
		dest.writeInt(price);
		dest.writeInt(discountPrice);
		dest.writeString(address);
		dest.writeSerializable(grade);
		dest.writeInt(index);
		dest.writeDouble(latitude);
		dest.writeDouble(longitude);
		dest.writeInt(isDailyChoice ? 1 : 0);
		dest.writeInt(isSoldOut ? 1 : 0);
	}

	protected void readFromParcel(Parcel in)
	{
		imageUrl = in.readString();
		name = in.readString();
		price = in.readInt();
		discountPrice = in.readInt();
		address = in.readString();
		grade = (HotelGrade) in.readSerializable();
		index = in.readInt();
		latitude = in.readDouble();
		longitude = in.readDouble();
		isDailyChoice = in.readInt() == 1 ? true : false;
		isSoldOut = in.readInt() == 1 ? true : false;
	}

	public abstract boolean setTicket(JSONObject jsonObject);
}
