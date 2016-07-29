package com.twoheart.dailyhotel.model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

public class GourmetCuration extends PlaceCuration
{
    private SaleTime mSaleTime;

    protected GourmetCurationOption mGourmetCurationOption;

    public GourmetCuration(Parcel in)
    {
        readFromParcel(in);
    }

    public GourmetCuration()
    {
        mGourmetCurationOption = new GourmetCurationOption();

        clear();
    }

    @Override
    public PlaceCurationOption getCurationOption()
    {
        return mGourmetCurationOption;
    }

    @Override
    public void setCurationOption(PlaceCurationOption placeCurationOption)
    {
        if (mGourmetCurationOption == null)
        {
            mGourmetCurationOption = new GourmetCurationOption();
        }

        mGourmetCurationOption.setCurationOption((GourmetCurationOption) placeCurationOption);
    }

    @Override
    public PlaceParams toPlaceParams(int page, int limit, boolean isDetails)
    {
        GourmetSearchParams gourmetParams = new GourmetSearchParams(this);
        gourmetParams.setPageInformation(page, limit, isDetails);

        return gourmetParams;
    }

    public void setSaleTime(long currentDateTime, long dailyDateTime)
    {
        if (mSaleTime == null)
        {
            mSaleTime = new SaleTime();
        }

        mSaleTime.setCurrentTime(currentDateTime);
        mSaleTime.setDailyTime(dailyDateTime);
    }

    public void setSaleTime(SaleTime saleTime)
    {
        mSaleTime = saleTime;
    }

    public SaleTime getSaleTime()
    {
        return mSaleTime;
    }

    public void clear()
    {
        mGourmetCurationOption.clear();

        mProvince = null;
        mLocation = null;
        mSaleTime = null;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeParcelable(mProvince, flags);
        dest.writeParcelable(mLocation, flags);
        dest.writeParcelable(mSaleTime, flags);
        dest.writeParcelable(mGourmetCurationOption, flags);
    }

    protected void readFromParcel(Parcel in)
    {
        mProvince = in.readParcelable(Province.class.getClassLoader());
        mLocation = in.readParcelable(Location.class.getClassLoader());
        mSaleTime = in.readParcelable(SaleTime.class.getClassLoader());
        mGourmetCurationOption = in.readParcelable(GourmetCurationOption.class.getClassLoader());
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public GourmetCuration createFromParcel(Parcel in)
        {
            return new GourmetCuration(in);
        }

        @Override
        public GourmetCuration[] newArray(int size)
        {
            return new GourmetCuration[size];
        }
    };
}
