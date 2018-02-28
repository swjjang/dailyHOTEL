package com.daily.dailyhotel.entity;

import java.util.List;

public class StaySubwayAreaGroup extends Area
{
    public int upperAreaIndex;

    private List<Area> mAreaList;

    public List<Area> getAreaList()
    {
        return mAreaList;
    }

    public int getAreaCount()
    {
        return mAreaList == null ? 0 : mAreaList.size();
    }

    public void setAreaList(List<Area> areaList)
    {
        mAreaList = areaList;
    }
}
