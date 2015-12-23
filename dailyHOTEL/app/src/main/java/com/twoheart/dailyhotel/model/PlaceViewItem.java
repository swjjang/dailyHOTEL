package com.twoheart.dailyhotel.model;

public class PlaceViewItem
{
    public static final int TYPE_ENTRY = 0;
    public static final int TYPE_SECTION = 1;
    public static final int TYPE_EVENT_BANNER = 2;

    protected int mType;
    protected Object mItem;

    public PlaceViewItem(int type, Object item)
    {
        mType = type;
        mItem = item;
    }

    public int getType()
    {
        return mType;
    }

    public <T extends Object> T getItem()
    {
        return (T) mItem;
    }
}