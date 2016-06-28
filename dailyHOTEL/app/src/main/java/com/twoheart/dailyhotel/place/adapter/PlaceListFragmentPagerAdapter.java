package com.twoheart.dailyhotel.place.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;

import java.util.ArrayList;

public abstract class PlaceListFragmentPagerAdapter extends FragmentStatePagerAdapter
{
    private ArrayList<PlaceListFragment> mFragmentList;
    private int mTabCount;

    protected abstract void makePlaceListFragment(ArrayList<PlaceListFragment> list, int count, PlaceListFragment.OnPlaceListFragmentListener listener);

    public PlaceListFragmentPagerAdapter(FragmentManager fragmentManager, int count, PlaceListFragment.OnPlaceListFragmentListener listener)
    {
        super(fragmentManager);

        mTabCount = count;
        mFragmentList = new ArrayList<>(count);

        makePlaceListFragment(mFragmentList, count, listener);
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
