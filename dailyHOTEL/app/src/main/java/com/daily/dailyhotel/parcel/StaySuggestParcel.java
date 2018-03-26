package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.entity.StaySuggest;

/**
 * Created by android_sam on 2018. 3. 12..
 */

public class StaySuggestParcel implements Parcelable
{
    private StaySuggest mStaySuggest;

    public StaySuggestParcel(@NonNull StaySuggest staySuggest)
    {
        if (staySuggest == null)
        {
            throw new NullPointerException("staySuggest == null");
        }

        mStaySuggest = staySuggest;
    }

    public StaySuggestParcel(Parcel in)
    {
        readFromParcel(in);
    }

    public StaySuggest getSuggest()
    {
        return mStaySuggest;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(mStaySuggest.menuType.name());
        dest.writeSerializable(mStaySuggest.getSuggestItem());
    }

    private void readFromParcel(Parcel in)
    {
        StaySuggest.MenuType menuType;
        StaySuggest.SuggestItem suggestItem;

        try
        {
            menuType = StaySuggest.MenuType.valueOf(in.readString());
        } catch (Exception e)
        {
            menuType = StaySuggest.MenuType.UNKNOWN;
        }

        suggestItem = (StaySuggest.SuggestItem) in.readSerializable();

        mStaySuggest = new StaySuggest(menuType, suggestItem);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public StaySuggestParcel createFromParcel(Parcel in)
        {
            return new StaySuggestParcel(in);
        }

        @Override
        public StaySuggestParcel[] newArray(int size)
        {
            return new StaySuggestParcel[size];
        }

    };
}
