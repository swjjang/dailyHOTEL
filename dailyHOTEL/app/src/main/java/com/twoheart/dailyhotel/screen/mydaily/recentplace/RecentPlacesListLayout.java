package com.twoheart.dailyhotel.screen.mydaily.recentplace;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.daily.base.widget.DailyImageView;
import com.daily.base.widget.DailyTextView;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.place.base.BaseBlurLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

import java.util.ArrayList;

/**
 * Created by android_sam on 2016. 10. 13..
 */

public abstract class RecentPlacesListLayout extends BaseBlurLayout
{
    RecyclerView mRecyclerView;
    private View mEmptyLayout;
    private DailyImageView mEmptyImageView;
    private DailyTextView mEmptyTextView;
    private DailyTextView mEmptyButtonTextView;
    private RecentPlacesListAdapter mListAdapter;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onListItemClick(View view, int position);

        void onListItemLongClick(View view, int position);

        void onListItemDeleteClick(int position);

        void onEmptyButtonClick();

        void onRecordAnalyticsList(ArrayList<PlaceViewItem> list);

        void onHomeClick();
    }

    protected abstract int getEmptyTextResId();

    protected abstract int getEmptyImageResId();

    protected abstract int getEmptyButtonTextResId();

    protected abstract RecentPlacesListAdapter getRecentPlacesListAdapter(Context context//
        , ArrayList<PlaceViewItem> list, RecentPlacesListAdapter.OnRecentPlacesItemListener listener);

    public RecentPlacesListLayout(Context context, OnBaseEventListener listener)
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
            imageResId = R.drawable.no_event_ic;
        }

        mEmptyImageView.setVectorImageResource(imageResId);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        mRecyclerView.setLayoutManager(layoutManager);

        EdgeEffectColor.setEdgeGlowColor(mRecyclerView, mContext.getResources().getColor(R.color.default_over_scroll_edge));
    }

    public void setData(ArrayList<PlaceViewItem> list, PlaceBookingDay placeBookingDay)
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
            mListAdapter = getRecentPlacesListAdapter(mContext, list, mItemListener);

            if (DailyPreference.getInstance(mContext).getTrueVRSupport() > 0)
            {
                mListAdapter.setTrueVREnabled(true);
            }

            mRecyclerView.setAdapter(mListAdapter);
        } else
        {
            mListAdapter.setPlaceBookingDay(placeBookingDay);
            mListAdapter.setData(list);
            mListAdapter.notifyDataSetChanged();
        }

        ((RecentPlacesListLayout.OnEventListener) mOnEventListener).onRecordAnalyticsList(list);
    }

    public ArrayList<PlaceViewItem> getList()
    {
        return mListAdapter != null ? mListAdapter.getList() : null;
    }

    public int getItemCount()
    {
        if (mListAdapter == null)
        {
            return 0;
        }

        return mListAdapter.getItemCount();
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
        PlaceViewItem removeItem = mListAdapter != null ? mListAdapter.removeItem(position) : null;
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

    private RecentPlacesListAdapter.OnRecentPlacesItemListener mItemListener = new RecentPlacesListAdapter.OnRecentPlacesItemListener()
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
        public void onDeleteClick(View view)
        {
            int position = mRecyclerView.getChildAdapterPosition(view);
            if (position < 0)
            {
                return;
            }

            ((OnEventListener) mOnEventListener).onListItemDeleteClick(position);
        }
    };

}
