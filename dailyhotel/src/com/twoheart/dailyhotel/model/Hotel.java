package com.twoheart.dailyhotel.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.ExLog;

public class Hotel implements Parcelable
{
	private String image;
	private String name;
	private String price;
	private String discount;
	private String address;
	private HotelGrade category;
	private int idx;
	private int availableRoom;
	private int sequence;
	private String bedType;
	private String detailRegion;
	public double mLatitude;
	public double mLongitude;

	public enum HotelGrade
	{
		biz(R.string.grade_biz, R.color.grade_hotel), //
		hostel(R.string.grade_hostel, R.color.grade_hotel), //
		grade1(R.string.grade_1, R.color.grade_hotel),
		grade2(R.string.grade_2, R.color.grade_hotel),
		grade3(R.string.grade_3, R.color.grade_hotel),
		boutique(R.string.grade_boutique, R.color.grade_boutique),
		residence(R.string.grade_residence, R.color.grade_residence),
		resort(R.string.grade_resort, R.color.grade_resort_pension_condo),
		pension(R.string.grade_pension, R.color.grade_resort_pension_condo),
		condo(R.string.grade_condo, R.color.grade_resort_pension_condo),
		special(R.string.grade_special, R.color.grade_special),
		etc(R.string.grade_not_yet, R.color.grade_not_yet);

		private int mNameResId;
		private int mColorResId;

		private HotelGrade(int nameResId, int colorResId)
		{
			mNameResId = nameResId;
			mColorResId = colorResId;
		}

		public String getName(Context context)
		{
			return context.getString(mNameResId);
		}

		public int getColorResId()
		{
			return mColorResId;
		}
	};

	public Hotel()
	{
		super();
	}

	public Hotel(Parcel in)
	{
		readFromParcel(in);
	}

	public Hotel(String image, String name, String price, String discount, String address, String category, int idx, int availableRoom, int sequence, String bedType)
	{
		this.image = image;
		this.name = name;
		this.price = price;
		this.discount = discount;
		this.address = address;
		this.category = HotelGrade.valueOf(category);
		this.idx = idx;
		this.availableRoom = availableRoom;
		this.sequence = sequence;
		this.bedType = bedType;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeString(image);
		dest.writeString(name);
		dest.writeString(price);
		dest.writeString(discount);
		dest.writeString(address);
		dest.writeSerializable(category);
		dest.writeInt(idx);
		dest.writeInt(availableRoom);
		dest.writeInt(sequence);
		dest.writeString(bedType);
		dest.writeDouble(mLatitude);
		dest.writeDouble(mLongitude);
	}

	private void readFromParcel(Parcel in)
	{
		this.image = in.readString();
		this.name = in.readString();
		this.price = in.readString();
		this.discount = in.readString();
		this.address = in.readString();
		this.category = (HotelGrade) in.readSerializable();
		this.idx = in.readInt();
		this.availableRoom = in.readInt();
		this.sequence = in.readInt();
		this.bedType = in.readString();
		this.mLatitude = in.readDouble();
		this.mLongitude = in.readDouble();
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
	{
		public Hotel createFromParcel(Parcel in)
		{
			return new Hotel(in);
		}

		@Override
		public Hotel[] newArray(int size)
		{
			return new Hotel[size];
		}

	};

	public HotelGrade getCategory()
	{
		return category;
	}

	public void setCategory(String category)
	{
		this.category = HotelGrade.valueOf(category);
	}

	public String getImage()
	{
		return image;
	}

	public void setImage(String image)
	{
		this.image = image;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getPrice()
	{
		return price;
	}

	public void setPrice(String price)
	{
		this.price = price;
	}

	public String getDiscount()
	{
		return discount;
	}

	public void setDiscount(String discount)
	{
		this.discount = discount;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public int getIdx()
	{
		return idx;
	}

	public void setIdx(int idx)
	{
		this.idx = idx;
	}

	public int getAvailableRoom()
	{
		return availableRoom;
	}

	public void setAvailableRoom(int availableRoom)
	{
		this.availableRoom = availableRoom;
	}

	public int getSequence()
	{
		return sequence;
	}

	public void setSequence(int sequence)
	{
		this.sequence = sequence;
	}

	public String getBedType()
	{
		return bedType;
	}

	public void setBedType(String bedType)
	{
		this.bedType = bedType;
	}

	public String getDetailRegion()
	{
		return detailRegion;
	}

	public void setDetailRegion(String detailRegion)
	{
		this.detailRegion = detailRegion;
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	public boolean setHotel(JSONObject jsonObject)
	{
		try
		{
			name = jsonObject.getString("name");
			price = jsonObject.getString("price");
			discount = jsonObject.getString("discount");
			address = jsonObject.getString("addr_summary");
			category = HotelGrade.valueOf(jsonObject.getString("cat"));
			idx = jsonObject.getInt("idx");
			availableRoom = jsonObject.getInt("avail_room_count");
			sequence = jsonObject.getInt("seq");
			detailRegion = jsonObject.getString("site2_name");

			JSONArray jsonArray = jsonObject.getJSONArray("img");
			image = "default";
			if (jsonArray.length() != 0)
			{
				JSONObject arrObj = jsonArray.getJSONObject(0);
				image = arrObj.getString("path");
			}

			if (jsonObject.has("lat") == true)
			{
				mLatitude = jsonObject.getDouble("lat");
			}

			if (jsonObject.has("lng") == true)
			{
				mLongitude = jsonObject.getDouble("lng");
			}
		} catch (JSONException e)
		{
			ExLog.d(e.toString());

			return false;
		}

		return true;
	}
}
