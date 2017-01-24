package com.twoheart.dailyhotel.screen.home;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewConfiguration;

import com.twoheart.dailyhotel.util.ExLog;

/**
 * Created by android_sam on 2017. 1. 12..
 */

public class HomeHeaderBehavior extends AppBarLayout.Behavior
{
    private int mTouchSlop;
    private int mMaxFlingVelocity;
    private int mMinFlingVelocity;

    private boolean mIsScrolling;

    private int mHeaderHeight;
    private Context mContext;

    private int mMinOffset;
    private int mMaxOffset;

//    private View mTargetView;

    private int mSkippedOffset;

//    private ViewFlinger mViewFlinger;

    public HomeHeaderBehavior()
    {
        super();
    }

    public HomeHeaderBehavior(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        mContext = context;
        final ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        mTouchSlop = viewConfiguration.getScaledTouchSlop();
        mMaxFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
        mMinFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
    }

    int mLastFlingY = 0;

    @Override
    public boolean onNestedFling(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, float velocityX, float velocityY, boolean consumed)
    {
        ExLog.d(target.getClass().getSimpleName() + " , " + velocityY + " , " + consumed + " , " + mMinFlingVelocity);
        consumed = false;
        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
    }


    //    @Override
//    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, float velocityX, float velocityY)
//    {
//        int dy = target.getScrollY()  - mLastFlingY;
//        final int selfOffset = getTopAndBottomOffset();
//        final int newSelfOffset = Math.max(mMinOffset, Math.min(mMaxOffset, selfOffset - dy));
//        final int skipped = newSelfOffset - selfOffset + dy;
//
//        final boolean selfFinished = !setTopAndBottomOffset(newSelfOffset);
//
//        final int targetOffset;
//        final boolean targetFinished;
//        if (target instanceof ScrollingView) {
//            targetOffset = ((ScrollingView) target).computeVerticalScrollOffset();
////            target.scrollBy(0, skipped);
//        } else {
//            targetOffset = target.getScrollY();
////            target.scrollBy(0, skipped);
//        }
//
//        ExLog.d(target.getClass().getSimpleName() + " , " + selfFinished + " , " + newSelfOffset + " , " + selfOffset);
//
//        mLastFlingY = target.getScrollY();
//
//        return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY);
//    }
//
    //    @Override
//    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View directTargetChild, View target, int nestedScrollAxes)
//    {
//        if (mViewFlinger != null)
//        {
//            mViewFlinger.cancel();
//        }
//
//        if ((nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0)
//        {
//            mTargetView = target;
//
//            mIsScrolling = false;
//            mSkippedOffset = 0;
//
//            mHeaderHeight = Util.dpToPx(mContext, 84);
//
//            mMinOffset = -(child.getHeight() - mHeaderHeight);
//            mMaxOffset = 0;
//
//            return true;
//        }
//        return false;
//    }
//
//    @Override
//    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, int dx, int dy, int[] consumed)
//    {
//        if (!mIsScrolling)
//        {
//            mSkippedOffset += dy;
//
//            if (Math.abs(mSkippedOffset) >= mTouchSlop)
//            {
//                mIsScrolling = true;
//                target.getParent().requestDisallowInterceptTouchEvent(true);
//            }
//        }
//
//        if (mIsScrolling && dy != 0)
//        {
//            int min = -child.getTotalScrollRange();
//            int max = 0;
//
//            int currentOffset = getTopAndBottomOffset();
//            int newOffset = Math.min(Math.max(min, currentOffset - dy), max);
//
//            consumed[1] = newOffset - currentOffset;
//
//            setTopAndBottomOffset(newOffset);
//        }
//    }
//
//    @Override
//    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, float velocityX, float velocityY)
//    {
//        if (mViewFlinger != null)
//        {
//            mViewFlinger.cancel();
//        } else
//        {
//            mViewFlinger = new ViewFlinger(coordinatorLayout);
//        }
//
//        final int targetOffsetRange;
//        final int targetOffset;
//        if (target instanceof ScrollingView)
//        {
//            targetOffsetRange = ((ScrollingView) target).computeVerticalScrollRange() + target.getPaddingTop() + target.getPaddingBottom();
//            targetOffset = ((ScrollingView) target).computeVerticalScrollOffset();
//        } else
//        {
//            targetOffsetRange = Math.max(0, target.getHeight() - coordinatorLayout.getHeight());
//            targetOffset = target.getScrollY();
//        }
//
//        mViewFlinger.fling((int) velocityY, targetOffset, targetOffsetRange);
//
//        return true;
//    }
//
//    private class ViewFlinger implements Runnable
//    {
//        private final ScrollerCompat mScroller;
//        private final CoordinatorLayout mCoordinatorLayout;
//        private int mLastFlingY;
//
//        public ViewFlinger(CoordinatorLayout coordinatorLayout)
//        {
//            mScroller = ScrollerCompat.create(coordinatorLayout.getContext());
//            mCoordinatorLayout = coordinatorLayout;
//        }
//
//        public void fling(int velocity, int targetOffset, int targetOffsetRange)
//        {
//            if (Math.abs(velocity) < mMinFlingVelocity)
//            {
//                return;
//            }
//
//            velocity = Math.max(-mMaxFlingVelocity, Math.min(velocity, mMaxFlingVelocity));
//
//            mScroller.fling(0, 0, 0, velocity, 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
//            mCoordinatorLayout.postOnAnimation(this);
//
//            mLastFlingY = 0;
//        }
//
//        public void cancel()
//        {
//            mScroller.abortAnimation();
//        }
//
//        @Override
//        public void run()
//        {
//            if (mScroller.computeScrollOffset())
//            {
//                int dy = mScroller.getCurrY() - mLastFlingY;
//
//                final int selfOffset = getTopAndBottomOffset();
//                final int newSelfOffset = Math.max(mMinOffset, Math.min(mMaxOffset, selfOffset - dy));
//                final int skipped = newSelfOffset - selfOffset + dy;
//
//                final boolean selfFinished = !setTopAndBottomOffset(newSelfOffset);
//
//                final int targetOffset;
//                final boolean targetFinished;
//                if (mTargetView instanceof ScrollingView)
//                {
//                    targetOffset = ((ScrollingView) mTargetView).computeVerticalScrollOffset();
//                    mTargetView.scrollBy(0, skipped);
//                    targetFinished = (targetOffset == ((ScrollingView) mTargetView).computeVerticalScrollOffset());
//                } else
//                {
//                    targetOffset = mTargetView.getScrollY();
//                    mTargetView.scrollBy(0, skipped);
//                    targetFinished = (targetOffset == mTargetView.getScrollY());
//                }
//
//                final boolean scrollerFinished = mScroller.isFinished();
//
//                if (scrollerFinished || (selfFinished && targetFinished))
//                {
//                    return;
//                }
//
//                mCoordinatorLayout.postOnAnimation(this);
//
//                mLastFlingY = mScroller.getCurrY();
//            }
//        }
//    }

    //    @Override
    //    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, int dx, int dy, int[] consumed)
    //    {
    //        //        ExLog.d(child.getHeight() + " , " + child.getMinimumHeight() + " , " + child.getBottom() + " , " + target.getTop());
    //
    //
    //        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
    //    }
    //
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
