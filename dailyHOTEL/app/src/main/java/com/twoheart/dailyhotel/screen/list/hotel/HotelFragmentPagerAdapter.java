package com.twoheart.dailyhotel.screen.list.hotel;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class HotelFragmentPagerAdapter extends FragmentPagerAdapter
{
    private ArrayList<HotelListFragment> mFragmentList;
    private int mTabCount;

    public HotelFragmentPagerAdapter(FragmentManager fragmentManager, int count, HotelMainFragment.OnCommunicateListener listener)
    {
        super(fragmentManager);

        mTabCount = count;
        mFragmentList = new ArrayList<>(count);

        HotelListFragment hotelListFragment01 = new HotelListFragment();
        hotelListFragment01.setOnCommunicateListener(listener);
        mFragmentList.add(hotelListFragment01);

        HotelListFragment hotelListFragment02 = new HotelListFragment();
        hotelListFragment02.setOnCommunicateListener(listener);
        mFragmentList.add(hotelListFragment02);

        HotelDaysListFragment hotelListFragment03 = new HotelDaysListFragment();
        hotelListFragment03.setOnCommunicateListener(listener);
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
