package com.twoheart.dailyhotel.screen.search;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.twoheart.dailyhotel.place.fragment.PlaceSearchFragment;

import java.util.ArrayList;

public class SearchFragmentPagerAdapter extends FragmentPagerAdapter
{
    private ArrayList<PlaceSearchFragment> mFragmentList;

    public SearchFragmentPagerAdapter(FragmentManager fragmentManager, ArrayList<PlaceSearchFragment> arrayList)
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

    @Override
    public int getCount()
    {
        return mFragmentList.size();
    }
}
