package com.twoheart.dailyhotel.screen.search.stay.result;

import android.support.v4.app.FragmentManager;
import android.view.View;

import com.twoheart.dailyhotel.place.adapter.PlaceListFragmentPagerAdapter;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;

import java.util.ArrayList;

public class StaySearchResultListFragmentPagerAdapter extends PlaceListFragmentPagerAdapter
{
    public StaySearchResultListFragmentPagerAdapter(FragmentManager fragmentManager, int count, View bottomOptionLayout, PlaceListFragment.OnPlaceListFragmentListener listener)
    {
        super(fragmentManager, count, bottomOptionLayout, listener);
    }

    @Override
    protected void makePlaceListFragment(ArrayList<PlaceListFragment> list, int count, View bottomOptionLayout, PlaceListFragment.OnPlaceListFragmentListener listener)
    {
        if (list == null)
        {
            return;
        }

        list.clear();

        StaySearchResultListFragment stayListFragment;

        for (int i = 0; i < count; i++)
        {
            stayListFragment = new StaySearchResultListFragment();
            stayListFragment.setPlaceOnListFragmentListener(listener);
            stayListFragment.setBottomOptionLayout(bottomOptionLayout);
            list.add(stayListFragment);
        }
    }
}
