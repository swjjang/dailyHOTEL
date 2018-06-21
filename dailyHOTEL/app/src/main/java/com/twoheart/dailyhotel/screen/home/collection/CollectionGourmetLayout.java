package com.twoheart.dailyhotel.screen.home.collection;

import android.content.Context;
import android.view.View;

import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.place.adapter.PlaceListAdapter;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;

import java.util.ArrayList;

public class CollectionGourmetLayout extends CollectionBaseLayout
{
    public CollectionGourmetLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected PlaceListAdapter getPlaceListAdapter(View.OnClickListener listener, View.OnClickListener recommendationListener)
    {
        return new CollectionGourmetAdapter(mContext, new ArrayList<PlaceViewItem>(), listener, recommendationListener);
    }

    @Override
    protected void setUsedMultiTransition(boolean isUsedMultiTransition)
    {
        mIsUsedMultiTransition = isUsedMultiTransition;

        if (mPlaceListAdapter != null)
        {
            ((CollectionGourmetAdapter) mPlaceListAdapter).setUsedMultiTransition(isUsedMultiTransition);
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
                CollectionGourmetAdapter.GourmetViewHolder gourmetViewHolder = (CollectionGourmetAdapter.GourmetViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position);

                if (gourmetViewHolder != null)
                {
                    gourmetViewHolder.gourmetCardView.setWish(wish);
                }
            }
        });
    }
}