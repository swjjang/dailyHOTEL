package com.twoheart.dailyhotel.screen.bookingdetail;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.twoheart.dailyhotel.screen.common.BaseFragment;

import java.util.ArrayList;

public class BookingDetailFragmentPagerAdapter extends FragmentPagerAdapter
{
    private ArrayList<BaseFragment> mFragmentList;

    public BookingDetailFragmentPagerAdapter(FragmentManager fragmentManager, ArrayList<BaseFragment> arrayList)
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

    public ArrayList<BaseFragment> getFragmentList()
    {
        return mFragmentList;
    }

    @Override
    public int getCount()
    {
        return mFragmentList.size();
    }
}
