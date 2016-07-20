package com.twoheart.dailyhotel.model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.Constants;

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

    public StayCurationOption getStayCurationOption()
    {
        return mStayCurationOption;
    }

    public Category getCategory()
    {
        return mCategory;
    }

    public void setCategory(Category category)
    {
        mCategory = category;
    }

    public StayParams getStayParams(int page, int limit, boolean isDetails)
    {
        StayParams params = new StayParams();

        params.dateCheckIn = mCheckInSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd");
        params.stays = getNights();
        params.provinceIdx = mProvince.getProvinceIndex();

        if (mProvince instanceof Area)
        {
            Area area = (Area) mProvince;
            if (area != null)
            {
                params.areaIdx = area.index;
            }
        }

        params.persons = mStayCurationOption.person;
        params.category = mCategory;
        params.bedType = mStayCurationOption.getParamStringByBedTypes(); // curationOption에서 가져온 스트링
        params.luxury = mStayCurationOption.getParamStingByAmenities(); // curationOption에서 가져온 스트링

        Constants.SortType sortType = mStayCurationOption.getSortType();
        if (Constants.SortType.DISTANCE == sortType)
        {
            if (mLocation != null)
            {
                params.latitude = mLocation.getLatitude();
                params.longitude = mLocation.getLongitude();
            }
        }

        params.page = page;
        params.limit = limit;
        params.setSortType(sortType);
        params.details = isDetails;

        return params;
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
