package com.twoheart.dailyhotel.screen.hotellist;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class HotelFragmentPagerAdapter extends FragmentPagerAdapter
{
    private ArrayList<HotelListFragment> mFragmentList;
    private HotelMainFragment.OnUserActionListener mOnUserActionListener;
    private int mTabCount;

    public HotelFragmentPagerAdapter(FragmentManager fragmentManager, int count, HotelMainFragment.OnUserActionListener listener)
    {
        super(fragmentManager);

        mTabCount = count;
        mFragmentList = new ArrayList<>(count);

        HotelListFragment hotelListFragment01 = new HotelListFragment();
        hotelListFragment01.setOnUserActionListener(mOnUserActionListener);
        mFragmentList.add(hotelListFragment01);

        HotelListFragment hotelListFragment02 = new HotelListFragment();
        hotelListFragment02.setOnUserActionListener(mOnUserActionListener);
        mFragmentList.add(hotelListFragment02);

        HotelDaysListFragment hotelListFragment03 = new HotelDaysListFragment();
        hotelListFragment03.setOnUserActionListener(mOnUserActionListener);
        mFragmentList.add(hotelListFragment03);
    }

    @Override
    public Fragment getItem(int position)
    {
        return mFragmentList.get(position);
    }

    public ArrayList<HotelListFragment> getFragmentList()
    {
        return mFragmentList;
    }

    @Override
    public int getCount()
    {
        return mTabCount;
    }
}
