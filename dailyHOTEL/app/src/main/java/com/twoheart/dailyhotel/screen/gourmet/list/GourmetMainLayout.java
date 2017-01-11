package com.twoheart.dailyhotel.screen.gourmet.list;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.place.adapter.PlaceListFragmentPagerAdapter;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.place.layout.PlaceMainLayout;

import java.util.ArrayList;

public class GourmetMainLayout extends PlaceMainLayout
{
    public GourmetMainLayout(Context context, OnEventListener mOnEventListener)
    {
        super(context, mOnEventListener);
    }

    @Override
    protected PlaceListFragmentPagerAdapter getPlaceListFragmentPagerAdapter(FragmentManager fragmentManager, int count, View bottomOptionLayout, PlaceListFragment.OnPlaceListFragmentListener listener)
    {
        PlaceListFragmentPagerAdapter placeListFragmentPagerAdapter = new PlaceListFragmentPagerAdapter(fragmentManager);

        ArrayList<GourmetListFragment> list = new ArrayList<>(count);

        for (int i = 0; i < count; i++)
        {
            GourmetListFragment gourmetListFragment = new GourmetListFragment();
            gourmetListFragment.setPlaceOnListFragmentListener(listener);
            gourmetListFragment.setBottomOptionLayout(bottomOptionLayout);
            list.add(gourmetListFragment);
        }

        placeListFragmentPagerAdapter.setPlaceFragmentList(list);

        return placeListFragmentPagerAdapter;
    }

    @Override
    protected void onAnalyticsCategoryFlicking(String category)
    {

    }

    @Override
    protected void onAnalyticsCategoryClick(String category)
    {

    }

    @Override
    protected String getAppBarTitle()
    {
        return mContext.getString(R.string.label_daily_gourmet);
    }

    protected void setToolbarDateText(SaleTime saleTime)
    {
        String dateText = saleTime.getDayOfDaysDateFormat("M.d(EEE)");
        setToolbarDateText(dateText);
    }
}
