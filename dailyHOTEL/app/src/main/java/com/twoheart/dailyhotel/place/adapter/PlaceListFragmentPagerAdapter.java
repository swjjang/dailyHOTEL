package com.twoheart.dailyhotel.place.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;

import java.util.ArrayList;

public class PlaceListFragmentPagerAdapter extends FragmentStatePagerAdapter
{
    private ArrayList<PlaceListFragment> mFragmentList;

    public PlaceListFragmentPagerAdapter(FragmentManager fragmentManager)
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

    public ArrayList<PlaceListFragment> getFragmentList()
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

    public void setPlaceFragmentList(ArrayList<? extends PlaceListFragment> list)
    {
        if (mFragmentList == null)
        {
            return;
        }

        mFragmentList.clear();
        addPlaceListFragment(list);
    }

    public void addPlaceListFragment(ArrayList<? extends PlaceListFragment> list)
    {
        if (mFragmentList == null)
        {
            return;
        }

        if (list != null)
        {
            mFragmentList.addAll(list);
        }
    }
}
