package com.twoheart.dailyhotel.widget;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;

import java.lang.reflect.Field;

public class DailyLoopViewPager extends ViewPager
{
    private static final boolean DEFAULT_BOUNDARY_CASHING = false;

    OnPageChangeListener mOuterPageChangeListener;
    DailyLoopPagerAdapterWrapper mAdapter;
    private boolean mBoundaryCaching = DEFAULT_BOUNDARY_CASHING;

    private DailyScrollerDuration mScroller = null;

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
                if (mPreviousPosition != position)
                {
                    mPreviousPosition = position;
                    if (mOuterPageChangeListener != null)
                    {
                        mOuterPageChangeListener.onPageSelected(position);
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
                int position = DailyLoopViewPager.super.getCurrentItem();
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

    public DailyLoopViewPager(Context context)
    {
        super(context);
        init(context);
    }

    public DailyLoopViewPager(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        try
        {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException e)
        {
            ExLog.d(e.toString());
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        try
        {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e)
        {
            ExLog.d(e.toString());
        }

        return false;
    }

    public void setSlideTime(int scrollFactor)
    {
        postInitViewPager();
        setScrollDurationFactor(scrollFactor);
    }

    /**
     * helper function which may be used when implementing FragmentPagerAdapter
     *
     * @param position
     * @param count
     * @return (position - 1)%count
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
        return mAdapter != null ? mAdapter.getRealAdapter() : super.getAdapter();
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
            mAdapter = new DailyLoopPagerAdapterWrapper(adapter);
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

        ViewGroup.LayoutParams layoutParams = getLayoutParams();

        if (layoutParams == null)
        {
            layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.getScreenWidth(context));
        } else
        {
            layoutParams.height = ScreenUtils.getScreenWidth(context);
        }

        setLayoutParams(layoutParams);
    }

    public View findViewWidthPosition(int position)
    {
        int count = getChildCount();

        for (int i = 0; i < count; i++)
        {
            View childView = getChildAt(i);

            Integer value = (Integer) childView.getTag(childView.getId());

            if (value != null && value == position)
            {
                return childView;
            }
        }

        return null;
    }

    private void postInitViewPager()
    {
        try
        {
            Class<?> viewpager = ViewPager.class;
            Field scroller = viewpager.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            Field interpolator = viewpager.getDeclaredField("sInterpolator");
            interpolator.setAccessible(true);

            mScroller = new DailyScrollerDuration(getContext(), (android.view.animation.Interpolator) interpolator.get(null));
            scroller.set(this, mScroller);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    public void setScrollDurationFactor(double scrollFactor)
    {
        if (mScroller != null)
        {
            mScroller.setScrollDurationFactor(scrollFactor);
        }
    }
}
