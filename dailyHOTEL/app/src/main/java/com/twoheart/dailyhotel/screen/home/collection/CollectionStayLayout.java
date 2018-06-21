package com.twoheart.dailyhotel.screen.home.collection;

import android.content.Context;
import android.view.View;

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
    protected PlaceListAdapter getPlaceListAdapter(View.OnClickListener listener, View.OnClickListener recommendationListener)
    {
        return new CollectionStayAdapter(mContext, new ArrayList<>(), listener, recommendationListener);
    }

    @Override
    protected void setUsedMultiTransition(boolean isUsedMultiTransition)
    {
        mIsUsedMultiTransition = isUsedMultiTransition;

        if (mPlaceListAdapter != null)
        {
            ((CollectionStayAdapter) mPlaceListAdapter).setUsedMultiTransition(isUsedMultiTransition);
        }
    }

    @Override
    protected void notifyWishChanged(int position, boolean wish)
    {
        if (mRecyclerView == null || mPlaceListAdapter == null)
        {
            return;
        }

        mRecyclerView.post(new Runnable()
        {
            @Override
            public void run()
            {
                CollectionStayAdapter.StayViewHolder stayViewHolder = (CollectionStayAdapter.StayViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position);

                if (stayViewHolder != null)
                {
                    stayViewHolder.stayCardView.setWish(wish);
                }
            }
        });
    }
}