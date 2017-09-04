package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.entity.GourmetMenu;
import com.daily.dailyhotel.entity.ImageInformation;

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
        dest.writeInt(mGourmetMenu.saleIndex);
        dest.writeString(mGourmetMenu.name);
        dest.writeInt(mGourmetMenu.price);
        dest.writeInt(mGourmetMenu.discountPrice);
        dest.writeString(mGourmetMenu.menuBenefit);
        dest.writeString(mGourmetMenu.needToKnow);
        dest.writeString(mGourmetMenu.reserveCondition);
        dest.writeString(mGourmetMenu.openTime);
        dest.writeString(mGourmetMenu.closeTime);
        dest.writeString(mGourmetMenu.lastOrderTime);
        dest.writeString(mGourmetMenu.menuSummary);
        dest.writeInt(mGourmetMenu.getPrimaryImageIndex());

        List<ImageInformation> imageInformationList = mGourmetMenu.getImageList();
        List<ImageInformationParcel> imageInformationParcelList = new ArrayList<>();

        if (imageInformationList != null && imageInformationList.size() > 0)
        {
            for (ImageInformation imageInformation : imageInformationList)
            {
                imageInformationParcelList.add(new ImageInformationParcel(imageInformation));
            }
        }

        dest.writeTypedList(imageInformationParcelList);
        dest.writeStringList(mGourmetMenu.getMenuDetailList());

    }

    private void readFromParcel(Parcel in)
    {
        mGourmetMenu = new GourmetMenu();

        mGourmetMenu.index = in.readInt();
        mGourmetMenu.saleIndex = in.readInt();
        mGourmetMenu.name = in.readString();
        mGourmetMenu.price = in.readInt();
        mGourmetMenu.discountPrice = in.readInt();
        mGourmetMenu.menuBenefit = in.readString();
        mGourmetMenu.needToKnow = in.readString();
        mGourmetMenu.reserveCondition = in.readString();
        mGourmetMenu.openTime = in.readString();
        mGourmetMenu.closeTime = in.readString();
        mGourmetMenu.lastOrderTime = in.readString();
        mGourmetMenu.menuSummary = in.readString();
        mGourmetMenu.setPrimaryImageIndex(in.readInt());

        List<ImageInformationParcel> imageInformationParcelList = in.createTypedArrayList(ImageInformationParcel.CREATOR);
        List<ImageInformation> imageInformationList = new ArrayList<>();

        for (ImageInformationParcel imageInformationParcel : imageInformationParcelList)
        {
            imageInformationList.add(imageInformationParcel.getGourmetMenuImage());
        }

        mGourmetMenu.setImageList(imageInformationList);
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
