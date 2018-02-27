package com.daily.dailyhotel.entity;

public class StayRegion
{
    private Area mAreaGroup;

    private Area mArea;

    public StayRegion(Area areaGroup, Area area)
    {
        mAreaGroup = areaGroup;
        mArea = area;
    }

    public Area getAreaGroup()
    {
        return mAreaGroup;
    }

    public Area getArea()
    {
        return mArea;
    }

    public void setAreaGroup(Area area)
    {
        mAreaGroup = area;
    }

    public void setArea(Area area)
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
