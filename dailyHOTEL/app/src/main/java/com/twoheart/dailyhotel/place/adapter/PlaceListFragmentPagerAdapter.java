package com.twoheart.dailyhotel.place.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.util.ExLog;

import java.util.ArrayList;

public class PlaceListFragmentPagerAdapter<T> extends FragmentStatePagerAdapter
{
    private ArrayList<PlaceListFragment> mFragmentList;
    private int mTabCount;
    protected Class<T> classT;

    public PlaceListFragmentPagerAdapter(FragmentManager fragmentManager, int count, PlaceListFragment.OnPlaceListFragmentListener listener)
    {
        super(fragmentManager);

        mTabCount = count;
        mFragmentList = new ArrayList<>(count);

        PlaceListFragment placeListFragment;

        for (int i = 0; i < count; i++)
        {
            try
            {
                placeListFragment = (PlaceListFragment)classT.newInstance();
                placeListFragment.setListFragmentListener(listener);
                mFragmentList.add(placeListFragment);
            } catch (InstantiationException e)
            {
                ExLog.d(e.toString());
            } catch (IllegalAccessException e)
            {
                ExLog.d(e.toString());
            }
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
