package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.entity.GourmetSuggest;

/**
 * Created by android_sam on 2018. 2. 1..
 */

public class GourmetSuggestParcel implements Parcelable
{
    private GourmetSuggest mGourmetSuggest;

    public GourmetSuggestParcel(@NonNull GourmetSuggest gourmetSuggest)
    {
        if (gourmetSuggest == null)
        {
            throw new NullPointerException("gourmetSuggest == null");
        }

        mGourmetSuggest = gourmetSuggest;
    }

    public GourmetSuggestParcel(Parcel in)
    {
        readFromParcel(in);
    }

    public GourmetSuggest getSuggest()
    {
        return mGourmetSuggest;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(mGourmetSuggest.gourmetIndex);
        dest.writeString(mGourmetSuggest.gourmetName);
        dest.writeString(mGourmetSuggest.regionName);
        dest.writeString(mGourmetSuggest.provinceName);
        dest.writeString(mGourmetSuggest.displayName);
        dest.writeString(mGourmetSuggest.address);
        dest.writeInt(mGourmetSuggest.discountPrice);
        dest.writeInt(mGourmetSuggest.availableTickets);
        dest.writeInt(mGourmetSuggest.isExpired ? 1 : 0);
        dest.writeInt(mGourmetSuggest.minimumOrderQuantity);
        dest.writeDouble(mGourmetSuggest.latitude);
        dest.writeDouble(mGourmetSuggest.longitude);
        dest.writeString(mGourmetSuggest.categoryKey);
        dest.writeInt(mGourmetSuggest.menuType);
    }

    private void readFromParcel(Parcel in)
    {
        mGourmetSuggest = new GourmetSuggest();

        mGourmetSuggest.gourmetIndex = in.readInt();
        mGourmetSuggest.gourmetName = in.readString();
        mGourmetSuggest.regionName = in.readString();
        mGourmetSuggest.provinceName = in.readString();
        mGourmetSuggest.displayName = in.readString();
        mGourmetSuggest.address = in.readString();
        mGourmetSuggest.discountPrice = in.readInt();
        mGourmetSuggest.availableTickets = in.readInt();
        mGourmetSuggest.isExpired = in.readInt() == 1;
        mGourmetSuggest.minimumOrderQuantity = in.readInt();
        mGourmetSuggest.latitude = in.readDouble();
        mGourmetSuggest.longitude = in.readDouble();
        mGourmetSuggest.categoryKey = in.readString();
        mGourmetSuggest.menuType = in.readInt();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public GourmetSuggestParcel createFromParcel(Parcel in)
        {
            return new GourmetSuggestParcel(in);
        }

        @Override
        public GourmetSuggestParcel[] newArray(int size)
        {
            return new GourmetSuggestParcel[size];
        }

    };
}
