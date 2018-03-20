package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.entity.StayOutboundSuggest;

public class StayOutboundSuggestParcel implements Parcelable
{
    private StayOutboundSuggest mStayOutboundSuggest;

    public StayOutboundSuggestParcel(@NonNull StayOutboundSuggest stayOutboundSuggest)
    {
        if (stayOutboundSuggest == null)
        {
            throw new NullPointerException("stayOutboundSuggest == null");
        }

        mStayOutboundSuggest = stayOutboundSuggest;
    }

    public StayOutboundSuggestParcel(Parcel in)
    {
        readFromParcel(in);
    }

    public StayOutboundSuggest getSuggest()
    {
        return mStayOutboundSuggest;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeLong(mStayOutboundSuggest.id);
        dest.writeString(mStayOutboundSuggest.name);
        dest.writeString(mStayOutboundSuggest.city);
        dest.writeString(mStayOutboundSuggest.country);
        dest.writeString(mStayOutboundSuggest.countryCode);
        dest.writeString(mStayOutboundSuggest.categoryKey);
        dest.writeString(mStayOutboundSuggest.display);
        dest.writeString(mStayOutboundSuggest.displayText);
        dest.writeDouble(mStayOutboundSuggest.latitude);
        dest.writeDouble(mStayOutboundSuggest.longitude);
        dest.writeInt(mStayOutboundSuggest.menuType);
    }

    private void readFromParcel(Parcel in)
    {
        mStayOutboundSuggest = new StayOutboundSuggest();

        mStayOutboundSuggest.id = in.readLong();
        mStayOutboundSuggest.name = in.readString();
        mStayOutboundSuggest.city = in.readString();
        mStayOutboundSuggest.country = in.readString();
        mStayOutboundSuggest.countryCode = in.readString();
        mStayOutboundSuggest.categoryKey = in.readString();
        mStayOutboundSuggest.display = in.readString();
        mStayOutboundSuggest.displayText = in.readString();
        mStayOutboundSuggest.latitude = in.readDouble();
        mStayOutboundSuggest.longitude = in.readDouble();
        mStayOutboundSuggest.menuType = in.readInt();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public StayOutboundSuggestParcel createFromParcel(Parcel in)
        {
            return new StayOutboundSuggestParcel(in);
        }

        @Override
        public StayOutboundSuggestParcel[] newArray(int size)
        {
            return new StayOutboundSuggestParcel[size];
        }

    };
}
