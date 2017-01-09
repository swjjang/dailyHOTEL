package com.twoheart.dailyhotel.screen.information.recentplace;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by android_sam on 2016. 10. 12..
 */

public class RecentPlacesFragmentPagerAdapter extends FragmentPagerAdapter
{
    private ArrayList<RecentPlacesListFragment> mFragmentList;

    public RecentPlacesFragmentPagerAdapter(FragmentManager fragmentManager, ArrayList<RecentPlacesListFragment> arrayList)
    {
        super(fragmentManager);

        mFragmentList = new ArrayList<>();
        if (arrayList != null)
        {
            mFragmentList.addAll(arrayList);
        }
    }

    @Override
    public Fragment getItem(int position)
    {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount()
    {
        return mFragmentList.size();
    }
}
