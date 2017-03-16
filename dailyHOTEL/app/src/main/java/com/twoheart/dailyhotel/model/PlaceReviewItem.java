package com.twoheart.dailyhotel.model;

public class PlaceReviewItem
{
    public static final int TYPE_ENTRY = 0;
    public static final int TYPE_HEADER_VIEW = 1;
    public static final int TYPE_FOOTER_VIEW = 2;

    public int mType;
    private Object mItem;

    public PlaceReviewItem(int type, Object item)
    {
        mType = type;
        mItem = item;
    }

    public <T> T getItem()
    {
        return (T) mItem;
    }
}