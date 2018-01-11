package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;

import com.daily.dailyhotel.entity.Bank;

/**
 * Created by android_sam on 2018. 1. 11..
 */

public class BankParcel implements Parcelable
{
    private Bank mBank;

    public BankParcel(Bank bank)
    {
        if (bank == null)
        {
            throw new NullPointerException("bank == null");
        }

        this.mBank = bank;
    }

    public BankParcel(Parcel in)
    {
        readFromParcel(in);
    }

    public Bank getBank()
    {
        return mBank;
    }

    @Override
    public void writeToParcel(Parcel dest, int flag)
    {
        dest.writeString(mBank.code);
        dest.writeString(mBank.name);
    }

    private void readFromParcel(Parcel in)
    {
        mBank = new Bank();
        mBank.code = in.readString();
        mBank.name = in.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public BankParcel createFromParcel(Parcel in)
        {
            return new BankParcel(in);
        }

        @Override
        public BankParcel[] newArray(int size)
        {
            return new BankParcel[size];
        }
    };
}
