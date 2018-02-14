package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;

import com.daily.dailyhotel.entity.Coupon;

/**
 * Created by android_sam on 2018. 1. 11..
 */

public class CouponParcel implements Parcelable
{
    private Coupon mCoupon;

    public CouponParcel(Coupon coupon)
    {
        if (coupon == null)
        {
            throw new NullPointerException("coupon == null");
        }

        this.mCoupon = coupon;
    }

    public CouponParcel(Parcel in)
    {
        readFromParcel(in);
    }

    public Coupon getCoupon()
    {
        return mCoupon;
    }

    @Override
    public void writeToParcel(Parcel dest, int flag)
    {
        dest.writeInt(mCoupon.amount);
        dest.writeString(mCoupon.title);
        dest.writeString(mCoupon.validFrom);
        dest.writeString(mCoupon.validTo);
        dest.writeInt(mCoupon.amountMinimum);
        dest.writeInt(mCoupon.isDownloaded ? 1 : 0);
        dest.writeString(mCoupon.availableItem);
        dest.writeString(mCoupon.serverDate);
        dest.writeString(mCoupon.couponCode);
        dest.writeString(mCoupon.userCouponIndex);
        dest.writeString(mCoupon.stayFrom);
        dest.writeString(mCoupon.stayTo);
        dest.writeString(mCoupon.downloadedAt);
        dest.writeString(mCoupon.description);
        dest.writeInt(mCoupon.availableInDomestic ? 1 : 0);
        dest.writeInt(mCoupon.availableInOverseas ? 1 : 0);
        dest.writeInt(mCoupon.availableInStay ? 1 : 0);
        dest.writeInt(mCoupon.availableInGourmet ? 1 : 0);
        dest.writeInt(mCoupon.isRedeemed ? 1 : 0);
        dest.writeInt(mCoupon.isExpired ? 1 : 0);
        dest.writeString(mCoupon.type.name());
    }

    private void readFromParcel(Parcel in)
    {
        mCoupon = new Coupon();

        mCoupon.amount = in.readInt();
        mCoupon.title = in.readString();
        mCoupon.validFrom = in.readString();
        mCoupon.validTo = in.readString();
        mCoupon.amountMinimum = in.readInt();
        mCoupon.isDownloaded = in.readInt() == 1;
        mCoupon.availableItem = in.readString();
        mCoupon.serverDate = in.readString();
        mCoupon.couponCode = in.readString();
        mCoupon.userCouponIndex = in.readString();
        mCoupon.stayFrom = in.readString();
        mCoupon.stayTo = in.readString();
        mCoupon.downloadedAt = in.readString();
        mCoupon.description = in.readString();
        mCoupon.availableInDomestic = in.readInt() == 1;
        mCoupon.availableInOverseas = in.readInt() == 1;
        mCoupon.availableInStay = in.readInt() == 1;
        mCoupon.availableInGourmet = in.readInt() == 1;
        mCoupon.isRedeemed = in.readInt() == 1;
        mCoupon.isExpired = in.readInt() == 1;
        mCoupon.type = Coupon.Type.valueOf(in.readString());
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public CouponParcel createFromParcel(Parcel in)
        {
            return new CouponParcel(in);
        }

        @Override
        public CouponParcel[] newArray(int size)
        {
            return new CouponParcel[size];
        }
    };
}
