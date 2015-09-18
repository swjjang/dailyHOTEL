package com.twoheart.dailyhotel.view;

import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.Place;

public class GourmetViewItem extends PlaceViewItem
{
    private Gourmet mGourmet;

    public GourmetViewItem(String title)
    {
        super(title);
    }

    public GourmetViewItem(Gourmet fnb)
    {
        super();

        mGourmet = fnb;
    }

    @Override
    public Place getPlace()
    {
        return mGourmet;
    }
}