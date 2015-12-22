package com.twoheart.dailyhotel.screen.gourmetlist;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class GourmetFragmentPagerAdapter extends FragmentPagerAdapter
{
    private ArrayList<GourmetListFragment> mFragmentList;
    private int mTabCount;

    public GourmetFragmentPagerAdapter(FragmentManager fragmentManager, int count, GourmetMainFragment.OnUserActionListener listener)
    {
        super(fragmentManager);

        mTabCount = count;
        mFragmentList = new ArrayList<>(count);

        GourmetListFragment gourmetListFragment01 = new GourmetListFragment();
        gourmetListFragment01.setOnUserActionListener(listener);
        mFragmentList.add(gourmetListFragment01);

        GourmetListFragment gourmetListFragment02 = new GourmetListFragment();
        gourmetListFragment02.setOnUserActionListener(listener);
        mFragmentList.add(gourmetListFragment02);

        GourmetDaysListFragment gourmetListFragment03 = new GourmetDaysListFragment();
        gourmetListFragment03.setOnUserActionListener(listener);
        mFragmentList.add(gourmetListFragment03);
    }

    @Override
    public Fragment getItem(int position)
    {
        return mFragmentList.get(position);
    }

    public ArrayList<GourmetListFragment> getFragmentList()
    {
        return mFragmentList;
    }

    @Override
    public int getCount()
    {
        return mTabCount;
    }
}
