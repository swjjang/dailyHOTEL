package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

public class HotelPaymentInformation extends PlacePaymentInformation
{
    private SaleRoomInformation mSaleRoomInformation;
    //
    public String checkInOutDate; // Thankyou에 넘기기 위한 데이터 저장

    // None Parcelable
    public String checkInDateFormat; // yyyy-MM-dd'T'HH:mm:ssZZZZZ , 쿠폰 요청시 사용
    public String checkOutDateFormat; // yyyy-MM-dd'T'HH:mm:ssZZZZZ , 쿠폰 요청시 사용

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
        super.writeToParcel(dest, flags);

        dest.writeParcelable(mSaleRoomInformation, flags);

    }

    @Override
    protected void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

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

        PaymentType(String name)
        {
            mName = name;
        }

        public String getName()
        {
            return mName;
        }
    }
}
