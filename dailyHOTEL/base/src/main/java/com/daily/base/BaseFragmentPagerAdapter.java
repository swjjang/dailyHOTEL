package com.daily.base;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class BaseFragmentPagerAdapter<T extends BaseFragment> extends FragmentStatePagerAdapter
{
    private List<T> mFragmentList;

    public BaseFragmentPagerAdapter(FragmentManager fragmentManager)
    {
        super(fragmentManager);

        mFragmentList = new ArrayList<>();
    }

    @Override
    public T getItem(int position)
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

    public List<T> getFragmentList()
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

    public void setFragmentList(List<T> fragmentList)
    {
        if (mFragmentList == null || fragmentList == null)
        {
            return;
        }

        mFragmentList.clear();
        addListFragment(fragmentList);
    }

    public void addFragment(T fragment)
    {
        if (mFragmentList == null || fragment == null)
        {
            return;
        }

        mFragmentList.add(fragment);
    }

    public void addListFragment(List<T> fragmentList)
    {
        if (mFragmentList == null || fragmentList == null)
        {
            return;
        }

        mFragmentList.addAll(fragmentList);
    }
}
