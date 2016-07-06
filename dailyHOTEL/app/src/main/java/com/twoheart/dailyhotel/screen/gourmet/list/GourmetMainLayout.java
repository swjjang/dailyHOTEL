package com.twoheart.dailyhotel.screen.gourmet.list;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.place.adapter.PlaceListFragmentPagerAdapter;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.place.layout.PlaceMainLayout;

public class GourmetMainLayout extends PlaceMainLayout
{
    public GourmetMainLayout(Context context, OnEventListener mOnEventListener)
    {
        super(context, mOnEventListener);
    }

    @Override
    protected PlaceListFragmentPagerAdapter getPlaceListFragmentPagerAdapter(FragmentManager fragmentManager, int count, View bottomOptionLayout, PlaceListFragment.OnPlaceListFragmentListener listener)
    {
        return new GourmetListFragmentPagerAdapter(fragmentManager, count, bottomOptionLayout, listener);
    }

    @Override
    protected void onAnalyticsCategoryFlicking(String category)
    {

    }

    @Override
    protected void onAnalyticsCategoryClick(String category)
    {

    }

    protected void setToolbarDateText(SaleTime saleTime)
    {
        String dateText = GourmetCurationManager.getInstance().getSaleTime().getDayOfDaysDateFormat("M.d(EEE)");
        setToolbarDateText(dateText);
    }
}
