package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Pay implements Parcelable
{
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

    public int credit;
    public int hotelIndex;
    public String checkInTime;
    public String checkOutTime;
    private SaleRoomInformation mSaleRoomInformation;
    private Customer mCustomer;
    private int mOriginalPrice;
    private boolean isSaleCredit;
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
        dest.writeInt(credit);
        dest.writeValue(mCustomer);
        dest.writeInt(mOriginalPrice);
        dest.writeByte((byte) (isSaleCredit ? 1 : 0));
        dest.writeString(checkInTime);
        dest.writeString(checkOutTime);
        dest.writeSerializable(mType);
        dest.writeValue(mGuest);
        dest.writeInt(hotelIndex);
    }

    private void readFromParcel(Parcel in)
    {
        mSaleRoomInformation = (SaleRoomInformation) in.readValue(SaleRoomInformation.class.getClassLoader());
        credit = in.readInt();
        mCustomer = (Customer) in.readValue(Customer.class.getClassLoader());
        mOriginalPrice = in.readInt();
        isSaleCredit = in.readByte() != 0;
        checkInTime = in.readString();
        checkOutTime = in.readString();
        mType = (Type) in.readSerializable();
        mGuest = (Guest) in.readValue(Guest.class.getClassLoader());
        hotelIndex = in.readInt();
    }

    public SaleRoomInformation getSaleRoomInformation()
    {
        return mSaleRoomInformation;
    }

    public void setSaleRoomInformation(SaleRoomInformation information)
    {
        mSaleRoomInformation = information;
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

    public enum Type
    {
        EASY_CARD("EasyCardPay"),
        CARD("CardPay"),
        PHONE("PhoneBillPay"),
        VBANK("VirtualAccountPay");

        private String mName;

        private Type(String name)
        {
            mName = name;
        }

        public String getName()
        {
            return mName;
        }
    }
}
