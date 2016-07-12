package com.twoheart.dailyhotel.screen.search.stay.result;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.place.adapter.PlaceListAdapter;
import com.twoheart.dailyhotel.place.adapter.PlaceListFragmentPagerAdapter;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.place.layout.PlaceSearchResultLayout;
import com.twoheart.dailyhotel.screen.hotel.list.StayListFragmentPagerAdapter;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

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

        mListAdapter.setAll(placeViewItemList);
        mListAdapter.notifyDataSetChanged();
    }

    @Override
    protected int getEmptyIconResourceId()
    {
        return R.drawable.no_hotel_ic;
    }

    @Override
    protected PlaceListFragmentPagerAdapter getPlaceListFragmentPagerAdapter(FragmentManager fragmentManager, int count, View bottomOptionLayout, PlaceListFragment.OnPlaceListFragmentListener listener)
    {
        return new StayListFragmentPagerAdapter(fragmentManager, count, bottomOptionLayout, listener);
    }

    @Override
    protected void onAnalyticsCategoryFlicking(String category)
    {
        AnalyticsManager.getInstance(mContext).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , AnalyticsManager.Action.DAILY_HOTEL_CATEGORY_FLICKING, category, null);
    }

    @Override
    protected void onAnalyticsCategoryClick(String category)
    {
        AnalyticsManager.getInstance(mContext).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , AnalyticsManager.Action.HOTEL_CATEGORY_CLICKED, category, null);
    }

    protected void setDateText(SaleTime checkInSaleTime, SaleTime checkOutSaleTime)
    {
        String checkInDay = checkInSaleTime.getDayOfDaysDateFormat("M.d(EEE)");
        String checkOutDay = checkOutSaleTime.getDayOfDaysDateFormat("M.d(EEE)");

        setCalendarText(String.format("%s-%s", checkInDay, checkOutDay));
    }

    private View.OnClickListener mOnItemClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
//            int position = mRecyclerView.getChildAdapterPosition(v);
//
//            if (position < 0)
//            {
//                return;
//            }
//
//            PlaceViewItem placeViewItem = mListAdapter.getItem(position);
//
//            if (placeViewItem.mType != PlaceViewItem.TYPE_ENTRY)
//            {
//                return;
//            }
//
//            ((OnEventListener) mOnEventListener).onItemClick(placeViewItem);
        }
    };
}
