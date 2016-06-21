package com.twoheart.dailyhotel.screen.hotel.list;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class StayListFragmentPagerAdapter extends FragmentStatePagerAdapter
{
    private ArrayList<StayListFragment> mFragmentList;
    private int mTabCount;

    public StayListFragmentPagerAdapter(FragmentManager fragmentManager, int count, StayMainFragment.OnCommunicateListener listener)
    {
        super(fragmentManager);

        mTabCount = count;
        mFragmentList = new ArrayList<>(count);

        StayListFragment hotelCategoryListFragment;

        for (int i = 0; i < count; i++)
        {
            hotelCategoryListFragment = new StayListFragment();
            hotelCategoryListFragment.setOnCommunicateListener(listener);
            mFragmentList.add(hotelCategoryListFragment);
        }
    }

    @Override
    public Fragment getItem(int position)
    {
        return mFragmentList.get(position);
    }

    public ArrayList<StayListFragment> getFragmentList()
    {
        return mFragmentList;
    }

    @Override
    public int getCount()
    {
        return mTabCount;
    }
}
