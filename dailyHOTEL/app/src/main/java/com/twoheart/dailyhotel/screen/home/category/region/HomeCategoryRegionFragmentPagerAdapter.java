package com.twoheart.dailyhotel.screen.home.category.region;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by android_sam on 2017. 4. 12..
 */

public class HomeCategoryRegionFragmentPagerAdapter extends FragmentPagerAdapter
{
    private ArrayList<HomeCategoryRegionListFragment> mFragmentList;

    public HomeCategoryRegionFragmentPagerAdapter(FragmentManager fragmentManager, ArrayList<HomeCategoryRegionListFragment> arrayList)
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

    public ArrayList<HomeCategoryRegionListFragment> getFragmentList()
    {
        return mFragmentList;
    }

    @Override
    public int getCount()
    {
        return mFragmentList.size();
    }
}
