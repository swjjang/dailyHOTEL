package com.twoheart.dailyhotel.place.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;

import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;

import java.util.ArrayList;

public abstract class PlaceListFragmentPagerAdapter extends FragmentStatePagerAdapter
{
    private ArrayList<PlaceListFragment> mFragmentList;

    protected abstract void makePlaceListFragment(ArrayList<PlaceListFragment> list, int count, View bottomOptionLayout, PlaceListFragment.OnPlaceListFragmentListener listener);

    public PlaceListFragmentPagerAdapter(FragmentManager fragmentManager, int count, View bottomOptionLayout, PlaceListFragment.OnPlaceListFragmentListener listener)
    {
        super(fragmentManager);

        mFragmentList = new ArrayList<>(count);

        makePlaceListFragment(mFragmentList, count, bottomOptionLayout, listener);
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

    public void removeItem(int position)
    {
        mFragmentList.remove(position);
    }

    @Override
    public int getCount()
    {
        if (mFragmentList == null)
        {
            return 0;
        }

        return mFragmentList.size();
    }
}
