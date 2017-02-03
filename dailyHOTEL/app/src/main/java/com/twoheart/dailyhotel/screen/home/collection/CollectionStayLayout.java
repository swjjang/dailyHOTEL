package com.twoheart.dailyhotel.screen.home.collection;

import android.content.Context;
import android.view.View;

import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.place.adapter.PlaceListAdapter;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;

import java.util.ArrayList;

public class CollectionStayLayout extends CollectionBaseLayout
{
    public CollectionStayLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected PlaceListAdapter getPlaceListAdapter(View.OnClickListener listener)
    {
        return new CollectionStayAdapter(mContext, new ArrayList<PlaceViewItem>(), listener);
    }
}