package com.daily.dailyhotel.entity;

import java.util.List;

public class Region
{
    private Province mProvince;
    private List<Area> mAreaList;

    public boolean expandGroup;

    public List<Area> getAreaList()
    {
        return mAreaList;
    }

    public void setAreaList(List<Area> areaList)
    {
        mAreaList = areaList;
    }

    public Province getProvince()
    {
        return mProvince;
    }

    public void setProvince(Province province)
    {
        mProvince = province;
    }

    public int getAreaCount()
    {
        return mAreaList == null ? 0 : mAreaList.size();
    }
}
