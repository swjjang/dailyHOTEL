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
//
//            GourmetSuggestV2.Province province = gourmet.province;
//
//            dest.writeString(GourmetSuggestV2.Gourmet.class.getSimpleName());
//            dest.writeString(gourmet.name);
//            dest.writeInt(gourmet.index);
//
//            dest.writeInt(province == null ? 0 : province.index);
//            dest.writeString(province == null ? null : province.name);
        } else if (item instanceof GourmetSuggestV2.Province)
        {
            GourmetSuggestV2.Province province = (GourmetSuggestV2.Province) item;
            dest.writeSerializable(province);
//            GourmetSuggestV2.Area area = province.area;
//
//            dest.writeString(GourmetSuggestV2.Province.class.getSimpleName());
//            dest.writeInt(province.index);
//            dest.writeString(province.name);
//            dest.writeInt(area == null ? 0 : area.index);
//            dest.writeString(area == null ? null : area.name);
        } else if (item instanceof GourmetSuggestV2.Direct)
        {
            GourmetSuggestV2.Direct direct = (GourmetSuggestV2.Direct) item;
            dest.writeSerializable(direct);

//            dest.writeString(GourmetSuggestV2.Direct.class.getSimpleName());
//            dest.writeString(item.name);
        } else if (item instanceof GourmetSuggestV2.Location)
        {
            GourmetSuggestV2.Location location = (GourmetSuggestV2.Location) item;
            dest.writeSerializable(location);

//            dest.writeString(GourmetSuggestV2.Location.class.getSimpleName());
//            dest.writeString(location.name);
//            dest.writeString(location.address);
//            dest.writeDouble(location.latitude);
//            dest.writeDouble(location.longitude);
        } else {
            dest.writeSerializable(item);
//            dest.writeString(GourmetSuggestV2.SuggestItem.class.getSimpleName());
            ExLog.e("sam : writeToParcel error suggestItem name : " + item.name);
        }
    }

    private void readFromParcel(Parcel in)
    {
        mGourmetSuggest = new GourmetSuggestV2();

        mGourmetSuggest.menuType = in.readInt();

        mGourmetSuggest.suggestItem = (GourmetSuggestV2.SuggestItem) in.readSerializable();

        ExLog.d("sam : " + mGourmetSuggest.suggestItem.name);

        GourmetSuggestV2.SuggestItem item = mGourmetSuggest.suggestItem;
        if (item == null)
        {
            return;
        }

        if (item instanceof GourmetSuggestV2.Gourmet)
        {
            GourmetSuggestV2.Gourmet gourmet = (GourmetSuggestV2.Gourmet) item;
            GourmetSuggestV2.Province province = gourmet.province;

            ExLog.e("sam : " + gourmet.name + " , " + gourmet.index + " , " + province.index + province.name);
        } else if (item instanceof GourmetSuggestV2.Province)
        {
            GourmetSuggestV2.Province province = (GourmetSuggestV2.Province) item;
            ExLog.e("sam : " + province.name);
        } else if (item instanceof GourmetSuggestV2.Direct)
        {
            GourmetSuggestV2.Direct direct = (GourmetSuggestV2.Direct) item;
            ExLog.e("sam : " + direct.name);
        } else if (item instanceof GourmetSuggestV2.Location)
        {
            GourmetSuggestV2.Location location = (GourmetSuggestV2.Location) item;
            ExLog.e("sam : " + item.name + " , "  + location.address + " , " + location.latitude + " , " + location.longitude);
        } else {
            ExLog.e("sam : readFromParcel error suggestItem name : " + item.name);
        }
//
//        String className = in.readString();
//
//        if (GourmetSuggestV2.Gourmet.class.getSimpleName().equalsIgnoreCase(className))
//        {
//            GourmetSuggestV2.Gourmet gourmet = new GourmetSuggestV2.Gourmet();
//
//
//        } else if (GourmetSuggestV2.Province.class.getSimpleName().equalsIgnoreCase(className))
//        {
//            GourmetSuggestV2.Province province = new GourmetSuggestV2.Province();
//
//
//        } else if (GourmetSuggestV2.Direct.class.getSimpleName().equalsIgnoreCase(className))
//        {
//            GourmetSuggestV2.Direct direct = new GourmetSuggestV2.Direct(in.readString());
//            mGourmetSuggest.suggestItem = direct;
//        } else if (GourmetSuggestV2.Location.class.getSimpleName().equalsIgnoreCase(className))
//        {
//            GourmetSuggestV2.Location location = new GourmetSuggestV2.Location();
//
//        }
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
