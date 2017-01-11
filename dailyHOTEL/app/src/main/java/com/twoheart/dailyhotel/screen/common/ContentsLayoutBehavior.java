package com.twoheart.dailyhotel.screen.common;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.ExLog;

import java.util.List;

public class ContentsLayoutBehavior extends ViewOffsetBehavior
{
    private static final String TAG = ContentsLayoutBehavior.class.getSimpleName();

    public ContentsLayoutBehavior()
    {
    }

    public ContentsLayoutBehavior(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, View child, int layoutDirection)
    {
        return super.onLayoutChild(parent, child, layoutDirection);
    }

    @Override
    public boolean onMeasureChild(CoordinatorLayout parent, View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed)
    {
        if (child.getLayoutParams().height == ViewGroup.LayoutParams.MATCH_PARENT)
        {
            List dependencies = parent.getDependencies(child);
            if (dependencies.isEmpty())
            {
                return false;
            }

            LinearLayout appBarLayout = findAppBarLayout(dependencies);
            if (appBarLayout != null && ViewCompat.isLaidOut(appBarLayout))
            {
                int height = parent.getHeight() - appBarLayout.findViewById(R.id.toolbarLayout).getMeasuredHeight();
                int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.AT_MOST);
                parent.onMeasureChild(child, parentWidthMeasureSpec, widthUsed, heightMeasureSpec, heightUsed);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency)
    {
        CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) dependency.getLayoutParams()).getBehavior();
        if (behavior instanceof AppBarBehavior)
        {
            int headerOffset = ((AppBarBehavior) behavior).getTopAndBottomOffset();
            int contentsOffset = dependency.getHeight() + headerOffset;
            setTopAndBottomOffset(contentsOffset);
        }

        return false;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency)
    {
        return dependency.getId() == R.id.appBarLayout;
    }

    private static LinearLayout findAppBarLayout(List<View> views)
    {
        int i = 0;

        for (int z = views.size(); i < z; ++i)
        {
            View view = views.get(i);
            if (view.getId() == R.id.appBarLayout)
            {
                return (LinearLayout) view;
            }
        }

        return null;
    }
}
