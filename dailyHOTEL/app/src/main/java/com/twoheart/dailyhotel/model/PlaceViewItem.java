package com.twoheart.dailyhotel.model;

public class PlaceViewItem
{
    public static final int TYPE_ENTRY = 0;
    public static final int TYPE_SECTION = 1;
    public static final int TYPE_EVENT_BANNER = 2;
    public static final int TYPE_FOOTER_VIEW = 3;
    public static final int TYPE_LOADING_VIEW = 4;

    public int mType;
    private Object mItem;

    public PlaceViewItem(int type, Object item)
    {
        mType = type;
        mItem = item;
    }

    public <T> T getItem()
    {
        return (T) mItem;
    }
}