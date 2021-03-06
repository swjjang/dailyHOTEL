package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.entity.DetailImageInformation;
import com.daily.dailyhotel.entity.GourmetMenu;

import java.util.ArrayList;
import java.util.List;

public class GourmetMenuParcel implements Parcelable
{
    private GourmetMenu mGourmetMenu;

    public GourmetMenuParcel(@NonNull GourmetMenu gourmetMenu)
    {
        if (gourmetMenu == null)
        {
            throw new NullPointerException("gourmetMenu == null");
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
        dest.writeInt(mGourmetMenu.persons);
        dest.writeInt(mGourmetMenu.minimumOrderQuantity);
        dest.writeInt(mGourmetMenu.maximumOrderQuantity);
        dest.writeInt(mGourmetMenu.availableTicketNumbers);
        dest.writeString(mGourmetMenu.startEatingTime);
        dest.writeString(mGourmetMenu.endEatingTime);
        dest.writeString(mGourmetMenu.readyTime);
        dest.writeString(mGourmetMenu.expiryTime);
        dest.writeInt(mGourmetMenu.timeInterval);
        dest.writeString(mGourmetMenu.baseImageUrl);

        List<DetailImageInformation> detailImageInformationList = mGourmetMenu.getImageList();
        List<DetailImageInformationParcel> detailImageInformationParcelList = new ArrayList<>();

        if (detailImageInformationList != null && detailImageInformationList.size() > 0)
        {
            for (DetailImageInformation detailImageInformation : detailImageInformationList)
            {
                detailImageInformationParcelList.add(new DetailImageInformationParcel(detailImageInformation));
            }
        }

        dest.writeTypedList(detailImageInformationParcelList);
        dest.writeStringList(mGourmetMenu.getMenuDetailList());
        dest.writeList(mGourmetMenu.getOperationTimeList());
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
        mGourmetMenu.persons = in.readInt();
        mGourmetMenu.minimumOrderQuantity = in.readInt();
        mGourmetMenu.maximumOrderQuantity = in.readInt();
        mGourmetMenu.availableTicketNumbers = in.readInt();
        mGourmetMenu.startEatingTime = in.readString();
        mGourmetMenu.endEatingTime = in.readString();
        mGourmetMenu.readyTime = in.readString();
        mGourmetMenu.expiryTime = in.readString();
        mGourmetMenu.timeInterval = in.readInt();
        mGourmetMenu.baseImageUrl = in.readString();

        List<DetailImageInformationParcel> detailImageInformationParcelList = in.createTypedArrayList(DetailImageInformationParcel.CREATOR);
        List<DetailImageInformation> detailImageInformationList = new ArrayList<>();

        for (DetailImageInformationParcel detailImageInformationParcel : detailImageInformationParcelList)
        {
            detailImageInformationList.add(detailImageInformationParcel.getDetailImageInformation());
        }

        mGourmetMenu.setImageList(detailImageInformationList);
        mGourmetMenu.setMenuDetailList(in.createStringArrayList());
        mGourmetMenu.setOperationTimeList(in.readArrayList(Integer.class.getClassLoader()));
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
