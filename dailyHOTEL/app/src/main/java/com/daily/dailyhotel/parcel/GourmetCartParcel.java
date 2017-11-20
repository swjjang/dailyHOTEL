package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.entity.GourmetCart;
import com.twoheart.dailyhotel.util.DailyCalendar;

import java.util.LinkedHashMap;

public class GourmetCartParcel implements Parcelable
{
    private GourmetCart mGourmetCart;

    public GourmetCartParcel(@NonNull GourmetCart gourmetCart)
    {
        if (gourmetCart == null)
        {
            throw new NullPointerException("gourmetCart == null");
        }

        mGourmetCart = gourmetCart;
    }

    public GourmetCartParcel(Parcel in)
    {
        readFromParcel(in);
    }

    public GourmetCart getGourmetCart()
    {
        return mGourmetCart;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(mGourmetCart.visitTime);
        dest.writeInt(mGourmetCart.gourmetIndex);
        dest.writeString(mGourmetCart.gourmetName);

        if (mGourmetCart.getGourmetBookDateTime() != null)
        {
            dest.writeString(mGourmetCart.getGourmetBookDateTime().getVisitDateTime(DailyCalendar.ISO_8601_FORMAT));
        } else
        {
            dest.writeString(null);
        }

        dest.writeSerializable(mGourmetCart.getMenuMap());
        dest.writeString(mGourmetCart.gourmetCategory);
        dest.writeString(mGourmetCart.imageUrl);
    }

    private void readFromParcel(Parcel in)
    {
        mGourmetCart = new GourmetCart();

        mGourmetCart.visitTime = in.readInt();
        mGourmetCart.gourmetIndex = in.readInt();
        mGourmetCart.gourmetName = in.readString();
        mGourmetCart.setGourmetBookDateTime(in.readString());
        mGourmetCart.setMenuMap((LinkedHashMap) in.readSerializable());
        mGourmetCart.gourmetCategory = in.readString();
        mGourmetCart.imageUrl = in.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public GourmetCartParcel createFromParcel(Parcel in)
        {
            return new GourmetCartParcel(in);
        }

        @Override
        public GourmetCartParcel[] newArray(int size)
        {
            return new GourmetCartParcel[size];
        }
    };
}
