package com.twoheart.dailyhotel.screen.mydaily.recentplace;

import android.content.Context;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;

import java.util.ArrayList;

/**
 * Created by android_sam on 2016. 10. 13..
 */

public class RecentStayListLayout extends RecentPlacesListLayout
{
    public RecentStayListLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected RecentPlacesListAdapter getRecentPlacesListAdapter(Context context, ArrayList<PlaceViewItem> list, RecentPlacesListAdapter.OnRecentPlacesItemListener listener)
    {
        return new RecentStayListAdapter(context, list, listener);
    }

    @Override
    protected void notifyWishChanged(int position, boolean wish)
    {
        if (mRecyclerView == null)
        {
            return;
        }

        RecentStayListAdapter.StayInboundViewHolder stayViewHolder = (RecentStayListAdapter.StayInboundViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position);

        if (stayViewHolder != null)
        {
            stayViewHolder.stayCardView.setWish(wish);
        }
    }

    @Override
    protected int getEmptyTextResId()
    {
        return R.string.recent_stay_list_empty_message;
    }

    @Override
    protected int getEmptyImageResId()
    {
        return R.drawable.no_hotel_ic;
    }

    @Override
    protected int getEmptyButtonTextResId()
    {
        return R.string.recent_stay_list_empty_button_message;
    }
}
