package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class PlacePaymentInformation implements Parcelable
{
    public int placeIndex;
    public int bonus;
    public boolean isEnabledBonus;
    public PaymentType paymentType;
    public boolean isDBenefit;

    private Customer mCustomer;
    private Guest mGuest;

    public PlacePaymentInformation()
    {
        paymentType = PaymentType.EASY_CARD;
    }

    public PlacePaymentInformation(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(placeIndex);
        dest.writeInt(bonus);
        dest.writeInt(isEnabledBonus ? 1 : 0);
        dest.writeString(paymentType.name());
        dest.writeInt(isDBenefit ? 1 : 0);
        dest.writeParcelable(mCustomer, flags);
        dest.writeParcelable(mGuest, flags);
    }

    protected void readFromParcel(Parcel in)
    {
        placeIndex = in.readInt();
        bonus = in.readInt();
        isEnabledBonus = in.readInt() == 1;
        paymentType = PaymentType.valueOf(in.readString());
        isDBenefit = in.readInt() == 1;
        mCustomer = in.readParcelable(Customer.class.getClassLoader());
        mGuest = in.readParcelable(Guest.class.getClassLoader());
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

    @Override
    public int describeContents()
    {
        return 0;
    }

    // 명칭 변경하면 안됨 서버와 약속되어있음.
    public enum PaymentType
    {
        EASY_CARD("EasyCardPay"),
        CARD("CardPay"),
        PHONE_PAY("PhoneBillPay"),
        VBANK("VirtualAccountPay");

        private String mName;

        private PaymentType(String name)
        {
            mName = name;
        }

        public String getName()
        {
            return mName;
        }
    }
}
