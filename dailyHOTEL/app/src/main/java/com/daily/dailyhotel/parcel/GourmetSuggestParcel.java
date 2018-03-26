package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.entity.GourmetSuggestV2;

/**
 * Created by android_sam on 2018. 3. 7..
 */

public class GourmetSuggestParcel implements Parcelable
{
    private GourmetSuggestV2 mGourmetSuggest;

    public GourmetSuggestParcel(@NonNull GourmetSuggestV2 gourmetSuggest)
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

    public GourmetSuggestV2 getSuggest()
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
        GourmetSuggestV2.MenuType menuType;
        GourmetSuggestV2.SuggestItem suggestItem;

        try
        {
            menuType = GourmetSuggestV2.MenuType.valueOf(in.readString());
        } catch (Exception e)
        {
            menuType = GourmetSuggestV2.MenuType.UNKNOWN;
        }

        suggestItem = (GourmetSuggestV2.SuggestItem) in.readSerializable();

        mGourmetSuggest = new GourmetSuggestV2(menuType, suggestItem);
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
