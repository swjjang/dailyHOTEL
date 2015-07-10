package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Pay implements Parcelable
{
	public enum Type
	{
		EASY_CARD, CARD, PHONE_PAY, VBANK,
		//		PAYPAL
	};

	private SaleRoomInformation mSaleRoomInformation;
	private Credit mCredit;
	private Customer mCustomer;
	private int mPayPrice;
	private int mOriginalPrice;
	private boolean isSaleCredit;
	private String mCheckOut;
	private Type mType;
	private Guest mGuest;

	public Pay()
	{
	}

	public Pay(Parcel in)
	{
		readFromParcel(in);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeValue(mSaleRoomInformation);
		dest.writeValue(mCredit);
		dest.writeValue(mCustomer);
		dest.writeInt(mPayPrice);
		dest.writeInt(mOriginalPrice);
		dest.writeByte((byte) (isSaleCredit ? 1 : 0));
		dest.writeString(mCheckOut);
		dest.writeSerializable(mType);
		dest.writeValue(mGuest);
	}

	private void readFromParcel(Parcel in)
	{
		mSaleRoomInformation = (SaleRoomInformation) in.readValue(SaleRoomInformation.class.getClassLoader());
		mCredit = (Credit) in.readValue(Credit.class.getClassLoader());
		mCustomer = (Customer) in.readValue(Customer.class.getClassLoader());
		mPayPrice = in.readInt();
		mOriginalPrice = in.readInt();
		isSaleCredit = in.readByte() != 0;
		mCheckOut = in.readString();
		mType = (Type) in.readSerializable();
		mGuest = (Guest) in.readValue(Guest.class.getClassLoader());
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
	{
		public Pay createFromParcel(Parcel in)
		{
			return new Pay(in);
		}

		@Override
		public Pay[] newArray(int size)
		{
			return new Pay[size];
		}

	};

	public SaleRoomInformation getSaleRoomInformation()
	{
		return mSaleRoomInformation;
	}

	public void setSaleRoomInformation(SaleRoomInformation information)
	{
		mSaleRoomInformation = information;
	}

	public Credit getCredit()
	{
		return mCredit;
	}

	public void setCredit(Credit credit)
	{
		this.mCredit = credit;
	}

	public Customer getCustomer()
	{
		return mCustomer;
	}

	public void setCustomer(Customer customer)
	{
		this.mCustomer = customer;
	}

	public Guest getGuest()
	{
		return mGuest;
	}

	public void setGuest(Guest guest)
	{
		mGuest = guest;
	}

	public int getPayPrice()
	{
		return mPayPrice;
	}

	public void setPayPrice(int payPrice)
	{
		this.mPayPrice = payPrice;
	}

	public int getOriginalPrice()
	{
		return mOriginalPrice;
	}

	public void setOriginalPrice(int originalPrice)
	{
		mOriginalPrice = originalPrice;
	}

	public boolean isSaleCredit()
	{
		return isSaleCredit;
	}

	public void setSaleCredit(boolean isSaleCredit)
	{
		this.isSaleCredit = isSaleCredit;
	}

	public String getCheckOut()
	{
		return mCheckOut;
	}

	public void setCheckOut(String checkOut)
	{
		mCheckOut = checkOut;
	}

	public Type getType()
	{
		return mType;
	}

	public void setType(Type type)
	{
		mType = type;
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

}
