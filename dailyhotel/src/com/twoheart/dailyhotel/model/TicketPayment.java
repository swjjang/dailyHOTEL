package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

public class TicketPayment implements Parcelable
{
	public enum Type
	{
		EASY_CARD, CARD, PHONE_PAY, VBANK,
		//		PAYPAL
	};

	private TicketInformation mTicketInformation;
	public int bonus;
	public boolean isEnabledBonus;
	public String checkInTime;
	public String checkOutTime;
	public Type type;
	private Customer mCustomer; // 로그인 유저 정보
	private Guest mGuest; // 실제 예약 혹은 투숙하는 사람
	public int count;
	public int maxCount; // 최대 결제 가능한 티켓 개수

	public TicketPayment()
	{
		// Default Ticket count
		count = 1;
		maxCount = 1;
		type = Type.EASY_CARD;
	}

	public TicketPayment(Parcel in)
	{
		readFromParcel(in);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeValue(mTicketInformation);
		dest.writeInt(bonus);
		dest.writeValue(mCustomer);
		dest.writeByte((byte) (isEnabledBonus ? 1 : 0));
		dest.writeString(checkInTime);
		dest.writeString(checkOutTime);
		dest.writeSerializable(type);
		dest.writeValue(mGuest);
		dest.writeInt(count);
		dest.writeInt(maxCount);
	}

	private void readFromParcel(Parcel in)
	{
		mTicketInformation = (TicketInformation) in.readValue(TicketInformation.class.getClassLoader());
		bonus = in.readInt();
		mCustomer = (Customer) in.readValue(Customer.class.getClassLoader());
		isEnabledBonus = in.readByte() != 0;
		checkInTime = in.readString();
		checkOutTime = in.readString();
		type = (Type) in.readSerializable();
		mGuest = (Guest) in.readValue(Guest.class.getClassLoader());
		count = in.readInt();
		maxCount = in.readInt();
	}

	public TicketInformation getTicketInformation()
	{
		return mTicketInformation;
	}

	public void setTicketInformation(TicketInformation information)
	{
		mTicketInformation = information;
	}

	public Customer getCustomer()
	{
		return mCustomer;
	}

	public void setCustomer(Customer customer)
	{
		mCustomer = customer;
	}

	public Guest getGuest()
	{
		return mGuest;
	}

	public void setGuest(Guest guest)
	{
		mGuest = guest;
	}

	/**
	 * 적립금이 반영된 가격
	 * 
	 * @return
	 */
	public int getPaymentToPay()
	{
		int price = (mTicketInformation.discountPrice * count) - (isEnabledBonus ? bonus : 0);

		if (price < 0)
		{
			price = 0;
		}

		return price;
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
	{
		public TicketPayment createFromParcel(Parcel in)
		{
			return new TicketPayment(in);
		}

		@Override
		public TicketPayment[] newArray(int size)
		{
			return new TicketPayment[size];
		}

	};
}
