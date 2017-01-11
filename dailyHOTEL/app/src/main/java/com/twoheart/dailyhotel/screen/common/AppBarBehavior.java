package com.twoheart.dailyhotel.screen.common;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;

import com.twoheart.dailyhotel.R;

public class AppBarBehavior extends ViewOffsetBehavior<LinearLayout>
{
    private int mTouchSlop;
    private boolean mIsScrolling;

    private View mToolbarLayout;
    private int mSkippedOffset;

    public AppBarBehavior()
    {

    }

    public AppBarBehavior(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        final ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        mTouchSlop = viewConfiguration.getScaledTouchSlop();
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, LinearLayout child, int layoutDirection)
    {
        return super.onLayoutChild(parent, child, layoutDirection);
    }

    @Override
    public boolean onMeasureChild(CoordinatorLayout parent, LinearLayout child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed)
    {
        return super.onMeasureChild(parent, child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, LinearLayout child, View dependency)
    {
        return super.onDependentViewChanged(parent, child, dependency);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, LinearLayout child, View dependency)
    {
        return super.layoutDependsOn(parent, child, dependency);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, LinearLayout child, View directTargetChild, View target, int nestedScrollAxes)
    {
        if ((nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0)
        {
            mIsScrolling = false;
            mSkippedOffset = 0;

            if (mToolbarLayout == null)
            {
                mToolbarLayout = child.findViewById(R.id.toolbarLayout);
            }

            return true;
        }

        return false;
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, LinearLayout child, View target, int dx, int dy, int[] consumed)
    {
        if (mIsScrolling == false)
        {
            mSkippedOffset += dy;

            if (Math.abs(mSkippedOffset) >= mTouchSlop)
            {
                mIsScrolling = true;
                target.getParent().requestDisallowInterceptTouchEvent(true);
            }
        }

        if (mIsScrolling && dy != 0)
        {
            int min = -mToolbarLayout.getHeight();
            int max = 0;

            int currentOffset = getTopAndBottomOffset();
            int newOffset = Math.min(Math.max(min, currentOffset - dy), max);

            consumed[1] = newOffset - currentOffset;

            setTopAndBottomOffset(newOffset);
        }
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, LinearLayout child, View target)
    {
        super.onStopNestedScroll(coordinatorLayout, child, target);

        if (Math.abs(mToolbarLayout.getTop()) > mToolbarLayout.getHeight() / 2)
        {
        } else
        {
        }

        child.scrollTo(0, 0);
    }
}
