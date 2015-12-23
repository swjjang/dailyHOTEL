package com.twoheart.dailyhotel.screen.region;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class RegionFragmentPagerAdapter extends FragmentPagerAdapter
{
    private ArrayList<RegionListFragment> mFragmentList;

    public RegionFragmentPagerAdapter(FragmentManager fragmentManager, ArrayList<RegionListFragment> arrayList)
    {
        super(fragmentManager);

        mFragmentList = new ArrayList<>();
        mFragmentList.addAll(arrayList);
    }

    @Override
    public Fragment getItem(int position)
    {
        return mFragmentList.get(position);
    }

    public ArrayList<RegionListFragment> getFragmentList()
    {
        return mFragmentList;
    }

    @Override
    public int getCount()
    {
        return mFragmentList.size();
    }
}
