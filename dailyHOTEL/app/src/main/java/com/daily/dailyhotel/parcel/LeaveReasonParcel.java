package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.entity.LeaveReason;

/**
 * Created by android_sam on 2018. 2. 20..
 */

public class LeaveReasonParcel implements Parcelable
{
    private LeaveReason mLeaveReason;

    public LeaveReasonParcel(@NonNull LeaveReason leaveReason)
    {
        if (leaveReason == null)
        {
            throw new NullPointerException("leaveReason == null");
        }

        mLeaveReason = leaveReason;
    }

    public LeaveReasonParcel(Parcel in)
    {
        readFromParcel(in);
    }

    public LeaveReason getLeaveReason()
    {
        return mLeaveReason;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(mLeaveReason.index);
        dest.writeString(mLeaveReason.reason);
    }

    private void readFromParcel(Parcel in)
    {
        mLeaveReason = new LeaveReason();

        mLeaveReason.index = in.readInt();
        mLeaveReason.reason = in.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public LeaveReasonParcel createFromParcel(Parcel in)
        {
            return new LeaveReasonParcel(in);
        }

        @Override
        public LeaveReasonParcel[] newArray(int size)
        {
            return new LeaveReasonParcel[size];
        }
    };
}
