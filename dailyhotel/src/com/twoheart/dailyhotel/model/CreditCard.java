package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CreditCard implements Parcelable
{
	public String name;
	public String number;
	public String billingkey;

	public CreditCard()
	{

	}

	public CreditCard(Parcel in)
	{
		readFromParcel(in);
	}

	public CreditCard(String name, String number, String billkey)
	{
		this.name = name;
		this.number = number;
		this.billingkey = billkey;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeString(name);
		dest.writeString(number);
		dest.writeString(billingkey);
	}

	private void readFromParcel(Parcel in)
	{
		name = in.readString();
		number = in.readString();
		billingkey = in.readString();
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
	{
		public CreditCard createFromParcel(Parcel in)
		{
			return new CreditCard(in);
		}

		@Override
		public CreditCard[] newArray(int size)
		{
			return new CreditCard[size];
		}

	};

	@Override
	public int describeContents()
	{
		return 0;
	}
}
