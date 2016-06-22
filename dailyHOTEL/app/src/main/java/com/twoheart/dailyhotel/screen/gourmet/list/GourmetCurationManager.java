package com.twoheart.dailyhotel.screen.gourmet.list;

import android.location.Location;

import com.twoheart.dailyhotel.model.GourmetCurationOption;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;

public class GourmetCurationManager
{
    private static GourmetCurationManager mInstance;

    private GourmetCurationOption mGourmetCurationOption;

    private Province mProvince;
    private Location mLocation;
    private SaleTime mSaleTime;

    public static synchronized GourmetCurationManager getInstance()
    {
        if (mInstance == null)
        {
            mInstance = new GourmetCurationManager();
        }

        return mInstance;
    }

    private GourmetCurationManager()
    {
        mGourmetCurationOption = new GourmetCurationOption();

        clear();
    }

    public void clear()
    {
        mGourmetCurationOption.clear();

        mProvince = null;
        mLocation = null;
        mSaleTime = null;
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
}
