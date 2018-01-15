package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;

import com.daily.dailyhotel.entity.StayBookingDetail;
import com.twoheart.dailyhotel.model.Stay;

/**
 * Created by android_sam on 2018. 1. 15..
 */

public class StayBookingDetailParcel implements Parcelable
{
    private StayBookingDetail mStayBookingDetail;

    public StayBookingDetailParcel(StayBookingDetail stayBookingDetail)
    {
        if (stayBookingDetail == null)
        {
            throw new NullPointerException("stayBookingDetail == null");
        }

        this.mStayBookingDetail = stayBookingDetail;
    }

    public StayBookingDetailParcel(Parcel in)
    {
        readFromParcel(in);
    }

    public StayBookingDetail getStayBookingDetail()
    {
        return mStayBookingDetail;
    }

    @Override
    public void writeToParcel(Parcel dest, int flag)
    {
        dest.writeInt(mStayBookingDetail.reservationIndex);
        dest.writeInt(mStayBookingDetail.stayIndex);
        dest.writeInt(mStayBookingDetail.userIndex);
        dest.writeInt(mStayBookingDetail.userCouponIndex);
        dest.writeInt(mStayBookingDetail.regionProvinceIndex);
        dest.writeString(mStayBookingDetail.regionDistrictName);
        dest.writeString(mStayBookingDetail.stayName);
        dest.writeString(mStayBookingDetail.stayAddress);
        dest.writeString(mStayBookingDetail.addressSummary);
        dest.writeString(mStayBookingDetail.phone1);
        dest.writeString(mStayBookingDetail.phone2);
        dest.writeString(mStayBookingDetail.phone3);
        dest.writeString(mStayBookingDetail.stayGrade.name());
        dest.writeString(mStayBookingDetail.checkInDateTime);
        dest.writeString(mStayBookingDetail.checkOutDateTime);
        dest.writeDouble(mStayBookingDetail.latitude);
        dest.writeDouble(mStayBookingDetail.longitude);
        dest.writeString(mStayBookingDetail.guestEmail);
        dest.writeString(mStayBookingDetail.guestName);
        dest.writeString(mStayBookingDetail.guestPhone);
        dest.writeInt(mStayBookingDetail.roomIndex);
        dest.writeString(mStayBookingDetail.roomName);
        dest.writeString(mStayBookingDetail.guestTransportation);
        dest.writeString(mStayBookingDetail.refundStatus);
        dest.writeString(mStayBookingDetail.paymentDateTime);
        dest.writeInt(mStayBookingDetail.priceTotal);
        dest.writeInt(mStayBookingDetail.discountTotal);
        dest.writeInt(mStayBookingDetail.bonusAmount);
        dest.writeInt(mStayBookingDetail.couponAmount);
        dest.writeString(mStayBookingDetail.transactionType);
        dest.writeString(mStayBookingDetail.reviewStatusType);
        dest.writeString(mStayBookingDetail.refundType);
        dest.writeInt(mStayBookingDetail.overseas ? 1 : 0);
        dest.writeInt(mStayBookingDetail.readyForRefund ? 1 : 0);
        dest.writeInt(mStayBookingDetail.waitingForBooking ? 1 : 0);
        dest.writeString(mStayBookingDetail.cancelDateTime);
        dest.writeInt(mStayBookingDetail.rewardStickerCount);
    }

    private void readFromParcel(Parcel in)
    {
        mStayBookingDetail = new StayBookingDetail();
        mStayBookingDetail.reservationIndex = in.readInt();
        mStayBookingDetail.stayIndex = in.readInt();
        mStayBookingDetail.userIndex = in.readInt();
        mStayBookingDetail.userCouponIndex = in.readInt();
        mStayBookingDetail.regionProvinceIndex = in.readInt();
        mStayBookingDetail.regionDistrictName = in.readString();
        mStayBookingDetail.stayName = in.readString();
        mStayBookingDetail.stayAddress = in.readString();
        mStayBookingDetail.addressSummary = in.readString();
        mStayBookingDetail.phone1 = in.readString();
        mStayBookingDetail.phone2 = in.readString();
        mStayBookingDetail.phone3 = in.readString();

        String gradeName = in.readString();
        try
        {
            mStayBookingDetail.stayGrade = Stay.Grade.valueOf(gradeName);
        } catch (Exception e)
        {
            mStayBookingDetail.stayGrade = Stay.Grade.etc;
        }

        mStayBookingDetail.checkInDateTime = in.readString();
        mStayBookingDetail.checkOutDateTime = in.readString();
        mStayBookingDetail.latitude = in.readDouble();
        mStayBookingDetail.longitude = in.readDouble();
        mStayBookingDetail.guestEmail = in.readString();
        mStayBookingDetail.guestName = in.readString();
        mStayBookingDetail.guestPhone = in.readString();
        mStayBookingDetail.roomIndex = in.readInt();
        mStayBookingDetail.roomName = in.readString();
        mStayBookingDetail.guestTransportation = in.readString();
        mStayBookingDetail.refundStatus = in.readString();
        mStayBookingDetail.paymentDateTime = in.readString();
        mStayBookingDetail.priceTotal = in.readInt();
        mStayBookingDetail.discountTotal = in.readInt();
        mStayBookingDetail.bonusAmount = in.readInt();
        mStayBookingDetail.couponAmount = in.readInt();
        mStayBookingDetail.transactionType = in.readString();
        mStayBookingDetail.reviewStatusType = in.readString();
        mStayBookingDetail.refundType = in.readString();
        mStayBookingDetail.overseas = in.readInt() == 1 ? true : false;
        mStayBookingDetail.readyForRefund = in.readInt() == 1 ? true : false;
        mStayBookingDetail.waitingForBooking = in.readInt() == 1 ? true : false;
        mStayBookingDetail.cancelDateTime = in.readString();
        mStayBookingDetail.rewardStickerCount = in.readInt();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public StayBookingDetailParcel createFromParcel(Parcel in)
        {
            return new StayBookingDetailParcel(in);
        }

        @Override
        public StayBookingDetailParcel[] newArray(int size)
        {
            return new StayBookingDetailParcel[size];
        }
    };
}
