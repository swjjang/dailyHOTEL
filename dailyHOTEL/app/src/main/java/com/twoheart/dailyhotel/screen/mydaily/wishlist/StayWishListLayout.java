package com.twoheart.dailyhotel.screen.mydaily.wishlist;

import android.content.Context;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;

import java.util.List;

/**
 * Created by android_sam on 2016. 11. 1..
 */

public class StayWishListLayout extends PlaceWishListLayout
{
    public StayWishListLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected PlaceWishListAdapter getWishListAdapter(Context context, List<PlaceViewItem> list, PlaceWishListAdapter.OnPlaceWishListItemListener listener)
    {
        return new StayWishListAdapter(context, list, listener);
    }

    @Override
    protected int getEmptyMessageResId()
    {
        return R.string.wishlist_list_empty_message02_stay;
    }

    @Override
    protected int getEmptyButtonTextResId()
    {
        return R.string.wishlist_list_empty_button_message_stay;
    }
}
