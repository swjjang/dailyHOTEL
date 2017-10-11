package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.entity.TrueVR;

public class TrueVRParcel implements Parcelable
{
    private TrueVR mTrueVR;

    public TrueVRParcel(@NonNull TrueVR trueVR)
    {
        if (trueVR == null)
        {
            throw new NullPointerException("trueVR == null");
        }

        mTrueVR = trueVR;
    }

    public TrueVRParcel(Parcel in)
    {
        readFromParcel(in);
    }

    public TrueVR getTrueVR()
    {
        return mTrueVR;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(mTrueVR.name);
        dest.writeString(mTrueVR.type);
        dest.writeInt(mTrueVR.typeIndex);
        dest.writeString(mTrueVR.url);
    }

    private void readFromParcel(Parcel in)
    {
        mTrueVR = new TrueVR();

        mTrueVR.name = in.readString();
        mTrueVR.type = in.readString();
        mTrueVR.typeIndex = in.readInt();
        mTrueVR.url = in.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public TrueVRParcel createFromParcel(Parcel in)
        {
            return new TrueVRParcel(in);
        }

        @Override
        public TrueVRParcel[] newArray(int size)
        {
            return new TrueVRParcel[size];
        }

    };
}
