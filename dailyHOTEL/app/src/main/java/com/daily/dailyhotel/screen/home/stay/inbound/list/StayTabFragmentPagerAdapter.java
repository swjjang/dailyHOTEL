package com.daily.dailyhotel.screen.home.stay.inbound.list;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class StayTabFragmentPagerAdapter extends FragmentStatePagerAdapter
{
    private List<StayListFragment> mFragmentList;

    public StayTabFragmentPagerAdapter(FragmentManager fragmentManager)
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

    public List<StayListFragment> getFragmentList()
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

    public void setPlaceFragmentList(List<StayListFragment> fragmentList)
    {
        if (mFragmentList == null || fragmentList == null)
        {
            return;
        }

        mFragmentList.clear();
        addPlaceListFragment(fragmentList);
    }

    public void addPlaceListFragment(List<StayListFragment> fragmentList)
    {
        if (mFragmentList == null || fragmentList == null)
        {
            return;
        }

        mFragmentList.addAll(fragmentList);
    }
}
