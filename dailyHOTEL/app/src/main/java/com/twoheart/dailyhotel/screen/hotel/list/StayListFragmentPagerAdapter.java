package com.twoheart.dailyhotel.screen.hotel.list;

import android.support.v4.app.FragmentManager;

import com.twoheart.dailyhotel.place.adapter.PlaceListFragmentPagerAdapter;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;

import java.util.ArrayList;

public class StayListFragmentPagerAdapter extends PlaceListFragmentPagerAdapter
{
    public StayListFragmentPagerAdapter(FragmentManager fragmentManager, int count, PlaceListFragment.OnPlaceListFragmentListener listener)
    {
        super(fragmentManager, count, listener);
    }

    @Override
    protected void makePlaceListFragment(ArrayList<PlaceListFragment> list, int count, PlaceListFragment.OnPlaceListFragmentListener listener)
    {
        if (list == null)
        {
            return;
        }

        list.clear();

        StayListFragment stayListFragment;

        for (int i = 0; i < count; i++)
        {
            stayListFragment = new StayListFragment();
            stayListFragment.setListFragmentListener(listener);
            list.add(stayListFragment);
        }
    }
}
