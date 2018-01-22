package com.daily.dailyhotel.screen.home.search;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class SearchFragmentPagerAdapter extends FragmentStatePagerAdapter
{
    private ArrayList<Fragment> mFragmentList;

    public SearchFragmentPagerAdapter(FragmentManager fragmentManager)
    {
        super(fragmentManager);

        mFragmentList = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position)
    {
        if (mFragmentList.size() == 0)
        {
            return null;
        }

        return mFragmentList.get(position);
    }

    @Override
    public int getItemPosition(Object object)
    {
        int position = mFragmentList.indexOf(object);
        return position == -1 ? POSITION_NONE : position;
    }

    public ArrayList<Fragment> getFragmentList()
    {
        return mFragmentList;
    }

    public void removeItem(int position)
    {
        mFragmentList.remove(position);
    }

    public void removeAll()
    {
        mFragmentList.clear();
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

    public void add(Fragment baseFragment)
    {
        if (mFragmentList == null || baseFragment == null)
        {
            return;
        }

        mFragmentList.add(baseFragment);
    }
}
