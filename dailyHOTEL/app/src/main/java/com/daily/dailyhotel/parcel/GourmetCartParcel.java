package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.entity.GourmetCart;
import com.daily.dailyhotel.entity.GourmetCartMenu;
import com.twoheart.dailyhotel.util.DailyCalendar;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

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

        dest.writeString(mGourmetCart.gourmetCategory);
        dest.writeString(mGourmetCart.imageUrl);

        List<GourmetCartMenuParcel> gourmetCartMenuParcelList = new ArrayList<>();

        for (GourmetCartMenu gourmetCartMenu : mGourmetCart.getMenuList())
        {
            gourmetCartMenuParcelList.add(new GourmetCartMenuParcel(gourmetCartMenu));
        }

        dest.writeList(gourmetCartMenuParcelList);
    }

    private void readFromParcel(Parcel in)
    {
        mGourmetCart = new GourmetCart();

        mGourmetCart.visitTime = in.readInt();
        mGourmetCart.gourmetIndex = in.readInt();
        mGourmetCart.gourmetName = in.readString();
        mGourmetCart.setGourmetBookDateTime(in.readString());
        mGourmetCart.gourmetCategory = in.readString();
        mGourmetCart.imageUrl = in.readString();

        LinkedHashMap<Integer, GourmetCartMenu> linkedHashMap = new LinkedHashMap<>();

        List<GourmetCartMenuParcel> gourmetCartMenuParcelList = in.readArrayList(GourmetCartMenuParcel.class.getClassLoader());

        if (gourmetCartMenuParcelList != null)
        {
            for (GourmetCartMenuParcel gourmetCartMenuParcel : gourmetCartMenuParcelList)
            {
                GourmetCartMenu gourmetCartMenu = gourmetCartMenuParcel.getGourmetCartMenu();

                linkedHashMap.put(gourmetCartMenu.index, gourmetCartMenu);
            }
        }

        mGourmetCart.setMenuMap(linkedHashMap);
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
