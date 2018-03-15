package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.entity.StaySuggestV2;

/**
 * Created by android_sam on 2018. 3. 12..
 */

public class StaySuggestParcelV2 implements Parcelable
{
    private StaySuggestV2 mStaySuggest;

    public StaySuggestParcelV2(@NonNull StaySuggestV2 staySuggest)
    {
        if (staySuggest == null)
        {
            throw new NullPointerException("staySuggest == null");
        }

        mStaySuggest = staySuggest;
    }

    public StaySuggestParcelV2(Parcel in)
    {
        readFromParcel(in);
    }

    public StaySuggestV2 getSuggest()
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
        StaySuggestV2.MenuType menuType;
        StaySuggestV2.SuggestItem suggestItem;

        try
        {
            menuType = StaySuggestV2.MenuType.valueOf(in.readString());
        } catch (Exception e)
        {
            menuType = StaySuggestV2.MenuType.UNKNOWN;
        }

        suggestItem = (StaySuggestV2.SuggestItem) in.readSerializable();

        mStaySuggest = new StaySuggestV2(menuType, suggestItem);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public StaySuggestParcelV2 createFromParcel(Parcel in)
        {
            return new StaySuggestParcelV2(in);
        }

        @Override
        public StaySuggestParcelV2[] newArray(int size)
        {
            return new StaySuggestParcelV2[size];
        }

    };
}
