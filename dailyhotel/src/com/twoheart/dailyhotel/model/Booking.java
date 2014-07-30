package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Booking implements Parcelable {
	
	private String sday;
	private String hotel_idx;
	private String hotel_name;
	private String bedType;
	
	public Booking() {
	}
	
	public Booking(Parcel in) {
		readFromParcel(in);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(sday);
		dest.writeString(hotel_idx);
		dest.writeString(hotel_name);
		dest.writeString(bedType);

	}
	
	private void readFromParcel(Parcel in) {
		sday = in.readString();
		hotel_idx = in.readString();
		hotel_name = in.readString();
		bedType = in.readString();
	}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Booking createFromParcel(Parcel in) {
			return new Booking(in);
		}

		@Override
		public Booking[] newArray(int size) {
			return new Booking[size];
		}

	};
	
	public Booking(String sday, String hotel_idx, String hotel_name, String bedType) {
		this.sday = sday;
		this.hotel_idx = hotel_idx;
		this.hotel_name = hotel_name;
		this.bedType = bedType;
		
	}

	public String getSday() {
		return sday;
	}

	public void setSday(String sday) {
		this.sday = sday;
	}

	public String getHotel_idx() {
		return hotel_idx;
	}

	public void setHotel_idx(String hotel_idx) {
		this.hotel_idx = hotel_idx;
	}

	public String getHotel_name() {
		return hotel_name;
	}

	public void setHotel_name(String hotel_name) {
		this.hotel_name = hotel_name;
	}
	
	public String getBedType() {
		return bedType;
	}

	public void setBedType(String bedType) {
		this.bedType = bedType;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
}
