package com.twoheart.dailyhotel.screen.mydaily.wishlist;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.daily.base.widget.DailyTextView;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.place.base.BaseBlurLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android_sam on 2016. 11. 1..
 */

public abstract class PlaceWishListLayout extends BaseBlurLayout
{
    RecyclerView mRecyclerView;
    private View mEmptyLayout;
    private DailyTextView mEmptyMessageView;
    private DailyTextView mEmptyButtonView;
    private PlaceWishListAdapter mListAdapter;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onListItemClick(View view, int position);

        void onListItemLongClick(View view, int position);

        void onListItemRemoveClick(int position);

        void onEmptyButtonClick();

        void onRecordAnalyticsList(List<PlaceViewItem> list);

        void onHomeClick();
    }

    protected abstract int getEmptyMessageResId();

    protected abstract int getEmptyButtonTextResId();

    protected abstract PlaceWishListAdapter getWishListAdapter(Context context //
        , List<PlaceViewItem> list, PlaceWishListAdapter.OnPlaceWishListItemListener listener);

    public PlaceWishListLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected void initLayout(View view)
    {
        View homeButtonView = view.findViewById(R.id.homeButtonView);
        homeButtonView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).onHomeClick();
            }
        });

        mEmptyLayout = view.findViewById(R.id.emptyLayout);
        setEmptyViewVisibility(View.GONE);

        mEmptyMessageView = view.findViewById(R.id.messageTextView02);
        mEmptyButtonView = view.findViewById(R.id.buttonView);

        mEmptyMessageView.setText(getEmptyMessageResId());
        mEmptyButtonView.setText(getEmptyButtonTextResId());

        mEmptyButtonView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).onEmptyButtonClick();
            }
        });

        mRecyclerView = view.findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        mRecyclerView.setLayoutManager(layoutManager);

        EdgeEffectColor.setEdgeGlowColor(mRecyclerView, mContext.getResources().getColor(R.color.default_over_scroll_edge));
    }

    public void setData(List<PlaceViewItem> list, boolean rewardEnabled)
    {
        setData(list, true, rewardEnabled);
    }

    public void setData(List<PlaceViewItem> list, boolean isShowEmpty, boolean rewardEnabled)
    {
        // 1개면 Footer만 있는 상태이다.
        if (list == null || list.size() == 0)
        {
            list = new ArrayList<>();

            setEmptyViewVisibility(isShowEmpty == true ? View.VISIBLE : View.GONE);
        } else
        {
            setEmptyViewVisibility(View.GONE);
        }

        if (mListAdapter == null)
        {
            mListAdapter = getWishListAdapter(mContext, list, mItemListener);

            if (DailyPreference.getInstance(mContext).getTrueVRSupport() > 0)
            {
                mListAdapter.setTrueVREnabled(true);
            }

            mRecyclerView.setAdapter(mListAdapter);
        } else
        {
            mListAdapter.setData(list);
            mListAdapter.notifyDataSetChanged();
        }

        mListAdapter.setRewardEnabled(rewardEnabled);

        ((OnEventListener) mOnEventListener).onRecordAnalyticsList(list);
    }

    public List<PlaceViewItem> getList()
    {
        return mListAdapter != null ? mListAdapter.getList() : null;
    }

    public int getRealItemCount()
    {
        int realCount = 0;
        if (mListAdapter != null)
        {
            realCount = mListAdapter.getItemCount() - 1;
            if (realCount < 0)
            {
                realCount = 0;
            }
        }

        return realCount;
    }

    public PlaceViewItem getItem(int position)
    {
        return mListAdapter != null ? mListAdapter.getItem(position) : null;
    }

    public PlaceViewItem removeItem(int position)
    {
        PlaceViewItem removeItem = mListAdapter != null ? mListAdapter.remove(position) : null;
        setEmptyViewVisibility(getRealItemCount() <= 0 ? View.VISIBLE : View.GONE);
        return removeItem;
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

    private PlaceWishListAdapter.OnPlaceWishListItemListener mItemListener = new PlaceWishListAdapter.OnPlaceWishListItemListener()
    {
        @Override
        public void onItemClick(View view)
        {
            int position = mRecyclerView.getChildAdapterPosition(view);
            if (position < 0)
            {
                //                ((OnEventListener) mOnEventListener).onListItemClick(view, position); // ????
                return;
            }

            ((OnEventListener) mOnEventListener).onListItemClick(view, position);
        }

        @Override
        public void onItemLongClick(View view)
        {
            int position = mRecyclerView.getChildAdapterPosition(view);
            if (position < 0)
            {
                return;
            }

            ((OnEventListener) mOnEventListener).onListItemLongClick(view, position);
        }

        @Override
        public void onItemRemoveClick(View view)
        {
            int position = mRecyclerView.getChildAdapterPosition(view);
            if (position < 0)
            {
                //                ((OnEventListener) mOnEventListener).onListItemClick(view, position); // ????
                return;
            }

            ((OnEventListener) mOnEventListener).onListItemRemoveClick(position);
        }
    };
}
