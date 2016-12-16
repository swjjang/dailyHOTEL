package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by android_sam on 2016. 12. 14..
 */

public class User extends Customer
{
    private String mType;
    private String mBirthDay;
    private String mRecommender;
    private String mPassword;

    public User()
    {
        super();
    }

    public User(Parcel in)
    {
//        super(in);
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);

        dest.writeString(mType);
        dest.writeString(mBirthDay);
        dest.writeString(mRecommender);
        dest.writeString(mPassword);
    }

    private void readFromParcel(Parcel in)
    {
        mType = in.readString();
        mBirthDay = in.readString();
        mRecommender = in.readString();
        mPassword = in.readString();
    }

    public String getType()
    {
        return mType;
    }

    public void setType(String userType)
    {
        this.mType = userType;
    }

    public String getBirthDay()
    {
        return mBirthDay;
    }

    public void setBirthDay(String birthDay)
    {
        this.mBirthDay = birthDay;
    }

    public String getRecommender()
    {
        return mRecommender;
    }

    public void setRecommender(String recommender)
    {
        this.mRecommender = recommender;
    }

    public String getPassword()
    {
        return mPassword;
    }

    public void setPassword(String password)
    {
        this.mPassword = password;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public Customer createFromParcel(Parcel in)
        {
            return new Customer(in);
        }

        @Override
        public Customer[] newArray(int size)
        {
            return new Customer[size];
        }

    };
}
