package com.twoheart.dailyhotel.screen.home;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

import com.twoheart.dailyhotel.util.ExLog;

/**
 * Created by android_sam on 2017. 1. 12..
 */

public class HomeScrollViewBehavior extends AppBarLayout.ScrollingViewBehavior
{
    public HomeScrollViewBehavior()
    {
        super();
    }

    public HomeScrollViewBehavior(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }


    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dx, int dy, int[] consumed)
    {
        ExLog.d(child.getHeight() + " , " + target.getTop());
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
    }

//    @Override
//    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency)
//    {
//        return dependency instanceof AppBarLayout;
////        return super.layoutDependsOn(parent, child, dependency);
//    }
}
