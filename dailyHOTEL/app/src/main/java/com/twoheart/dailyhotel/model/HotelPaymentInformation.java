package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

public class HotelPaymentInformation extends PlacePaymentInformation
{
    public Hotel.HotelGrade grade;
    public int originalPrice;
    private SaleRoomInformation mSaleRoomInformation;
    //
    public String checkInOutDate; // Thankyou에 넘기기 위한 데이터 저장

    public HotelPaymentInformation()
    {
        super();
    }

    public HotelPaymentInformation(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(grade.name());
        dest.writeInt(originalPrice);
        dest.writeParcelable(mSaleRoomInformation, flags);
    }

    private void readFromParcel(Parcel in)
    {
        try
        {
            grade = Hotel.HotelGrade.valueOf(in.readString());
        } catch (Exception e)
        {
            grade = Hotel.HotelGrade.etc;
        }

        originalPrice = in.readInt();
        mSaleRoomInformation = in.readParcelable(SaleRoomInformation.class.getClassLoader());
    }

    public SaleRoomInformation getSaleRoomInformation()
    {
        return mSaleRoomInformation;
    }

    public void setSaleRoomInformation(SaleRoomInformation information)
    {
        mSaleRoomInformation = information;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public HotelPaymentInformation createFromParcel(Parcel in)
        {
            return new HotelPaymentInformation(in);
        }

        @Override
        public HotelPaymentInformation[] newArray(int size)
        {
            return new HotelPaymentInformation[size];
        }
    };

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
