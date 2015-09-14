package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class TicketPayment implements Parcelable
{
	public enum PaymentType
	{
		EASY_CARD, CARD, PHONE_PAY, VBANK,
	};

	private TicketInformation mTicketInformation;
	public int bonus;
	public boolean isEnabledBonus;
	public String checkInTime;
	public String checkOutTime;
	public PaymentType paymentType;
	private Customer mCustomer; // 로그인 유저 정보
	private Guest mGuest; // 실제 예약 혹은 투숙하는 사람
	public int ticketCount;
	public int ticketMaxCount; // 최대 결제 가능한 티켓 개수
	public long ticketTime;
	public long[] ticketTimes;

	public TicketPayment()
	{
		// Default Ticket count
		ticketCount = 1;
		ticketMaxCount = 1;
		paymentType = PaymentType.EASY_CARD;
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
		dest.writeSerializable(paymentType);
		dest.writeValue(mGuest);
		dest.writeInt(ticketCount);
		dest.writeInt(ticketMaxCount);
		dest.writeLong(ticketTime);
		dest.writeLongArray(ticketTimes);
	}

	private void readFromParcel(Parcel in)
	{
		mTicketInformation = (TicketInformation) in.readValue(TicketInformation.class.getClassLoader());
		bonus = in.readInt();
		mCustomer = (Customer) in.readValue(Customer.class.getClassLoader());
		isEnabledBonus = in.readByte() != 0;
		checkInTime = in.readString();
		checkOutTime = in.readString();
		paymentType = (PaymentType) in.readSerializable();
		mGuest = (Guest) in.readValue(Guest.class.getClassLoader());
		ticketCount = in.readInt();
		ticketMaxCount = in.readInt();
		ticketTime = in.readLong();
		ticketTimes = in.createLongArray();
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
		int price = (mTicketInformation.discountPrice * ticketCount) - (isEnabledBonus ? bonus : 0);

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
