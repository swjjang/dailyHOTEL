package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.entity.GourmetMenu;
import com.daily.dailyhotel.entity.GourmetMenuImage;

import java.util.ArrayList;
import java.util.List;

public class GourmetMenuParcel implements Parcelable
{
    private GourmetMenu mGourmetMenu;

    public GourmetMenuParcel(@NonNull GourmetMenu gourmetMenu)
    {
        if (gourmetMenu == null)
        {
            throw new NullPointerException("reservation == null");
        }

        mGourmetMenu = gourmetMenu;
    }

    public GourmetMenuParcel(Parcel in)
    {
        readFromParcel(in);
    }

    public GourmetMenu getGourmetMenu()
    {
        return mGourmetMenu;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(mGourmetMenu.index);
        dest.writeInt(mGourmetMenu.saleIdx);
        dest.writeString(mGourmetMenu.ticketName);
        dest.writeInt(mGourmetMenu.price);
        dest.writeInt(mGourmetMenu.discountPrice);
        dest.writeString(mGourmetMenu.menuBenefit);
        dest.writeString(mGourmetMenu.needToKnow);
        dest.writeString(mGourmetMenu.openTime);
        dest.writeString(mGourmetMenu.closeTime);
        dest.writeString(mGourmetMenu.lastOrderTime);
        dest.writeString(mGourmetMenu.menuSummary);

        List<GourmetMenuImage> gourmetMenuImageList = mGourmetMenu.getImageList();
        List<GourmetMenuImageParcel> gourmetMenuImageParcelList = new ArrayList<>();

        if (gourmetMenuImageList != null && gourmetMenuImageList.size() > 0)
        {
            for (GourmetMenuImage gourmetMenuImage : gourmetMenuImageList)
            {
                gourmetMenuImageParcelList.add(new GourmetMenuImageParcel(gourmetMenuImage));
            }
        }

        dest.writeTypedList(gourmetMenuImageParcelList);
        dest.writeStringList(mGourmetMenu.getMenuDetailList());

    }

    private void readFromParcel(Parcel in)
    {
        mGourmetMenu = new GourmetMenu();

        mGourmetMenu.index = in.readInt();
        mGourmetMenu.saleIdx = in.readInt();
        mGourmetMenu.ticketName = in.readString();
        mGourmetMenu.price = in.readInt();
        mGourmetMenu.discountPrice = in.readInt();
        mGourmetMenu.menuBenefit = in.readString();
        mGourmetMenu.needToKnow = in.readString();
        mGourmetMenu.openTime = in.readString();
        mGourmetMenu.closeTime = in.readString();
        mGourmetMenu.lastOrderTime = in.readString();
        mGourmetMenu.menuSummary = in.readString();

        List<GourmetMenuImageParcel> gourmetMenuImageParcelList = in.createTypedArrayList(GourmetMenuImageParcel.CREATOR);
        List<GourmetMenuImage> gourmetMenuImageList = new ArrayList<>();

        for (GourmetMenuImageParcel gourmetMenuImageParcel : gourmetMenuImageParcelList)
        {
            gourmetMenuImageList.add(gourmetMenuImageParcel.getGourmetMenuImage());
        }

        mGourmetMenu.setImageList(gourmetMenuImageList);
        mGourmetMenu.setMenuDetailList(in.createStringArrayList());
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public GourmetMenuParcel createFromParcel(Parcel in)
        {
            return new GourmetMenuParcel(in);
        }

        @Override
        public GourmetMenuParcel[] newArray(int size)
        {
            return new GourmetMenuParcel[size];
        }
    };
}
