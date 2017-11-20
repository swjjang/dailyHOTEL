package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.entity.GourmetCartMenu;

public class GourmetCartMenuParcel implements Parcelable
{
    private GourmetCartMenu mGourmetCartMenu;

    public GourmetCartMenuParcel(@NonNull GourmetCartMenu gourmetCartMenu)
    {
        if (gourmetCartMenu == null)
        {
            throw new NullPointerException("gourmetCartMenu == null");
        }

        mGourmetCartMenu = gourmetCartMenu;
    }

    public GourmetCartMenuParcel(Parcel in)
    {
        readFromParcel(in);
    }

    public GourmetCartMenu getGourmetCartMenu()
    {
        return mGourmetCartMenu;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(mGourmetCartMenu.index);
        dest.writeInt(mGourmetCartMenu.saleIndex);
        dest.writeInt(mGourmetCartMenu.count);
        dest.writeInt(mGourmetCartMenu.price);
        dest.writeInt(mGourmetCartMenu.discountPrice);
        dest.writeString(mGourmetCartMenu.name);
        dest.writeInt(mGourmetCartMenu.persons);

        dest.writeInt(mGourmetCartMenu.minimumOrderQuantity);
        dest.writeInt(mGourmetCartMenu.maximumOrderQuantity);
        dest.writeInt(mGourmetCartMenu.saleOrderQuantity);
    }

    private void readFromParcel(Parcel in)
    {
        mGourmetCartMenu = new GourmetCartMenu();

        mGourmetCartMenu.index = in.readInt();
        mGourmetCartMenu.saleIndex = in.readInt();
        mGourmetCartMenu.count = in.readInt();
        mGourmetCartMenu.price = in.readInt();
        mGourmetCartMenu.discountPrice = in.readInt();
        mGourmetCartMenu.name = in.readString();
        mGourmetCartMenu.persons = in.readInt();

        mGourmetCartMenu.minimumOrderQuantity = in.readInt();
        mGourmetCartMenu.maximumOrderQuantity = in.readInt();
        mGourmetCartMenu.saleOrderQuantity = in.readInt();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public GourmetCartMenuParcel createFromParcel(Parcel in)
        {
            return new GourmetCartMenuParcel(in);
        }

        @Override
        public GourmetCartMenuParcel[] newArray(int size)
        {
            return new GourmetCartMenuParcel[size];
        }
    };
}
