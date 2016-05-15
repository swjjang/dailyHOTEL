package com.twoheart.dailyhotel.place.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.twoheart.dailyhotel.place.fragment.PlaceRegionListFragment;

import java.util.ArrayList;

public class PlaceRegionFragmentPagerAdapter extends FragmentPagerAdapter
{
    private ArrayList<PlaceRegionListFragment> mFragmentList;

    public PlaceRegionFragmentPagerAdapter(FragmentManager fragmentManager, ArrayList<PlaceRegionListFragment> arrayList)
    {
        super(fragmentManager);

        mFragmentList = new ArrayList<>();
        mFragmentList.addAll(arrayList);
    }

    public void removeItem(int position)
    {
        mFragmentList.remove(position);
    }

    @Override
    public Fragment getItem(int position)
    {
        return mFragmentList.get(position);
    }

    public ArrayList<PlaceRegionListFragment> getFragmentList()
    {
        return mFragmentList;
    }

    @Override
    public int getCount()
    {
        return mFragmentList.size();
    }
}
