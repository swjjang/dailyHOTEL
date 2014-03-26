package com.twoheart.dailyhotel.obj;

import android.os.Parcel;
import android.os.Parcelable;

public class Customer implements Parcelable {
	
	private String mEmail;
	private String mName;
	private String mPhone;
	private String mAccessToken;
	
	public Customer() {
	}
	
	public Customer(Parcel in) {
		readFromParcel(in);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mEmail);
		dest.writeString(mName);
		dest.writeString(mPhone);
		dest.writeString(mAccessToken);
	}
	
	private void readFromParcel(Parcel in) {
		mEmail = in.readString();
		mName = in.readString();
		mPhone = in.readString();
		mAccessToken = in.readString();
	}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Customer createFromParcel(Parcel in) {
			return new Customer(in);
		}

		@Override
		public Customer[] newArray(int size) {
			return new Customer[size];
		}

	};
	
	public String getEmail() {
		return mEmail;
	}
	public void setEmail(String email) {
		this.mEmail = email;
	}
	public String getName() {
		return mName;
	}
	public void setName(String name) {
		this.mName = name;
	}
	public String getPhone() {
		return mPhone;
	}
	public void setPhone(String phone) {
		this.mPhone = phone;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getAccessToken() {
		return mAccessToken;
	}

	public void setAccessToken(String accessToken) {
		this.mAccessToken = accessToken;
	}

}
