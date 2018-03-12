package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.GourmetSuggestV2;

/**
 * Created by android_sam on 2018. 3. 7..
 */

public class GourmetSuggestParcelV2 implements Parcelable
{
    private GourmetSuggestV2 mGourmetSuggest;

    public GourmetSuggestParcelV2(@NonNull GourmetSuggestV2 gourmetSuggest)
    {
        if (gourmetSuggest == null)
        {
            throw new NullPointerException("gourmetSuggest == null");
        }

        mGourmetSuggest = gourmetSuggest;
    }

    public GourmetSuggestParcelV2(Parcel in)
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
        dest.writeInt(mGourmetSuggest.menuType);

        GourmetSuggestV2.SuggestItem item = mGourmetSuggest.suggestItem;
        if (item == null)
        {
            return;
        }

        if (item instanceof GourmetSuggestV2.Gourmet)
        {
            GourmetSuggestV2.Gourmet gourmet = (GourmetSuggestV2.Gourmet) item;
            dest.writeSerializable(gourmet);
        } else if (item instanceof GourmetSuggestV2.Province)
        {
            GourmetSuggestV2.Province province = (GourmetSuggestV2.Province) item;
            dest.writeSerializable(province);
        } else if (item instanceof GourmetSuggestV2.Direct)
        {
            GourmetSuggestV2.Direct direct = (GourmetSuggestV2.Direct) item;
            dest.writeSerializable(direct);
        } else if (item instanceof GourmetSuggestV2.Location)
        {
            GourmetSuggestV2.Location location = (GourmetSuggestV2.Location) item;
            dest.writeSerializable(location);
        } else {
            dest.writeSerializable(item);
            ExLog.e("sam : writeToParcel error suggestItem name : " + item.name);
        }
    }

    private void readFromParcel(Parcel in)
    {
        mGourmetSuggest = new GourmetSuggestV2();

        mGourmetSuggest.menuType = in.readInt();

        mGourmetSuggest.suggestItem = (GourmetSuggestV2.SuggestItem) in.readSerializable();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public GourmetSuggestParcelV2 createFromParcel(Parcel in)
        {
            return new GourmetSuggestParcelV2(in);
        }

        @Override
        public GourmetSuggestParcelV2[] newArray(int size)
        {
            return new GourmetSuggestParcelV2[size];
        }

    };
}
