package com.twoheart.dailyhotel.screen.information.wishlist;

import android.content.Context;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;

import java.util.ArrayList;

/**
 * Created by android_sam on 2016. 10. 13..
 */

public class GourmetWishListLayout extends PlaceWishListLayout
{
    public GourmetWishListLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected PlaceWishListAdapter getWishListAdapter(Context context, ArrayList<? extends Place> list, PlaceWishListAdapter.OnWishListItemListener listener)
    {
        return new GourmetWishListAdapter(context, list, listener);
    }

    @Override
    protected int getEmptyTextResId()
    {
        return R.string.recent_gourmet_list_empty_message;
    }

    @Override
    protected int getEmptyImageResId()
    {
        return R.drawable.no_gourmet_ic;
    }

    @Override
    protected int getEmptyButtonTextResId()
    {
        return R.string.recent_gourmet_list_empty_button_message;
    }


}
