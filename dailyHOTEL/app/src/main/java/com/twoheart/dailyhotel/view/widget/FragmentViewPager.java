/**
 *
 */
package com.twoheart.dailyhotel.view.widget;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class FragmentViewPager extends LinearLayout
{
    private ArrayList<? extends Fragment> mDataList;
    private ArrayList<String> mPageTitleList;
    private FragementViewPagerAdapter mAdapter;

    private CustomViewPager mViewPager;

    /**
     * @param context
     */
    public FragmentViewPager(Context context)
    {
        super(context);
        init(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public FragmentViewPager(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public FragmentViewPager(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context)
    {
        mViewPager = new CustomViewPager(context);
        mViewPager.setId(100);
        //		mViewPager.setOnPageChangeListener(mOnPageChangeListener);

        this.addView(mViewPager);
    }

    public void setData(ArrayList<? extends Fragment> dataList)
    {
        mDataList = dataList;
    }

    public void setData(ArrayList<? extends Fragment> dataList, ArrayList<String> tabList)
    {
        mDataList = dataList;
        mPageTitleList = tabList;
    }

    public void setAdapter(FragmentManager fm)
    {
        mViewPager.setOffscreenPageLimit(mDataList.size());
        mAdapter = new FragementViewPagerAdapter(fm);
        mViewPager.setAdapter(mAdapter);
    }

    /**
     * 현재 선택된 fragment를 넘겨준다
     *
     * @return current fragment
     */
    public Fragment getCurrentFragment()
    {
        return mAdapter.getItem(mViewPager.getCurrentItem());
    }

    public Fragment getCurrentFragment(int index)
    {
        return mAdapter.getItem(index);
    }

    public int getCurrentItem()
    {
        return mViewPager.getCurrentItem();
    }

    public void setCurrentItem(int position)
    {
        mViewPager.setCurrentItem(position);
    }

    public ViewPager getViewPager()
    {
        return mViewPager;
    }

    public void setPagingEnable(boolean enable)
    {
        mViewPager.setPagingEnable(enable);
    }

    public void setPageMargin(int margin)
    {
        mViewPager.setPageMargin(margin);
    }

    private class FragementViewPagerAdapter extends FragmentPagerAdapter
    {

        public FragementViewPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int i)
        {
            return mDataList.get(i);
        }

        @Override
        public int getCount()
        {
            return mDataList.size();
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            if (mPageTitleList != null)
            {
                String title = mPageTitleList.get(position);
                if (title != null)
                {
                    return title;
                }
            }

            return super.getPageTitle(position);
        }

    }

    private class CustomViewPager extends ViewPager
    {
        private boolean mEnable = true;

        public CustomViewPager(Context context)
        {
            super(context);
        }

        public CustomViewPager(Context context, AttributeSet attrs)
        {
            super(context, attrs);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent event)
        {
            return true == mEnable && super.onInterceptTouchEvent(event);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event)
        {
            if (true == mEnable)
            {
                try
                {
                    return super.onTouchEvent(event);
                } catch (Exception e)
                {
                    return true;
                }
            } else
            {
                return false;
            }
        }

        public void setPagingEnable(boolean enable)
        {
            mEnable = enable;
        }
    }
}
