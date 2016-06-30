package com.twoheart.dailyhotel.screen.search.stay;

import android.content.Context;
import android.view.View;

import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.place.adapter.PlaceListAdapter;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.place.layout.PlaceSearchResultLayout;
import com.twoheart.dailyhotel.util.Constants;

import java.util.ArrayList;

public class StaySearchResultLayout extends PlaceSearchResultLayout
{
    private StaySearchResultListAdapter mListAdapter;

    @Override
    protected PlaceListAdapter getListAdapter()
    {
        mListAdapter = new StaySearchResultListAdapter(mContext, new ArrayList<PlaceViewItem>(), mOnItemClickListener);

        return mListAdapter;
    }

    public StaySearchResultLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    public void setSortType(Constants.SortType sortType)
    {
        if (mListAdapter == null)
        {
            return;
        }

        mListAdapter.setSortType(sortType);
    }

    @Override
    public void addSearchResultList(ArrayList<PlaceViewItem> placeViewItemList)
    {
        mIsLoading = false;

        if (placeViewItemList == null)
        {
            return;
        }

        mListAdapter.addAll(placeViewItemList);
        mListAdapter.notifyDataSetChanged();
    }

    private View.OnClickListener mOnItemClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            int position = mRecyclerView.getChildAdapterPosition(v);

            if (position < 0)
            {
                return;
            }

            PlaceViewItem placeViewItem = mListAdapter.getItem(position);

            if (placeViewItem.mType != PlaceViewItem.TYPE_ENTRY)
            {
                return;
            }

            ((OnEventListener) mOnEventListener).onItemClick(placeViewItem);
        }
    };
}
