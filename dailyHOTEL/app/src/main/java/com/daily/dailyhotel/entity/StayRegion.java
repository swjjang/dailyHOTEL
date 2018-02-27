package com.daily.dailyhotel.entity;

public class StayRegion
{
    private StayAreaGroup mAreaGroup;

    private StayArea mArea;

    public StayRegion(StayAreaGroup areaGroup, StayArea area)
    {
        mAreaGroup = areaGroup;
        mArea = area;
    }

    public StayAreaGroup getAreaGroup()
    {
        return mAreaGroup;
    }

    public StayArea getArea()
    {
        return mArea;
    }

    public void setAreaGroup(StayAreaGroup area)
    {
        mAreaGroup = area;
    }

    public void setArea(StayArea area)
    {
        mArea = area;
    }

    public String getAreaGroupName()
    {
        return mAreaGroup == null ? null : mAreaGroup.name;
    }

    public String getAreaName()
    {
        return mArea == null ? null : mArea.name;
    }
}
