package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.entity.Suggest;

public class SuggestParcel implements Parcelable
{
    private Suggest mSuggest;

    public SuggestParcel(@NonNull Suggest suggest)
    {
        if (suggest == null)
        {
            throw new NullPointerException("suggest == null");
        }

        mSuggest = suggest;
    }

    public SuggestParcel(Parcel in)
    {
        readFromParcel(in);
    }

    public Suggest getSuggest()
    {
        return mSuggest;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeLong(mSuggest.id);
        dest.writeString(mSuggest.name);
        dest.writeString(mSuggest.city);
        dest.writeString(mSuggest.country);
        dest.writeString(mSuggest.countryCode);
        dest.writeString(mSuggest.categoryKey);
        dest.writeString(mSuggest.display);
        dest.writeDouble(mSuggest.latitude);
        dest.writeDouble(mSuggest.longitude);
    }

    private void readFromParcel(Parcel in)
    {
        mSuggest = new Suggest();

        mSuggest.id = in.readLong();
        mSuggest.name = in.readString();
        mSuggest.city = in.readString();
        mSuggest.country = in.readString();
        mSuggest.countryCode = in.readString();
        mSuggest.categoryKey = in.readString();
        mSuggest.display = in.readString();
        mSuggest.latitude = in.readDouble();
        mSuggest.longitude = in.readDouble();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public SuggestParcel createFromParcel(Parcel in)
        {
            return new SuggestParcel(in);
        }

        @Override
        public SuggestParcel[] newArray(int size)
        {
            return new SuggestParcel[size];
        }

    };
}
