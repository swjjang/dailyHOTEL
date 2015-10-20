package com.twoheart.dailyhotel.view;

import com.twoheart.dailyhotel.model.Hotel;

public class HotelListViewItem
{
    public static final int TYPE_ENTRY = 0;
    public static final int TYPE_SECTION = 1;
    public static final int TYPE_EVENT = 2;

    private int type;
    private Hotel item;
    private String category;
    public int eventLayotuResourceId;

    public HotelListViewItem(Hotel hotel)
    {
        type = TYPE_ENTRY;
        item = hotel;
    }

    public HotelListViewItem(String region)
    {
        type = TYPE_SECTION;
        category = region;
    }

    public HotelListViewItem(int resId)
    {
        type = TYPE_EVENT;
        eventLayotuResourceId = resId;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public Hotel getItem()
    {
        return item;
    }

    public void setItem(Hotel item)
    {
        this.item = item;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

}