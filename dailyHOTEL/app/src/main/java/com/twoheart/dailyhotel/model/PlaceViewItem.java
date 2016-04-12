package com.twoheart.dailyhotel.model;

public class PlaceViewItem
{
    public static final int TYPE_ENTRY = 0;
    public static final int TYPE_SECTION = 1;
    public static final int TYPE_EVENT_BANNER = 2;

    public int mType;
    private Object mItem;

    public PlaceViewItem(int type, Object item)
    {
        mType = type;
        mItem = item;
    }

    public <T extends Object> T getItem()
    {
        return (T) mItem;
    }
}