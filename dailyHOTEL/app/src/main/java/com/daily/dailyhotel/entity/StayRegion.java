package com.daily.dailyhotel.entity;

public class StayRegion
{
    private PreferenceRegion.AreaType mAreaType;

    private AreaElement mAreaGroupElement;

    private AreaElement mAreaElement;

    public StayRegion(PreferenceRegion.AreaType areaType, AreaElement areaGroupElement, AreaElement areaElement)
    {
        mAreaType = areaType;
        mAreaGroupElement = areaGroupElement;
        mAreaElement = areaElement;
    }

    public PreferenceRegion.AreaType getAreaType()
    {
        return mAreaType;
    }

    public AreaElement getAreaGroupElement()
    {
        return mAreaGroupElement;
    }

    public AreaElement getAreaElement()
    {
        return mAreaElement;
    }

    public int getAreaGroupIndex()
    {
        return mAreaGroupElement == null ? 0 : mAreaGroupElement.index;
    }

    public String getAreaGroupName()
    {
        return mAreaGroupElement == null ? null : mAreaGroupElement.name;
    }

    public int getAreaIndex()
    {
        return mAreaElement == null ? 0 : mAreaElement.index;
    }

    public String getAreaName()
    {
        return mAreaElement == null ? null : mAreaElement.name;
    }
}
