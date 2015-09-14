package com.twoheart.dailyhotel.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.ExLog;

import org.json.JSONException;
import org.json.JSONObject;

public class Hotel implements Parcelable
{
	private String image;
	private String name;
	private int price;
	public int averageDiscount;
	private String address;
	private HotelGrade category;
	private int idx;
	private int availableRoom;
	private int sequence;
	private String bedType;
	private String detailRegion;
	public double mLatitude;
	public double mLongitude;
	public boolean isDailyChoice;
	public int saleIndex;
	public boolean isDBenefit;
	public int nights;

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

	public Hotel()
	{
		super();
	}

	public Hotel(Parcel in)
	{
		readFromParcel(in);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeString(image);
		dest.writeString(name);
		dest.writeInt(price);
		dest.writeInt(averageDiscount);
		dest.writeString(address);
		dest.writeSerializable(category);
		dest.writeInt(idx);
		dest.writeInt(availableRoom);
		dest.writeInt(sequence);
		dest.writeString(bedType);
		dest.writeDouble(mLatitude);
		dest.writeDouble(mLongitude);
		dest.writeInt(isDailyChoice ? 1 : 0);
		dest.writeInt(saleIndex);
		dest.writeInt(isDBenefit ? 1 : 0);
	}

	private void readFromParcel(Parcel in)
	{
		image = in.readString();
		name = in.readString();
		price = in.readInt();
		averageDiscount = in.readInt();
		address = in.readString();
		category = (HotelGrade) in.readSerializable();
		idx = in.readInt();
		availableRoom = in.readInt();
		sequence = in.readInt();
		bedType = in.readString();
		mLatitude = in.readDouble();
		mLongitude = in.readDouble();
		isDailyChoice = in.readInt() == 1 ? true : false;
		saleIndex = in.readInt();
		isDBenefit = in.readInt() == 1 ? true : false;
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

	public int getPrice()
	{
		return price;
	}

	public void setPrice(int price)
	{
		this.price = price;
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
		//	      "address": "제주특별자치도 제주시 중앙로 151",
		//	      "region_province_idx": 45,
		//	      "discount_total": 101600, (연박일 내 모든 객실의 총 판매가)
		//	      "discount_avg": 98000, (연박일 내 모든 객실의 총 판매가의 평균)
		//	      "district_seq": 2,
		//	      "imgDir": "jejukal",
		//	      "province_seq": 8,
		//	      "region_district_idx": 901,

		try
		{
			name = jsonObject.getString("name");
			price = Integer.parseInt(jsonObject.getString("price"));
			averageDiscount = Integer.parseInt(jsonObject.getString("discount_avg"));
			address = jsonObject.getString("addr_summary");

			try
			{
				category = HotelGrade.valueOf(jsonObject.getString("cat"));
			} catch (Exception e)
			{
				category = HotelGrade.etc;
			}

			idx = jsonObject.getInt("idx");
			availableRoom = jsonObject.getInt("avail_room_count");
			sequence = jsonObject.getInt("seq");
			detailRegion = jsonObject.getString("district_name");
			image = jsonObject.getString("img");

			if (jsonObject.has("lat") == true)
			{
				mLatitude = jsonObject.getDouble("lat");
			}

			if (jsonObject.has("lng") == true)
			{
				mLongitude = jsonObject.getDouble("lng");
			}

			if (jsonObject.has("is_dailychoice") == true)
			{
				isDailyChoice = jsonObject.getBoolean("is_dailychoice");
			}

			if (jsonObject.has("sale_idx") == true)
			{
				saleIndex = jsonObject.getInt("sale_idx");
			}

			if (jsonObject.has("hotel_benefit") == true)
			{
				String dBenefit = jsonObject.getString("hotel_benefit");

				if (TextUtils.isEmpty(dBenefit) == true || "null".equalsIgnoreCase(dBenefit) == true)
				{
					isDBenefit = false;
				} else
				{
					isDBenefit = true;
				}
			}

			if (jsonObject.has("nights") == true)
			{
				nights = jsonObject.getInt("nights");
			}
		} catch (JSONException e)
		{
			ExLog.d(e.toString());

			return false;
		}

		return true;
	}
}
