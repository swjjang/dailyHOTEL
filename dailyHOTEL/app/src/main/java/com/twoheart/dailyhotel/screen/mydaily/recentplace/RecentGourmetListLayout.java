package com.twoheart.dailyhotel.screen.mydaily.recentplace;

import android.content.Context;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;

import java.util.ArrayList;

/**
 * Created by android_sam on 2016. 10. 13..
 */

public class RecentGourmetListLayout extends RecentPlacesListLayout
{
    public RecentGourmetListLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected RecentPlacesListAdapter getRecentPlacesListAdapter(Context context, ArrayList<PlaceViewItem> list, RecentPlacesListAdapter.OnRecentPlacesItemListener listener)
    {
        return new RecentGourmetListAdapter(context, list, listener);
    }

    @Override
    protected void notifyWishChanged(int position, boolean wish)
    {
        if (mRecyclerView == null)
        {
            return;
        }

        mRecyclerView.post(new Runnable()
        {
            @Override
            public void run()
            {
                RecentGourmetListAdapter.GourmetViewHolder gourmetViewHolder = (RecentGourmetListAdapter.GourmetViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position);

                if (gourmetViewHolder != null)
                {
                    gourmetViewHolder.gourmetCardView.setWish(wish);
                }
            }
        });
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
