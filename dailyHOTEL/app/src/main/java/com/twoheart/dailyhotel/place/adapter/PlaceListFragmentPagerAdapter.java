package com.twoheart.dailyhotel.place.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;

import java.util.ArrayList;

public class PlaceListFragmentPagerAdapter extends FragmentStatePagerAdapter
{
    private ArrayList<PlaceListFragment> mFragmentList;
    private int mTabCount;

    public PlaceListFragmentPagerAdapter(FragmentManager fragmentManager, int count, PlaceListFragment.OnListFragmentListener listener)
    {
        super(fragmentManager);

        mTabCount = count;
        mFragmentList = new ArrayList<>(count);

        PlaceListFragment placeListFragment;

        for (int i = 0; i < count; i++)
        {
            placeListFragment = new PlaceListFragment();
            placeListFragment.setListFragmentListener(listener);
            mFragmentList.add(placeListFragment);
        }
    }

    @Override
    public Fragment getItem(int position)
    {
        return mFragmentList.get(position);
    }

    public ArrayList<PlaceListFragment> getFragmentList()
    {
        return mFragmentList;
    }

    @Override
    public int getCount()
    {
        return mTabCount;
    }
}
