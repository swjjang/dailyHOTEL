package com.twoheart.dailyhotel.screen.information.recentplace;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

import java.util.ArrayList;

/**
 * Created by android_sam on 2016. 10. 13..
 */

public abstract class RecentPlacesListLayout extends BaseLayout
{
    private RecyclerView mRecyclerView;
    private RecentPlacesListAdapter mListAdapter;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onListItemClick(int position);

        void onListItemDeleteClick(int position);
    }

    protected abstract RecentPlacesListAdapter getRecentPlacesListAdapter(Context context//
        , ArrayList<? extends Place> list, RecentPlacesListAdapter.OnRecentPlacesItemListener listener);

    public RecentPlacesListLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected void initLayout(View view)
    {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        mRecyclerView.setLayoutManager(layoutManager);

        EdgeEffectColor.setEdgeGlowColor(mRecyclerView, mContext.getResources().getColor(R.color.default_over_scroll_edge));
    }

    public void setData(ArrayList<? extends Place> list)
    {
        if (list == null || list.size() == 0)
        {
            list = new ArrayList<>();
        }

        if (mListAdapter == null)
        {
            mListAdapter = getRecentPlacesListAdapter(mContext, list, mItemListener);
            mRecyclerView.setAdapter(mListAdapter);
        } else
        {
            mListAdapter.setData(list);
            mListAdapter.notifyDataSetChanged();
        }
    }

    public Place getItem(int position)
    {
        return mListAdapter != null ? mListAdapter.getItem(position) : null;
    }

    public boolean removeItem(Place place)
    {
        return mListAdapter != null ? mListAdapter.removeItem(place) : false;
    }

    public Place removeItem(int position)
    {
        return mListAdapter != null ? mListAdapter.removeItem(position) : null;
    }

    public void notifyDataSetChanged()
    {
        if (mListAdapter != null)
        {
            mListAdapter.notifyDataSetChanged();
        }
    }

    private RecentPlacesListAdapter.OnRecentPlacesItemListener mItemListener = new RecentPlacesListAdapter.OnRecentPlacesItemListener()
    {
        @Override
        public void onItemClick(View view)
        {
            int position = mRecyclerView.getChildAdapterPosition(view);
            if (position < 0)
            {
                ((OnEventListener) mOnEventListener).onListItemClick(position);
                return;
            }

            ((OnEventListener) mOnEventListener).onListItemClick(position);
        }

        @Override
        public void onDeleteClick(View view, int position)
        {
            ((OnEventListener) mOnEventListener).onListItemDeleteClick(position);
        }
    };

}
