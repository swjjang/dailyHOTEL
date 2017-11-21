package com.daily.dailyhotel.entity;

import java.util.List;

public class Region
{
    private List<Province> mProvinceList;
    private List<Area> mAreaList;

    public List<Area> getAreaList()
    {
        return mAreaList;
    }

    public void setAreaList(List<Area> areaList)
    {
        mAreaList = areaList;
    }

    public List<Province> getProvinceList()
    {
        return mProvinceList;
    }

    public void setProvinceList(List<Province> provinceList)
    {
        mProvinceList = provinceList;
    }
}
