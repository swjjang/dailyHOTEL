package com.twoheart.dailyhotel.view;

import com.twoheart.dailyhotel.model.Place;

public abstract class PlaceViewItem
{
    public static final int TYPE_SECTION = 1;
    public static final int TYPE_ENTRY = 0;

    public int type;
    public String title;

    public PlaceViewItem()
    {
        type = TYPE_ENTRY;
    }

    public PlaceViewItem(String title)
    {
        type = TYPE_SECTION;
        this.title = title;
    }

    public abstract Place getPlace();
}