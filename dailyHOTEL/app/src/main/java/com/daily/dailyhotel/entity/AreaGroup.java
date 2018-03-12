package com.daily.dailyhotel.entity;

import java.util.List;

public class AreaGroup<E extends Area> extends AreaElement
{
    private List<E> mAreaList;

    public List<E> getAreaList()
    {
        return mAreaList;
    }

    public E getArea(int position)
    {
        return mAreaList == null ? null : mAreaList.get(position);
    }

    public int getAreaCount()
    {
        return mAreaList == null ? 0 : mAreaList.size();
    }

    public void setAreaList(List<E> areaList)
    {
        mAreaList = areaList;
    }
}
