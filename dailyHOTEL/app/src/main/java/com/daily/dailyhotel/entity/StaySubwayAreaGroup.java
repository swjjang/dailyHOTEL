package com.daily.dailyhotel.entity;

import java.util.List;

public class StaySubwayAreaGroup extends Area
{
    public int upperAreaIndex;

    private List<StayArea> mAreaList;

    public List<StayArea> getAreaList()
    {
        return mAreaList;
    }

    public int getAreaCount()
    {
        return mAreaList == null ? 0 : mAreaList.size();
    }

    public void setAreaList(List<StayArea> areaList)
    {
        mAreaList = areaList;
    }
}
