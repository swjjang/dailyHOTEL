package com.twoheart.dailyhotel.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.model.GourmetDetailParams;
import com.twoheart.dailyhotel.network.model.GourmetProduct;
import com.twoheart.dailyhotel.network.model.ImageInformation;

import java.util.List;

public class GourmetDetail extends PlaceDetail<GourmetProduct> implements Parcelable
{
    private GourmetDetailParams mGourmetDetailParams;

    public boolean hasCoupon;

    public GourmetDetail(int index, int entryPosition, String isShowOriginalPrice, int listCount, boolean isDailyChoice)
    {
        this.index = index;
        this.entryPosition = entryPosition;
        this.isShowOriginalPrice = isShowOriginalPrice;
        this.listCount = listCount;
        this.isDailyChoice = isDailyChoice;
    }

    public GourmetDetail(Parcel in)
    {
        readFromParcel(in);
    }

    public GourmetDetailParams getGourmetDetailParmas()
    {
        return mGourmetDetailParams;
    }

    public void setGourmetDetailParmas(GourmetDetailParams gourmetDetailParmas)
    {
        mGourmetDetailParams = gourmetDetailParmas;
    }

    @Override
    public List<GourmetProduct> getProductList()
    {
        if (mGourmetDetailParams == null)
        {
            return null;
        }

        return mGourmetDetailParams.getProductList();
    }

    @Override
    public GourmetProduct getProduct(int index)
    {
        if (mGourmetDetailParams == null || index < 0)
        {
            return null;
        }

        List<GourmetProduct> gourmetProductList = mGourmetDetailParams.getProductList();

        if (gourmetProductList == null || gourmetProductList.size() <= index)
        {
            return null;
        }

        return gourmetProductList.get(index);
    }

    @Override
    public List<Pictogram> getPictogramList()
    {
        if (mGourmetDetailParams == null)
        {
            return null;
        }

        return mGourmetDetailParams.getPictogramList();
    }

    @Override
    public List<ImageInformation> getImageList()
    {
        if (mGourmetDetailParams == null)
        {
            return null;
        }

        return mGourmetDetailParams.getImageList();
    }

    @Override
    public List<DetailInformation> getDetailList()
    {
        if (mGourmetDetailParams == null)
        {
            return null;
        }

        return mGourmetDetailParams.getDetailList();
    }

    @Override
    public List<String> getBenefitList()
    {
        if (mGourmetDetailParams == null)
        {
            return null;
        }

        return mGourmetDetailParams.getBenefitList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeParcelable(mGourmetDetailParams, flags);
        dest.writeInt(hasCoupon == true ? 1 : 0);
        dest.writeInt(index);
        dest.writeInt(listCount);
        dest.writeInt(entryPosition);
        dest.writeString(isShowOriginalPrice);
        dest.writeInt(isDailyChoice == true ? 1 : 0);
    }

    protected void readFromParcel(Parcel in)
    {
        mGourmetDetailParams = in.readParcelable(GourmetDetailParams.class.getClassLoader());
        hasCoupon = in.readInt() == 1;
        index = in.readInt();
        listCount = in.readInt();
        entryPosition = in.readInt();
        isShowOriginalPrice = in.readString();
        isDailyChoice = in.readInt() == 1;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public GourmetDetail createFromParcel(Parcel in)
        {
            return new GourmetDetail(in);
        }

        @Override
        public GourmetDetail[] newArray(int size)
        {
            return new GourmetDetail[size];
        }
    };

    public enum Pictogram implements Parcelable
    {
        parking(R.string.label_parking, R.drawable.f_ic_facilities_05),
        valet(R.string.label_valet_available, R.drawable.f_ic_facilities_10),
        privateRoom(R.string.label_private_room, R.drawable.f_ic_facilities_11),
        groupBooking(R.string.label_group_booking, R.drawable.f_ic_facilities_12),
        babySeat(R.string.label_baby_seat, R.drawable.f_ic_facilities_13),
        corkage(R.string.label_corkage, R.drawable.f_ic_facilities_14),
        none(0, 0);

        private int mNameResId;
        private int mImageResId;

        Pictogram(int nameResId, int imageResId)
        {
            this.mNameResId = nameResId;
            this.mImageResId = imageResId;
        }

        public String getName(Context context)
        {
            if (mNameResId <= 0)
            {
                return "";
            }
            return context.getString(mNameResId);
        }

        public int getImageResId()
        {
            return mImageResId;
        }

        @Override
        public int describeContents()
        {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags)
        {
            dest.writeString(name());
        }

        public static final Parcelable.Creator<Pictogram> CREATOR = new Creator<Pictogram>()
        {
            @Override
            public Pictogram createFromParcel(Parcel in)
            {
                return Pictogram.valueOf(in.readString());
            }

            @Override
            public Pictogram[] newArray(int size)
            {
                return new Pictogram[size];
            }
        };
    }
}
