/*
 * Copyright (C) 2013 Leszek Mzyk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.twoheart.dailyhotel.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import java.lang.reflect.Field;

/**
 * A ViewPager subclass enabling infinte scrolling of the viewPager elements
 * <p/>
 * When used for paginating views (in opposite to fragments), no code changes
 * should be needed only change xml's from
 * <android.support.v4.view.ViewPager> to <com.imbryk.viewPager.LoopViewPager>
 * <p/>
 * If "blinking" can be seen when paginating to first or last view, simply call
 * seBoundaryCaching( true ), or change DEFAULT_BOUNDARY_CASHING to true
 * <p/>
 * When using a FragmentPagerAdapter or FragmentStatePagerAdapter, additional
 * changes in the adapter must be done. The adapter must be prepared to create 2
 * extra items e.g.:
 * <p/>
 * The original adapter creates 4 items: [0,1,2,3] The modified adapter will
 * have to create 6 items [0,1,2,3,4,5] with mapping
 * realPosition=(position-1)%count [0->3, 1->0, 2->1, 3->2, 4->3, 5->0]
 */
public class LoopViewPager extends ViewPager
{
    private static final boolean DEFAULT_BOUNDARY_CASHING = false;

    private OnPageChangeListener mOuterPageChangeListener;
    private LoopPagerAdapterWrapper mAdapter;
    private boolean mBoundaryCaching = DEFAULT_BOUNDARY_CASHING;

    private ScrollerCustomDuration mScroller = null;
    private OnPageChangeListener onPageChangeListener = new OnPageChangeListener()
    {
        private float mPreviousOffset = -1;
        private float mPreviousPosition = -1;

        @Override
        public void onPageSelected(int position)
        {
            if (mAdapter != null)
            {
                int realPosition = mAdapter.toRealPosition(position);

                if (mPreviousPosition != realPosition)
                {
                    mPreviousPosition = realPosition;
                    if (mOuterPageChangeListener != null)
                    {
                        mOuterPageChangeListener.onPageSelected(realPosition);
                    }
                }
            } else
            {
                int realPosition = position;

                if (mPreviousPosition != realPosition)
                {
                    mPreviousPosition = realPosition;
                    if (mOuterPageChangeListener != null)
                    {
                        mOuterPageChangeListener.onPageSelected(realPosition);
                    }
                }
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {
            int realPosition = position;
            if (mAdapter != null)
            {
                realPosition = mAdapter.toRealPosition(position);

                if (positionOffset == 0 && mPreviousOffset == 0 && (position == 0 || position == mAdapter.getCount() - 1))
                {
                    setCurrentItem(realPosition, false);
                }

                mPreviousOffset = positionOffset;
                if (mOuterPageChangeListener != null)
                {
                    mOuterPageChangeListener.onPageScrolled(realPosition, positionOffset, positionOffsetPixels);
                }
            } else
            {
                if (mOuterPageChangeListener != null)
                {
                    mOuterPageChangeListener.onPageScrolled(realPosition, positionOffset, positionOffsetPixels);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state)
        {
            if (mAdapter != null)
            {
                int position = LoopViewPager.super.getCurrentItem();
                int realPosition = mAdapter.toRealPosition(position);
                if (state == ViewPager.SCROLL_STATE_IDLE && (position == 0 || position == mAdapter.getCount() - 1))
                {
                    setCurrentItem(realPosition, false);
                }
            }

            if (mOuterPageChangeListener != null)
            {
                mOuterPageChangeListener.onPageScrollStateChanged(state);
            }
        }
    };

    public LoopViewPager(Context context)
    {
        super(context);
        init(context);
    }

    public LoopViewPager(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    /**
     * helper function which may be used when implementing FragmentPagerAdapter
     *
     * @param position
     * @param count
     * @return (position-1)%count
     */
    public static int toRealPosition(int position, int count)
    {
        position = position - 1;
        if (position < 0)
        {
            position += count;
        } else
        {
            position = position % count;
        }
        return position;
    }

    /**
     * If set to true, the boundary views (i.e. first and last) will never be
     * destroyed This may help to prevent "blinking" of some views
     *
     * @param flag
     */
    public void setBoundaryCaching(boolean flag)
    {
        mBoundaryCaching = flag;
        if (mAdapter != null)
        {
            mAdapter.setBoundaryCaching(flag);
        }
    }

    @Override
    public PagerAdapter getAdapter()
    {
        return mAdapter != null ? mAdapter.getRealAdapter() : null;
    }

    @Override
    public void setAdapter(PagerAdapter adapter)
    {
        if (adapter != null && adapter.getCount() == 1)
        {
            mAdapter = null;
            super.setAdapter(adapter);
        } else
        {
            mAdapter = new LoopPagerAdapterWrapper(adapter);
            mAdapter.setBoundaryCaching(mBoundaryCaching);
            super.setAdapter(mAdapter);
            setCurrentItem(0, false);
        }
    }

    @Override
    public int getCurrentItem()
    {
        return mAdapter != null ? mAdapter.toRealPosition(super.getCurrentItem()) : 0;
    }

    @Override
    public void setCurrentItem(int item)
    {
        if (getCurrentItem() != item)
        {
            setCurrentItem(item, true);
        }
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll)
    {
        if (mAdapter == null)
        {
            super.setCurrentItem(item, smoothScroll);
        } else
        {
            int realItem = mAdapter.toInnerPosition(item);
            super.setCurrentItem(realItem, smoothScroll);
        }
    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener)
    {
        mOuterPageChangeListener = listener;
    }

    private void init(Context context)
    {
        super.setOnPageChangeListener(onPageChangeListener);

        postInitViewPager();
    }

    public View findViewWidthPosition(int position)
    {
        int count = getChildCount();

        for (int i = 0; i < count; i++)
        {
            View childView = getChildAt(i);

            Integer value = (Integer) childView.getTag(childView.getId());

            if (value != null && value.intValue() == position)
            {
                return childView;
            }
        }

        return null;
    }

    /**
     * Override the Scroller instance with our own class so we can change the
     * duration
     */
    private void postInitViewPager()
    {
        try
        {
            Class<?> viewpager = ViewPager.class;
            Field scroller = viewpager.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            Field interpolator = viewpager.getDeclaredField("sInterpolator");
            interpolator.setAccessible(true);

            mScroller = new ScrollerCustomDuration(getContext(), (android.view.animation.Interpolator) interpolator.get(null));
            scroller.set(this, mScroller);
        } catch (Exception e)
        {
        }
    }

    /**
     * Set the factor by which the duration will change
     */
    public void setScrollDurationFactor(double scrollFactor)
    {
        if (mScroller != null)
        {
            mScroller.setScrollDurationFactor(scrollFactor);
        }
    }
}
