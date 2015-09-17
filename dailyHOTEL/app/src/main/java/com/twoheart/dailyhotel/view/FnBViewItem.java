package com.twoheart.dailyhotel.view;

import com.twoheart.dailyhotel.model.FnB;
import com.twoheart.dailyhotel.model.Place;

public class FnBViewItem extends PlaceViewItem
{
    private FnB mFnB;

    public FnBViewItem(String title)
    {
        super(title);
    }

    public FnBViewItem(FnB fnb)
    {
        super();

        mFnB = fnb;
    }

    @Override
    public Place getPlace()
    {
        return mFnB;
    }
}