package com.twoheart.dailyhotel.screen.search.gourmet.result;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.place.adapter.PlaceListFragmentPagerAdapter;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.place.layout.PlaceSearchResultLayout;

public class GourmetSearchResultLayout extends PlaceSearchResultLayout
{
    private double mRadius;
    private GourmetSearchResultListFragmentPagerAdapter mGourmetSearchResultListFragmentPagerAdapter;

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

    public void setCalendarText(SaleTime saleTime)
    {
        String checkInDate = saleTime.getDayOfDaysDateFormat("yyyy.MM.dd(EEE)");

        setCalendarText(checkInDate);
    }

    @Override
    protected int getEmptyIconResourceId()
    {
        return R.drawable.no_gourmet_ic;
    }

    @Override
    protected synchronized PlaceListFragmentPagerAdapter getPlaceListFragmentPagerAdapter(FragmentManager fragmentManager, int count, View bottomOptionLayout, PlaceListFragment.OnPlaceListFragmentListener listener)
    {
        if (mGourmetSearchResultListFragmentPagerAdapter == null)
        {
            mGourmetSearchResultListFragmentPagerAdapter = new GourmetSearchResultListFragmentPagerAdapter(fragmentManager, count, bottomOptionLayout, listener);
        }

        return mGourmetSearchResultListFragmentPagerAdapter;
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