package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.entity.User;

public class UserParcel implements Parcelable
{
    private User mUser;

    public UserParcel(@NonNull User user)
    {
        if (user == null)
        {
            throw new NullPointerException("user == null");
        }

        mUser = user;
    }

    public UserParcel(Parcel in)
    {
        readFromParcel(in);
    }

    public User getUser()
    {
        return mUser;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(mUser.index);
        dest.writeString(mUser.email);
        dest.writeString(mUser.name);
        dest.writeString(mUser.phone);
        dest.writeInt(mUser.recommender);
        dest.writeString(mUser.referralCode);
        dest.writeString(mUser.verifiedAt);
        dest.writeString(mUser.phoneVerifiedAt);
        dest.writeString(mUser.userType);
        dest.writeString(mUser.birthday);
        dest.writeInt(mUser.verified == true ? 1 : 0);
        dest.writeInt(mUser.phoneVerified == true ? 1 : 0);
        dest.writeInt(mUser.agreedBenefit == true ? 1 : 0);
    }

    private void readFromParcel(Parcel in)
    {
        mUser = new User();

        mUser.index = in.readInt();
        mUser.email = in.readString();
        mUser.name = in.readString();
        mUser.phone = in.readString();
        mUser.recommender = in.readInt();
        mUser.referralCode = in.readString();
        mUser.verifiedAt = in.readString();
        mUser.phoneVerifiedAt = in.readString();
        mUser.userType = in.readString();
        mUser.birthday = in.readString();
        mUser.verified = in.readInt() == 1;
        mUser.phoneVerified = in.readInt() == 1;
        mUser.agreedBenefit = in.readInt() == 1;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public UserParcel createFromParcel(Parcel in)
        {
            return new UserParcel(in);
        }

        @Override
        public UserParcel[] newArray(int size)
        {
            return new UserParcel[size];
        }

    };
}
