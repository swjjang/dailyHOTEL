package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;

import com.daily.dailyhotel.entity.RefundPolicy;

/**
 * Created by android_sam on 2018. 1. 12..
 */

public class RefundPolicyParcel implements Parcelable
{
    private RefundPolicy mRefundPolicy;

    public RefundPolicyParcel(RefundPolicy refundPolicy)
    {
        if (refundPolicy == null)
        {
            throw new NullPointerException("refundPolicy == null");
        }

        this.mRefundPolicy = refundPolicy;
    }

    public RefundPolicyParcel(Parcel in)
    {
        readFromParcel(in);
    }

    public RefundPolicy getRefundPolicy()
    {
        return mRefundPolicy;
    }

    public String comment; // (string, optional),
    public boolean refundManual; // (boolean, optional),
    public String refundPolicy; // (string, optional) = ['NO_CHARGE_REFUND', 'SURCHARGE_REFUND', 'NRD', 'NONE']
    public String message; // 서버 메시지 - baseDto 에서 가져옴

    @Override
    public void writeToParcel(Parcel dest, int flag)
    {
        dest.writeString(mRefundPolicy.comment);
        dest.writeInt(mRefundPolicy.refundManual ? 1 : 0);
        dest.writeString(mRefundPolicy.refundPolicy);
        dest.writeString(mRefundPolicy.message);
    }

    private void readFromParcel(Parcel in)
    {
        mRefundPolicy = new RefundPolicy();
        mRefundPolicy.comment = in.readString();
        mRefundPolicy.refundManual = in.readInt() == 1;
        mRefundPolicy.refundPolicy = in.readString();
        mRefundPolicy.message = in.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public RefundPolicyParcel createFromParcel(Parcel in)
        {
            return new RefundPolicyParcel(in);
        }

        @Override
        public RefundPolicyParcel[] newArray(int size)
        {
            return new RefundPolicyParcel[size];
        }
    };
}
