package com.twoheart.dailyhotel.screen.gourmet.list;

import android.support.v4.app.FragmentManager;
import android.view.View;

import com.twoheart.dailyhotel.place.adapter.PlaceListFragmentPagerAdapter;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;

import java.util.ArrayList;

public class GourmetListFragmentPagerAdapter extends PlaceListFragmentPagerAdapter
{
    public GourmetListFragmentPagerAdapter(FragmentManager fragmentManager, int count, View bottomOptionLayout, PlaceListFragment.OnPlaceListFragmentListener listener)
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

        GourmetListFragment gourmetListFragment;

        for (int i = 0; i < count; i++)
        {
            gourmetListFragment = new GourmetListFragment();
            gourmetListFragment.setPlaceOnListFragmentListener(listener);
            gourmetListFragment.setBottomOptionLayout(bottomOptionLayout);
            list.add(gourmetListFragment);
        }
    }

    @Override
    protected void addPlaceListFragment(ArrayList<PlaceListFragment> list, int count, View bottomOptionLayout, PlaceListFragment.OnPlaceListFragmentListener listener)
    {
        if (list == null)
        {
            return;
        }

        GourmetListFragment gourmetListFragment;

        for (int i = 0; i < count; i++)
        {
            gourmetListFragment = new GourmetListFragment();
            gourmetListFragment.setPlaceOnListFragmentListener(listener);
            gourmetListFragment.setBottomOptionLayout(bottomOptionLayout);
            list.add(gourmetListFragment);
        }
    }
}
