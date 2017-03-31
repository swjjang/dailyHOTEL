package com.twoheart.dailyhotel.widget;

import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by android_sam on 2017. 3. 22..
 */
public class DailyPagerSnapHelper extends SnapHelper
{
    private static final int MAX_SCROLL_ON_FLING_DURATION = 100; // ms

    static final float MILLISECONDS_PER_INCH = 100f;

    // Orientation helpers are lazily created per LayoutManager.
    @Nullable
    private OrientationHelper mVerticalHelper;
    @Nullable
    private OrientationHelper mHorizontalHelper;

    RecyclerView mRecyclerView;

    private int mCenterPosition;
    private boolean mForwardDirection;

    @Override
    public void attachToRecyclerView(@Nullable RecyclerView recyclerView) throws IllegalStateException
    {
        mRecyclerView = recyclerView;
        super.attachToRecyclerView(recyclerView);
    }

    @Nullable
    @Override
    public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager,//
                                              @NonNull View targetView)
    {
        int[] out = new int[2];
        if (layoutManager.canScrollHorizontally())
        {
            out[0] = distanceToCenter(layoutManager, targetView, getHorizontalHelper(layoutManager));
        } else
        {
            out[0] = 0;
        }

        if (layoutManager.canScrollVertically())
        {
            out[1] = distanceToCenter(layoutManager, targetView, getVerticalHelper(layoutManager));
        } else
        {
            out[1] = 0;
        }
        return out;
    }

    @Nullable
    @Override
    public View findSnapView(RecyclerView.LayoutManager layoutManager)
    {
        int itemCount = layoutManager.getItemCount();

        if (layoutManager.canScrollVertically())
        {
            if (mCenterPosition == itemCount - 1 && mForwardDirection == true)
            {
                return findEndView(layoutManager, getVerticalHelper(layoutManager));
            } else if (mCenterPosition == 0 && mForwardDirection == false)
            {
                return findStartView(layoutManager, getVerticalHelper(layoutManager));
            }

            return findCenterView(layoutManager, getVerticalHelper(layoutManager));
        } else if (layoutManager.canScrollHorizontally())
        {
            //            ExLog.d(itemCount + " : " + mCenterPosition + " : " + mForwardDirection);

            if (mCenterPosition == itemCount - 1 && mForwardDirection == true)
            {
                return findEndView(layoutManager, getHorizontalHelper(layoutManager));
            } else if (mCenterPosition == 0 && mForwardDirection == false)
            {
                return findStartView(layoutManager, getHorizontalHelper(layoutManager));
            }

            return findCenterView(layoutManager, getHorizontalHelper(layoutManager));
        }
        return null;
    }

    @Override
    public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY)
    {
        final int itemCount = layoutManager.getItemCount();
        if (itemCount == 0)
        {
            return RecyclerView.NO_POSITION;
        }

        //        ExLog.d("velocityX : " + velocityX + " , velocityY : " + velocityY);

        final boolean forwardDirection;
        if (layoutManager.canScrollHorizontally())
        {
            forwardDirection = velocityX > 0;
        } else
        {
            forwardDirection = velocityY > 0;
        }

        View mStartMostChildView = null;
        if (layoutManager.canScrollVertically())
        {
            if (mCenterPosition == itemCount - 1 && forwardDirection == true)
            {
                mStartMostChildView = findEndView(layoutManager, getVerticalHelper(layoutManager));
            } else if (forwardDirection == true)
            {
                mStartMostChildView = findCenterView(layoutManager, getVerticalHelper(layoutManager));
            } else
            {
                mStartMostChildView = findStartView(layoutManager, getVerticalHelper(layoutManager));
            }
        } else if (layoutManager.canScrollHorizontally())
        {
            if (mCenterPosition == itemCount - 1 && forwardDirection == true)
            {
                mStartMostChildView = findEndView(layoutManager, getHorizontalHelper(layoutManager));
            } else if (forwardDirection == true)
            {
                mStartMostChildView = findCenterView(layoutManager, getHorizontalHelper(layoutManager));
            } else
            {
                mStartMostChildView = findStartView(layoutManager, getHorizontalHelper(layoutManager));
            }
        }

        if (mStartMostChildView == null)
        {
            return RecyclerView.NO_POSITION;
        }

        final int centerPosition = layoutManager.getPosition(mStartMostChildView);
        if (centerPosition == RecyclerView.NO_POSITION)
        {
            return RecyclerView.NO_POSITION;
        }

        boolean reverseLayout = false;

        if ((layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider))
        {
            RecyclerView.SmoothScroller.ScrollVectorProvider vectorProvider = //
                (RecyclerView.SmoothScroller.ScrollVectorProvider) layoutManager;

            PointF vectorForEnd = vectorProvider.computeScrollVectorForPosition(itemCount - 1);
            if (vectorForEnd != null)
            {
                reverseLayout = vectorForEnd.x < 0 || vectorForEnd.y < 0;
            }
        }

        final int localPosition = reverseLayout //
            ? (forwardDirection ? centerPosition - 1 : centerPosition) //
            : (forwardDirection ? centerPosition + 1 : centerPosition);

        mForwardDirection = forwardDirection;
        mCenterPosition = localPosition;
        //        ExLog.d(mCenterPosition + " : " + forwardDirection + " : " + centerPosition);
        return localPosition;
    }

    @Override
    protected LinearSmoothScroller createSnapScroller(RecyclerView.LayoutManager layoutManager)
    {
        if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider))
        {
            return null;
        }

        return new LinearSmoothScroller(mRecyclerView.getContext())
        {
            @Override
            protected void onTargetFound(View targetView, RecyclerView.State state, Action action)
            {
                int[] snapDistances = calculateDistanceToFinalSnap(mRecyclerView.getLayoutManager(), targetView);
                final int dx = snapDistances[0];
                final int dy = snapDistances[1];
                final int time = calculateTimeForDeceleration(Math.max(Math.abs(dx), Math.abs(dy)));

                if (time > 0)
                {
                    action.update(dx, dy, time, mDecelerateInterpolator);
                }
            }

            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics)
            {
                return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
            }

            @Override
            protected int calculateTimeForScrolling(int dx)
            {
                return Math.min(MAX_SCROLL_ON_FLING_DURATION, super.calculateTimeForScrolling(dx));
            }
        };
    }

    private int distanceToCenter(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView, OrientationHelper helper)
    {
        final int childCenter = helper.getDecoratedStart(targetView) + (helper.getDecoratedMeasurement(targetView) / 2);
        final int containerCenter;

        if (layoutManager.getClipToPadding())
        {
            containerCenter = helper.getStartAfterPadding() + helper.getTotalSpace() / 2;
        } else
        {
            containerCenter = helper.getEnd() / 2;
        }

        return childCenter - containerCenter;
    }

    @Nullable
    private View findCenterView(RecyclerView.LayoutManager layoutManager, OrientationHelper helper)
    {
        int childCount = layoutManager.getChildCount();
        if (childCount == 0)
        {
            return null;
        }

        View closestChild = null;
        final int center;

        if (layoutManager.getClipToPadding())
        {
            center = helper.getStartAfterPadding() + helper.getTotalSpace() / 2;
        } else
        {
            center = helper.getEnd() / 2;
        }
        int absClosest = Integer.MAX_VALUE;

        for (int i = 0; i < childCount; i++)
        {
            final View child = layoutManager.getChildAt(i);
            int childCenter = helper.getDecoratedStart(child) + (helper.getDecoratedMeasurement(child) / 2);
            int absDistance = Math.abs(childCenter - center);

            /** if child center is closer than previous closest, set it as closest  **/
            if (absDistance < absClosest)
            {
                absClosest = absDistance;
                closestChild = child;
            }
        }

        return closestChild;
    }

    @Nullable
    private View findStartView(RecyclerView.LayoutManager layoutManager, OrientationHelper helper)
    {
        int childCount = layoutManager.getChildCount();
        if (childCount == 0)
        {
            return null;
        }

        View closestChild = null;
        int starTest = Integer.MAX_VALUE;

        for (int i = 0; i < childCount; i++)
        {
            final View child = layoutManager.getChildAt(i);
            int childStart = helper.getDecoratedStart(child);

            /** if child is more to start than previous closest, set it as closest  **/
            if (childStart < starTest)
            {
                starTest = childStart;
                closestChild = child;
            }
        }

        return closestChild;
    }

    @Nullable
    private View findEndView(RecyclerView.LayoutManager layoutManager, OrientationHelper helper)
    {
        int childCount = layoutManager.getChildCount();
        if (childCount == 0)
        {
            return null;
        }

        View closestChild = null;
        int endTest = Integer.MIN_VALUE;

        for (int i = 0; i < childCount; i++)
        {
            final View child = layoutManager.getChildAt(i);
            int childEnd = helper.getDecoratedStart(child) + (helper.getDecoratedMeasurement(child));

            /** if child is more to end than next closest, set it as closest  **/
            if (childEnd > endTest)
            {
                endTest = childEnd;
                closestChild = child;
            }
        }

        return closestChild;
    }

    @NonNull
    private OrientationHelper getVerticalHelper(@NonNull RecyclerView.LayoutManager layoutManager)
    {
        if (mVerticalHelper == null)
        {
            mVerticalHelper = OrientationHelper.createVerticalHelper(layoutManager);
        }
        return mVerticalHelper;
    }

    @NonNull
    private OrientationHelper getHorizontalHelper(@NonNull RecyclerView.LayoutManager layoutManager)
    {
        if (mHorizontalHelper == null)
        {
            mHorizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager);
        }
        return mHorizontalHelper;
    }
}
