package com.twoheart.dailyhotel.screen.gourmet.list;

import android.support.v4.app.FragmentManager;

import com.twoheart.dailyhotel.place.adapter.PlaceListFragmentPagerAdapter;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;

import java.util.ArrayList;

public class GourmetListFragmentPagerAdapter extends PlaceListFragmentPagerAdapter
{
    public GourmetListFragmentPagerAdapter(FragmentManager fragmentManager, int count, PlaceListFragment.OnPlaceListFragmentListener listener)
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

        GourmetListFragment_v2 gourmetListFragment;

        for (int i = 0; i < count; i++)
        {
            gourmetListFragment = new GourmetListFragment_v2();
            gourmetListFragment.setListFragmentListener(listener);
            list.add(gourmetListFragment);
        }
    }
}
