package com.daily.dailyhotel.entity;

/**
 * Created by iseung-won on 2017. 8. 24..
 */

public class CarouselListItem
{
    public static final int TYPE_HOMEPLACE = 0;
    public static final int TYPE_STAY = 1;
    public static final int TYPE_GOURMET = 2;

    public int mType;
    private Object mItem;

    public CarouselListItem(int type, Object item)
    {
        mType = type;
        mItem = item;
    }

    public <T> T getItem()
    {
        return (T) mItem;
    }
}