package com.twoheart.dailyhotel.screen.booking.detail;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.twoheart.dailyhotel.place.base.BaseFragment;

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

    @Override
    public int getCount()
    {
        return mFragmentList.size();
    }
}
