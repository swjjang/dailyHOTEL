package com.twoheart.dailyhotel.place.layout;

import android.content.Context;
import android.view.View;

import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;

public abstract class PlaceReviewLayout extends BaseLayout
{
    public interface OnEventListener extends OnBaseEventListener
    {
    }

    public PlaceReviewLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);


    }

    @Override
    protected void initLayout(View view)
    {
    }
}