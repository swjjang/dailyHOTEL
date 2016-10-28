package com.twoheart.dailyhotel.screen.information.wishlist;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.widget.DailyImageView;
import com.twoheart.dailyhotel.widget.DailyTextView;

import java.util.ArrayList;

/**
 * Created by android_sam on 2016. 10. 13..
 */

public abstract class PlaceWishListLayout extends BaseLayout
{
    private RecyclerView mRecyclerView;
    private View mEmptyLayout;
    private DailyImageView mEmptyImageView;
    private DailyTextView mEmptyTextView;
    private DailyTextView mEmptyButtonTextView;
    private PlaceWishListAdapter mListAdapter;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onListItemClick(View view, int position);

        void onListItemDeleteClick(int position);

        void onEmptyButtonClick();
    }

    protected abstract int getEmptyTextResId();

    protected abstract int getEmptyImageResId();

    protected abstract int getEmptyButtonTextResId();

    protected abstract PlaceWishListAdapter getWishListAdapter(Context context//
        , ArrayList<? extends Place> list, PlaceWishListAdapter.OnWishListItemListener listener);

    public PlaceWishListLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected void initLayout(View view)
    {
        mEmptyLayout = view.findViewById(R.id.emptyLayout);
        setEmptyViewVisibility(View.GONE);

        mEmptyImageView = (DailyImageView) view.findViewById(R.id.emptyImageView);
        mEmptyTextView = (DailyTextView) view.findViewById(R.id.emptyTextView);
        mEmptyTextView.setText(getEmptyTextResId());
        mEmptyButtonTextView = (DailyTextView) view.findViewById(R.id.buttonView);
        mEmptyButtonTextView.setText(getEmptyButtonTextResId());

        mEmptyButtonTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).onEmptyButtonClick();
            }
        });

        int imageResId = getEmptyImageResId();
        if (imageResId <= 0)
        {
            imageResId = R.drawable.no_wishlist_ic;
        }

        mEmptyImageView.setVectorImageResource(imageResId);

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

            setEmptyViewVisibility(View.VISIBLE);
        } else
        {
            setEmptyViewVisibility(View.GONE);
        }

        if (mListAdapter == null)
        {
            mListAdapter = getWishListAdapter(mContext, list, mItemListener);
            mRecyclerView.setAdapter(mListAdapter);
        } else
        {
            mListAdapter.setData(list);
            mListAdapter.notifyDataSetChanged();
        }
    }

    public ArrayList<? extends Place> getList()
    {
        return mListAdapter != null ? mListAdapter.getList() : null;
    }

    public Place getItem(int position)
    {
        return mListAdapter != null ? mListAdapter.getItem(position) : null;
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

    private void setEmptyViewVisibility(int visibility)
    {
        if (mEmptyLayout != null)
        {
            mEmptyLayout.setVisibility(visibility);
        }
    }

    private PlaceWishListAdapter.OnWishListItemListener mItemListener = new PlaceWishListAdapter.OnWishListItemListener()
    {
        @Override
        public void onItemClick(View view)
        {
            int position = mRecyclerView.getChildAdapterPosition(view);
            if (position < 0)
            {
                ((OnEventListener) mOnEventListener).onListItemClick(view, position);
                return;
            }

            ((OnEventListener) mOnEventListener).onListItemClick(view, position);
        }

        @Override
        public void onDeleteClick(View view, int position)
        {
            ((OnEventListener) mOnEventListener).onListItemDeleteClick(position);
        }
    };

}
