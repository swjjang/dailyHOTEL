package com.twoheart.dailyhotel.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.model.ImageInformation;
import com.twoheart.dailyhotel.network.model.StayDetailParams;

import java.util.List;

public class StayDetail extends PlaceDetail<StayProduct> implements Parcelable
{
    private StayDetailParams mStayDetailParams;

    public int nights;
    public boolean hasCoupon;

    public StayDetail(int hotelIndex, int nights, int entryIndex, String isShowOriginalPrice, int listCount, boolean isDailyChoice)
    {
        this.index = hotelIndex;
        this.nights = nights;
        this.entryPosition = entryIndex;
        this.isShowOriginalPrice = isShowOriginalPrice;
        this.listCount = listCount;
        this.isDailyChoice = isDailyChoice;
    }

    public StayDetail(Parcel in)
    {
        readFromParcel(in);
    }

    public StayDetailParams getStayDetailParams()
    {
        return mStayDetailParams;
    }

    public void setStayDetailParams(StayDetailParams stayDetailParams)
    {
        this.mStayDetailParams = stayDetailParams;
    }

    @Override
    public List<StayProduct> getProductList()
    {
        if (mStayDetailParams == null)
        {
            return null;
        }

        return mStayDetailParams.getProductList();
    }

    @Override
    public StayProduct getProduct(int index)
    {
        if (mStayDetailParams == null || index < 0)
        {
            return null;
        }

        List<StayProduct> stayProductList = mStayDetailParams.getProductList();
        if (stayProductList == null || stayProductList.size() == 0)
        {
            return null;
        }

        return stayProductList.get(index);
    }

    @Override
    public List<Pictogram> getPictogramList()
    {
        if (mStayDetailParams == null)
        {
            return null;
        }

        return mStayDetailParams.getPictogramList();
    }

    @Override
    public List<ImageInformation> getImageList()
    {
        if (mStayDetailParams == null) {
            return null;
        }

        return mStayDetailParams.getImageList();
    }

    @Override
    public List<DetailInformation> getDetailList()
    {
        if (mStayDetailParams == null)
        {
            return null;
        }

        return mStayDetailParams.getDetailInformationList();
    }

    @Override
    public List<String> getBenefitList()
    {
        if (mStayDetailParams == null) {
            return null;
        }

        return mStayDetailParams.getBenefitList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeParcelable(mStayDetailParams, flags);
        dest.writeInt(hasCoupon == true ? 1 : 0);
        dest.writeInt(index);
        dest.writeInt(listCount);
        dest.writeInt(entryPosition);
        dest.writeString(isShowOriginalPrice);
        dest.writeInt(isDailyChoice == true ? 1 : 0);
    }

    protected void readFromParcel(Parcel in)
    {
        mStayDetailParams = in.readParcelable(StayDetailParams.class.getClassLoader());
        hasCoupon = in.readInt() == 1 ? true : false;
        index = in.readInt();
        listCount = in.readInt();
        entryPosition = in.readInt();
        isShowOriginalPrice = in.readString();
        isDailyChoice = in.readInt() == 1 ? true : false;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public StayDetail createFromParcel(Parcel in)
        {
            return new StayDetail(in);
        }

        @Override
        public StayDetail[] newArray(int size)
        {
            return new StayDetail[size];
        }
    };

    public enum Pictogram implements Parcelable
    {
        PARKING(R.string.label_parking, R.drawable.ic_detail_facilities_01_parking),
        NO_PARKING(R.string.label_unabled_parking, R.drawable.ic_detail_facilities_02_no_parking),
        SHARED_BBQ(R.string.label_allowed_barbecue, R.drawable.ic_detail_facilities_06_bbq),
        POOL(R.string.label_pool, R.drawable.ic_detail_facilities_03_pool),
        BUSINESS_CENTER(R.string.label_business_center, R.drawable.ic_detail_facilities_01_parking),
        FITNESS(R.string.label_fitness, R.drawable.ic_detail_facilities_04_fitness),
        SAUNA(R.string.label_sauna, R.drawable.ic_detail_facilities_01_parking),
        PET(R.string.label_allowed_pet, R.drawable.ic_detail_facilities_05_pet),
        KIDS_PLAY_ROOM(R.string.label_kids_play_room, R.drawable.ic_detail_facilities_01_parking),
        NONE(0, 0);

        private int mNameResId;
        private int mImageResId;

        Pictogram(int nameResId, int imageResId)
        {
            mNameResId = nameResId;
            mImageResId = imageResId;
        }

        public String getName(Context context)
        {
            if (mNameResId == 0)
            {
                return null;
            }

            return context.getString(mNameResId);
        }

        public int getImageResId()
        {
            return mImageResId;
        }

        public void setNameResId(int nameResId)
        {
            mNameResId = nameResId;
        }

        public void setImageResId(int imageResId)
        {
            mImageResId = imageResId;
        }

        @Override
        public int describeContents()
        {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags)
        {
            dest.writeInt(ordinal());
            dest.writeInt(mNameResId);
            dest.writeInt(mImageResId);
        }

        public static final Parcelable.Creator<Pictogram> CREATOR = new Creator<Pictogram>()
        {
            @Override
            public Pictogram createFromParcel(Parcel in)
            {
                Pictogram pictogram = Pictogram.values()[in.readInt()];
                pictogram.setNameResId(in.readInt());
                pictogram.setImageResId(in.readInt());
                return pictogram;
            }

            @Override
            public Pictogram[] newArray(int size)
            {
                return new Pictogram[size];
            }
        };
    }
}