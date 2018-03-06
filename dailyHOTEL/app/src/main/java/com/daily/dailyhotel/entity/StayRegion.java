package com.daily.dailyhotel.entity;

public class StayRegion
{
    private PreferenceRegion.AreaType mAreaType;

    private Area mAreaGroup;

    private Area mArea;

    public StayRegion(PreferenceRegion.AreaType areaType, Area areaGroup, Area area)
    {
        mAreaType = areaType;
        mAreaGroup = areaGroup;
        mArea = area;
    }

    public PreferenceRegion.AreaType getAreaType()
    {
        return mAreaType;
    }

    public Area getAreaGroup()
    {
        return mAreaGroup;
    }

    public Area getArea()
    {
        return mArea;
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
