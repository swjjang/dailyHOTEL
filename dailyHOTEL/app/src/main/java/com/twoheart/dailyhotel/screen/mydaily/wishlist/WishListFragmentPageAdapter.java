package com.twoheart.dailyhotel.screen.mydaily.wishlist;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by android_sam on 2016. 11. 1..
 */

public class WishListFragmentPageAdapter extends FragmentPagerAdapter
{
    private ArrayList<PlaceWishListFragment> mFragmentList;

    public WishListFragmentPageAdapter(FragmentManager fragmentManager, ArrayList<PlaceWishListFragment> fragmentList)
    {
        super(fragmentManager);

        mFragmentList = new ArrayList<>();

        if (fragmentList != null)
        {
            mFragmentList.addAll(fragmentList);
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
