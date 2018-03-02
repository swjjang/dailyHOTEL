package com.daily.dailyhotel.entity;

public class StaySubwayAreaGroup extends AreaGroup<StaySubwayArea>
{
    private Area mRegion;

    public StaySubwayAreaGroup(Area region)
    {
        mRegion = region;
    }

    public Area getRegion()
    {
        return mRegion;
    }
}
