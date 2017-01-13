package com.twoheart.dailyhotel.screen.home;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

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
        //        ExLog.d(child.getHeight() + " , " + child.getMinimumHeight() + " , " + child.getBottom() + " , " + target.getTop());


        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
    }

    // 아래 방법으로 가능하기는 함. 보완이 필요하여 일단 검토 중
//    @Override
    //    public boolean onNestedFling(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, float velocityX, float velocityY, boolean consumed)
    //    {
    //
    //        ExLog.d(velocityX + " , " + velocityY + " , " + consumed);
    //
    //        if (!consumed)
    //        {
    //
    //        } else
    //        {
    //            if (velocityY < 0)
    //            {
    //
    //                int range = 0;
    //                for (int i = 0, z = child.getChildCount(); i < z; i++) {
    //                    final View subChild = child.getChildAt(i);
    //                    final AppBarLayout.LayoutParams lp = (AppBarLayout.LayoutParams) subChild.getLayoutParams();
    //                    int childHeight = subChild.getMeasuredHeight();
    //                    childHeight += lp.topMargin + lp.bottomMargin;
    //
    //                    final int flags = lp.getScrollFlags();
    //
    //                    if ((flags & AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL) != 0) {
    //                        // We're set to scroll so add the child's height
    //                        range += childHeight;
    //
    //                        if ((flags & AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED) != 0) {
    //                            // For a collapsing exit scroll, we to take the collapsed height into account.
    //                            // We also break the range straight away since later views can't scroll
    //                            // beneath us
    //                            range -= ViewCompat.getMinimumHeight(subChild) + child.getTop();
    //                            break;
    //                        }
    //                    } else {
    //                        // As soon as a view doesn't have the scroll flag, we end the range calculation.
    //                        // This is because views below can not scroll under a fixed view.
    //                        break;
    //                    }
    //                }
    //
    //
    //                // We're scrolling down
    //                final int targetScroll = -child.getTotalScrollRange() + range;
    //                if (getTopAndBottomOffset() + velocityY < targetScroll)
    //                {
    //                    // If we're currently not expanded more than the target scroll, we'll
    //                    // animate a fling
    //                    child.offsetTopAndBottom(targetScroll);
    //                }
    //            } else
    //            {
    ////                // We're scrolling up
    ////                final int targetScroll = -child.getTotalScrollRange();
    ////                if (getTopAndBottomOffset() + velocityY > targetScroll)
    ////                {
    ////                    // If we're currently not expanded less than the target scroll, we'll
    ////                    // animate a fling
    ////                    //                    child.dispatchNestedFling(velocityX, velocityX, consumed);
    ////                    child.setScrollY(targetScroll);
    ////                }
    //            }
    //        }
    //
    //        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
    //    }
}
