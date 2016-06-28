package com.twoheart.dailyhotel.screen.hotel.list;

import android.location.Location;

import com.twoheart.dailyhotel.model.Category;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.StayCurationOption;
import com.twoheart.dailyhotel.util.Constants;

public class StayCurationManager
{
    private static StayCurationManager mInstance;


    private Constants.SortType sortType = Constants.SortType.DEFAULT;

    private Province mProvince;
    private Location mLocation; // Not Parcelable

    private SaleTime mCheckInSaleTime;
    private SaleTime mCheckOutSaleTime;
    private Category mCategory;
    private StayCurationOption mStayCurationOption;

    public void setCheckInSaleTime(long currentDateTime, long dailyDateTime)
    {
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

    public static synchronized StayCurationManager getInstance()
    {
        if (mInstance == null)
        {
            mInstance = new StayCurationManager();
        }

        return mInstance;
    }

    private StayCurationManager()
    {
        mStayCurationOption = new StayCurationOption();

        clear();
    }

    public StayCurationOption getStayCurationOption()
    {
        return mStayCurationOption;
    }

    public void setSortType(Constants.SortType sortType)
    {
        if (sortType == null)
        {
            sortType = Constants.SortType.DEFAULT;
        }

        this.sortType = sortType;
    }

    public Constants.SortType getSortType()
    {
        return sortType;
    }

    public Province getProvince()
    {
        return mProvince;
    }

    public void setProvince(Province province)
    {
        mProvince = province;
    }

    public Location getLocation()
    {
        return mLocation;
    }

    public void setLocation(Location location)
    {
        mLocation = location;
    }

    public Category getCategory()
    {
        return mCategory;
    }

    public void setCategory(Category category)
    {
        mCategory = category;
    }

    public void clear()
    {
        sortType = Constants.SortType.DEFAULT;
        mCategory = Category.ALL;

        mStayCurationOption.clear();
        
        mCheckInSaleTime = new SaleTime();
        mCheckOutSaleTime = mCheckInSaleTime.getClone(mCheckInSaleTime.getOffsetDailyDay() + 1);

        mProvince = null;
        mLocation = null;

    }

}
