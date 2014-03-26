package com.twoheart.dailyhotel.obj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

public class HotelDetail implements Parcelable {
	
	private Hotel mHotel;
	private double mLatitude;
	private double mLongitude;
	private Map<String, List<String>> mSpecification = new HashMap<String, List<String>>();
	private List<String> mImageUrl = new ArrayList<String>();
	private int mSaleIdx;
	
	public HotelDetail() {
	}
	
	public HotelDetail(Parcel in) {
		readFromParcel(in);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeValue(mHotel);
		dest.writeDouble(mLatitude);
		dest.writeDouble(mLongitude);
		dest.writeMap(mSpecification);
		dest.writeList(mImageUrl);
		dest.writeInt(mSaleIdx);

	}
	
	private void readFromParcel(Parcel in) {
		mHotel = (Hotel) in.readValue(Hotel.class.getClassLoader());
		mLatitude = in.readDouble();
		mLongitude = in.readDouble();
		in.readMap(mSpecification, Map.class.getClassLoader());
		in.readList(mImageUrl, List.class.getClassLoader());
		mSaleIdx = in.readInt();
	}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public HotelDetail createFromParcel(Parcel in) {
			return new HotelDetail(in);
		}

		@Override
		public HotelDetail[] newArray(int size) {
			return new HotelDetail[size];
		}

	};
	
	public List<String> getImageUrl() {
		return mImageUrl;
	}
	public void setImageUrl(List<String> imageUrl) {
		this.mImageUrl = imageUrl;
	}
	public Hotel getHotel() {
		return mHotel;
	}
	public void setHotel(Hotel hotel) {
		this.mHotel = hotel;
	}
	public double getLatitude() {
		return mLatitude;
	}
	public void setLatitude(double latitude) {
		this.mLatitude = latitude;
	}
	public double getLongitude() {
		return mLongitude;
	}
	public void setLongitude(double longitude) {
		this.mLongitude = longitude;
	}
	public Map<String, List<String>> getSpecification() {
		return mSpecification;
	}
	public void setSpecification(Map<String, List<String>> specification) {
		this.mSpecification = specification;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getSaleIdx() {
		return mSaleIdx;
	}

	public void setSaleIdx(int saleIdx) {
		this.mSaleIdx = saleIdx;
	}

}
