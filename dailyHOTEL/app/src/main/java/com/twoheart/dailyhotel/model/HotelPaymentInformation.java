package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

public class HotelPaymentInformation extends PlacePaymentInformation
{
    private RoomInformation mRoomInformation;
    // Thankyou에 넘기기 위한 데이터 저장
    public long checkInDate;
    public long checkOutDate;
    public int nights;

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

        dest.writeParcelable(mRoomInformation, flags);

    }

    @Override
    protected void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

        mRoomInformation = in.readParcelable(RoomInformation.class.getClassLoader());
    }

    public RoomInformation getSaleRoomInformation()
    {
        return mRoomInformation;
    }

    public void setSaleRoomInformation(RoomInformation information)
    {
        mRoomInformation = information;
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
}
