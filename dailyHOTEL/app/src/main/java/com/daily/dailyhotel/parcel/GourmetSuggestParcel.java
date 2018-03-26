package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.entity.GourmetSuggest;

/**
 * Created by android_sam on 2018. 3. 7..
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
        dest.writeString(mGourmetSuggest.menuType.name());
        dest.writeSerializable(mGourmetSuggest.getSuggestItem());
    }

    private void readFromParcel(Parcel in)
    {
        GourmetSuggest.MenuType menuType;
        GourmetSuggest.SuggestItem suggestItem;

        try
        {
            menuType = GourmetSuggest.MenuType.valueOf(in.readString());
        } catch (Exception e)
        {
            menuType = GourmetSuggest.MenuType.UNKNOWN;
        }

        suggestItem = (GourmetSuggest.SuggestItem) in.readSerializable();

        mGourmetSuggest = new GourmetSuggest(menuType, suggestItem);
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
