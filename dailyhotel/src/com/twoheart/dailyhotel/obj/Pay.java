package com.twoheart.dailyhotel.obj;

import java.util.List;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

public class Pay implements Parcelable {
	
	private HotelDetail mHotelDetail;
	private Credit mCredit;
	private Customer mCustomer;
	private int mPayPrice;
	private boolean isSaleCredit;
	
	public Pay() {
	}
	
	public Pay(Parcel in) {
		readFromParcel(in);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeValue(mHotelDetail);
		dest.writeValue(mCredit);
		dest.writeValue(mCustomer);
		dest.writeInt(mPayPrice);
		dest.writeByte((byte) (isSaleCredit ? 1 : 0));
	}
	
	private void readFromParcel(Parcel in) {
		mHotelDetail = (HotelDetail) in.readValue(HotelDetail.class.getClassLoader());
		mCredit = (Credit) in.readValue(Credit.class.getClassLoader());
		mCustomer = (Customer) in.readValue(Customer.class.getClassLoader());
		mPayPrice = in.readInt();
		isSaleCredit = in.readByte() != 0;
	}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Pay createFromParcel(Parcel in) {
			return new Pay(in);
		}

		@Override
		public Pay[] newArray(int size) {
			return new Pay[size];
		}

	};
	
	public HotelDetail getHotelDetail() {
		return mHotelDetail;
	}
	public void setHotelDetail(HotelDetail hotelDetail) {
		this.mHotelDetail = hotelDetail;
	}
	public Credit getCredit() {
		return mCredit;
	}
	public void setCredit(Credit credit) {
		this.mCredit = credit;
	}
	public Customer getCustomer() {
		return mCustomer;
	}
	public void setCustomer(Customer customer) {
		this.mCustomer = customer;
	}
	public int getPayPrice() {
		return mPayPrice;
	}
	public void setPayPrice(int payPrice) {
		this.mPayPrice = payPrice;
	}
	public boolean isSaleCredit() {
		return isSaleCredit;
	}
	public void setSaleCredit(boolean isSaleCredit) {
		this.isSaleCredit = isSaleCredit;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

}
