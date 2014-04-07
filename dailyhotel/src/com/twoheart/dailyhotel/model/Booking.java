package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Booking implements Parcelable {
	
	String sday;
	String hotel_idx;
	String hotel_name;
	
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

	}
	
	private void readFromParcel(Parcel in) {
		sday = in.readString();
		hotel_idx = in.readString();
		hotel_name = in.readString();
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
	
	public Booking(String sday, String hotel_idx, String hotel_name) {
		this.sday = sday;
		this.hotel_idx = hotel_idx;
		this.hotel_name = hotel_name;
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

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
