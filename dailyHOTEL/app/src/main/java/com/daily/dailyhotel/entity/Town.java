package com.daily.dailyhotel.entity;

public class Town extends District
{
    private District mDistrict;

    public District getDistrict()
    {
        return mDistrict;
    }

    public void setDistrict(District district)
    {
        mDistrict = district;
    }
}
