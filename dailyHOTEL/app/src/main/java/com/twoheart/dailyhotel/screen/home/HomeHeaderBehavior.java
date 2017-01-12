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

public class HomeHeaderBehavior extends AppBarLayout.Behavior
{
    public HomeHeaderBehavior()
    {
        super();
    }

    public HomeHeaderBehavior(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, int dx, int dy, int[] consumed)
    {
        ExLog.d(child.getHeight() + " , " + child.getMinimumHeight() + " , " + child.getBottom() + " , " + target.getTop());





        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
    }

//    @Override
//    public boolean layoutDependsOn(CoordinatorLayout parent, AppBarLayout child, View dependency)
//    {
//        return dependency instanceof NestedScrollView;
//
////        return super.layoutDependsOn(parent, child, dependency);
//    }
}
