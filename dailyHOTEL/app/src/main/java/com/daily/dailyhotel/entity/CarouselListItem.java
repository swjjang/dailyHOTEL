package com.daily.dailyhotel.entity;

/**
 * Created by iseung-won on 2017. 8. 24..
 */

public class CarouselListItem
{
    public static final int TYPE_HOME_PLACE = 0;
    public static final int TYPE_IN_STAY = 1;
    public static final int TYPE_OB_STAY = 2;
    public static final int TYPE_GOURMET = 3;

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