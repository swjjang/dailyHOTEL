package com.twoheart.dailyhotel.screen.search.gourmet.result;

import android.support.v4.app.FragmentManager;
import android.view.View;

import com.twoheart.dailyhotel.place.adapter.PlaceListFragmentPagerAdapter;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;

import java.util.ArrayList;

public class GourmetSearchResultListFragmentPagerAdapter extends PlaceListFragmentPagerAdapter
{
    public GourmetSearchResultListFragmentPagerAdapter(FragmentManager fragmentManager, int count, View bottomOptionLayout, PlaceListFragment.OnPlaceListFragmentListener listener)
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

        GourmetSearchResultListFragment gourmetListFragment;

        for (int i = 0; i < count; i++)
        {
            gourmetListFragment = new GourmetSearchResultListFragment();
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

        GourmetSearchResultListFragment gourmetListFragment;

        for (int i = 0; i < count; i++)
        {
            gourmetListFragment = new GourmetSearchResultListFragment();
            gourmetListFragment.setPlaceOnListFragmentListener(listener);
            gourmetListFragment.setBottomOptionLayout(bottomOptionLayout);
            list.add(gourmetListFragment);
        }
    }
}
