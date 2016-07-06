package com.twoheart.dailyhotel.screen.hotel.list;

import android.support.v4.app.FragmentManager;
import android.view.View;

import com.twoheart.dailyhotel.place.adapter.PlaceListFragmentPagerAdapter;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;

import java.util.ArrayList;

public class StayListFragmentPagerAdapter extends PlaceListFragmentPagerAdapter
{
    public StayListFragmentPagerAdapter(FragmentManager fragmentManager, int count, View bottomOptionLayout, PlaceListFragment.OnPlaceListFragmentListener listener)
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

        StayListFragment stayListFragment;

        for (int i = 0; i < count; i++)
        {
            stayListFragment = new StayListFragment();
            stayListFragment.setPlaceOnListFragmentListener(listener);
            stayListFragment.setBottomOptionLayout(bottomOptionLayout);
            list.add(stayListFragment);
        }
    }
}
