package com.twoheart.dailyhotel.model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

public class StayCuration extends PlaceCuration
{
    private SaleTime mCheckInSaleTime;
    private SaleTime mCheckOutSaleTime;
    private Category mCategory;
    private StayCurationOption mStayCurationOption;

    public void setCheckInSaleTime(long currentDateTime, long dailyDateTime)
    {
        if (mCheckInSaleTime == null)
        {
            mCheckInSaleTime = new SaleTime();
        }

        mCheckInSaleTime.setCurrentTime(currentDateTime);
        mCheckInSaleTime.setDailyTime(dailyDateTime);
    }

    public void setCheckInSaleTime(SaleTime saleTime)
    {
        mCheckInSaleTime = saleTime;
    }

    public void setCheckOutSaleTime(SaleTime saleTime)
    {
        mCheckOutSaleTime = saleTime;
    }

    public SaleTime getCheckInSaleTime()
    {
        return mCheckInSaleTime;
    }

    public SaleTime getCheckOutSaleTime()
    {
        return mCheckOutSaleTime;
    }

    public int getNights()
    {
        if (mCheckInSaleTime == null || mCheckOutSaleTime == null)
        {
            return 1;
        }

        return mCheckOutSaleTime.getOffsetDailyDay() - mCheckInSaleTime.getOffsetDailyDay();
    }

    public StayCuration()
    {
        mStayCurationOption = new StayCurationOption();

        clear();
    }

    @Override
    public PlaceCurationOption getCurationOption()
    {
        return mStayCurationOption;
    }

    @Override
    public void setCurationOption(PlaceCurationOption placeCurationOption)
    {
        if (mStayCurationOption == null)
        {
            mStayCurationOption = new StayCurationOption();
        }

        mStayCurationOption.setCurationOption((StayCurationOption) placeCurationOption);
    }

    @Override
    public PlaceParams toPlaceParams(int page, int limit, boolean isDetails)
    {
        StayParams stayParams = new StayParams(this);
        stayParams.setPageInformation(page, limit, isDetails);

        return stayParams;
    }

    public Category getCategory()
    {
        if (mCategory == null)
        {
            mCategory = Category.ALL;
        }

        return mCategory;
    }

    public void setCategory(Category category)
    {
        if (category == null)
        {
            category = Category.ALL;
        }

        mCategory = category;
    }

    @Override
    public void clear()
    {
        mCategory = Category.ALL;

        mStayCurationOption.clear();

        mCheckInSaleTime = null;
        mCheckOutSaleTime = null;

        mProvince = null;
        mLocation = null;
    }

    public StayCuration(Parcel in)
    {
        readFromParcel(in);
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
        dest.writeParcelable(mCheckInSaleTime, flags);
        dest.writeParcelable(mCheckOutSaleTime, flags);
        dest.writeParcelable(mCategory, flags);
        dest.writeParcelable(mStayCurationOption, flags);
    }

    protected void readFromParcel(Parcel in)
    {
        mProvince = in.readParcelable(Province.class.getClassLoader());
        mLocation = in.readParcelable(Location.class.getClassLoader());
        mCheckInSaleTime = in.readParcelable(SaleTime.class.getClassLoader());
        mCheckOutSaleTime = in.readParcelable(SaleTime.class.getClassLoader());
        mCategory = in.readParcelable(Category.class.getClassLoader());
        mStayCurationOption = in.readParcelable(StayCurationOption.class.getClassLoader());
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public StayCuration createFromParcel(Parcel in)
        {
            return new StayCuration(in);
        }

        @Override
        public StayCuration[] newArray(int size)
        {
            return new StayCuration[size];
        }
    };
}
