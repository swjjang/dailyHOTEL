package com.twoheart.dailyhotel.screen.search.gourmet.result;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.place.adapter.PlaceListFragmentPagerAdapter;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.place.layout.PlaceSearchResultLayout;
import com.twoheart.dailyhotel.util.Constants;

import java.util.ArrayList;

public class GourmetSearchResultLayout extends PlaceSearchResultLayout
{
    public GourmetSearchResultLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected void initLayout(View view)
    {
        super.initLayout(view);

        setMenuBarLayoutVisible(false);
    }

//    public void setSortType(Constants.SortType sortType)
//    {
//        PlaceListFragment placeListFragment = getCurrentPlaceListFragment();
//
//        if (placeListFragment != null)
//        {
//            ((GourmetSearchResultListFragment) placeListFragment).setSortType(sortType);
//        }
//    }
//
//    @Override
//    public void addSearchResultList(ArrayList<PlaceViewItem> placeViewItemList)
//    {
//        if (placeViewItemList == null)
//        {
//            return;
//        }
//
//        PlaceListFragment placeListFragment = getCurrentPlaceListFragment();
//
//        if (placeListFragment != null)
//        {
//            ((GourmetSearchResultListFragment) placeListFragment).addSearchResultList(placeViewItemList);
//        }
//
//    }

    public void setCalendarText(SaleTime saleTime)
    {
        String checkInDate = saleTime.getDayOfDaysDateFormat("yyyy.MM.dd(EEE)");

        setCalendarText(checkInDate);
    }

    @Override
    protected void addSearchResultList(ArrayList<PlaceViewItem> placeViewItemList)
    {

    }

    @Override
    protected int getEmptyIconResourceId()
    {
        return R.drawable.no_gourmet_ic;
    }

    @Override
    protected PlaceListFragmentPagerAdapter getPlaceListFragmentPagerAdapter(FragmentManager fragmentManager, int count, View bottomOptionLayout, PlaceListFragment.OnPlaceListFragmentListener listener)
    {
        return new GourmetSearchResultListFragmentPagerAdapter(fragmentManager, count, bottomOptionLayout, listener);
    }

    @Override
    protected void onAnalyticsCategoryFlicking(String category)
    {

    }

    @Override
    protected void onAnalyticsCategoryClick(String category)
    {

    }
}