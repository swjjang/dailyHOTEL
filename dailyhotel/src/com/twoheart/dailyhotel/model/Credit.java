package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Credit implements Parcelable {
	private String content;
	private String bonus;
	private String expires;
	
	public Credit() {
	}
	
	public Credit(Parcel in) {
		readFromParcel(in);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(content);
		dest.writeString(bonus);
		dest.writeString(expires);
	}
	
	private void readFromParcel(Parcel in) {
		content = in.readString();
		bonus = in.readString();
		expires = in.readString();
	}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Credit createFromParcel(Parcel in) {
			return new Credit(in);
		}

		@Override
		public Credit[] newArray(int size) {
			return new Credit[size];
		}

	};
	
	public Credit(String content, String bonus, String expires) {
		this.content = content;
		this.bonus = bonus;
		this.expires = expires;
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getBonus() {
		return bonus;
	}
	public void setBonus(String bonus) {
		this.bonus = bonus;
	}
	public String getExpires() {
		return expires;
	}
	public void setExpires(String expires) {
		this.expires = expires;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
}
