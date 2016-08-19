package com.twoheart.dailyhotel.widget;

/**
 * Created by android_sam on 2016. 8. 19..
 */

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ObjectAnimator;

public class DailyTensionViewPager extends ViewPager
{

    /**
     * 최대 X 이동 가능 값
     */
    private final static int DEFAULT_OVER_SCROLL_TRANSLATION = 200;
    private final static int DEFAULT_OVER_SCROLL_ANIMATION_DURATION = 300;

    private final static int INVALID_POINTER_ID = -1;

    private final OverScrollEffect mOverScrollEffect = new OverScrollEffect();

    private OnPageChangeListener mScrollListener;
    private float mLastMotionX;
    private int mActivePointerId;
    private int mScrollPosition;
    private float mScrollPositionOffset;
    private final int mTouchSlop; // Touch Event 가 Scroll Event로 인정받기위한 최소간격!

    private int mOverScrollTranslation;
    private int mOverScrollAnimationDuration;
    private Rect mTempRect = new Rect();

    private class OverScrollEffect
    {
        private float mOverScroll;
        private Animator mAnimator;

        /**
         * @param distance [0..1] 1 보다 크면 OverScroll
         */
        public void setPull(final float distance)
        {
            mOverScroll = distance;
            invalidateVisibleChildren();
        }

        private void onRelease()
        {
            if (mAnimator != null && mAnimator.isRunning())
            {
                mAnimator.addListener(new AnimatorListener()
                {

                    @Override
                    public void onAnimationStart(Animator animation)
                    {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation)
                    {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        startAnimation(0);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation)
                    {
                    }
                });
                mAnimator.cancel();
            } else
            {
                startAnimation(0);
            }
        }

        private void startAnimation(final float target)
        {
            mAnimator = ObjectAnimator.ofFloat(this, "Pull", mOverScroll, target);
            mAnimator.setInterpolator(new DecelerateInterpolator());

            final float scale = Math.abs(target - mOverScroll);
            mAnimator.setDuration((long) (mOverScrollAnimationDuration * scale));
            mAnimator.start();
        }

        private boolean isOverScrolling()
        {
            if (isFirst() && mOverScroll < 0)
            {
                return true;
            }
            if (getAdapter() != null)
            {
                if (isLast() && mOverScroll > 0)
                {
                    return true;
                }
            }
            return false;
        }
    }

    public DailyTensionViewPager(Context context)
    {
        super(context);

        setStaticTransformationsEnabled(true);
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);

        super.setOnPageChangeListener(new ViewPageChangeListener());

        mOverScrollTranslation = DEFAULT_OVER_SCROLL_TRANSLATION;
        mOverScrollAnimationDuration = DEFAULT_OVER_SCROLL_ANIMATION_DURATION;
    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener)
    {
        mScrollListener = listener;
    }

    private void invalidateVisibleChildren()
    {
        for (int i = 0; i < getChildCount(); i++)
        {
            final View childAt = getChildAt(i);
            childAt.getLocalVisibleRect(mTempRect);

            final int area = mTempRect.width() * mTempRect.height();
            if (area > 0)
            {
                childAt.invalidate();
            }
        }

        invalidate();
    }

    private class ViewPageChangeListener implements OnPageChangeListener
    {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {
            if (mScrollListener != null)
            {
                mScrollListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            mScrollPositionOffset = positionOffset;
        }

        @Override
        public void onPageSelected(int position)
        {
            if (mScrollListener != null)
            {
                mScrollListener.onPageSelected(position);
            }

            mScrollPosition = position;
        }

        @Override
        public void onPageScrollStateChanged(final int state)
        {
            if (mScrollListener != null)
            {
                mScrollListener.onPageScrollStateChanged(state);
            }

            if (state == SCROLL_STATE_IDLE)
            {
                mScrollPositionOffset = 0;
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;
        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
            {
                mLastMotionX = ev.getX();
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                break;
            }
            case MotionEventCompat.ACTION_POINTER_DOWN:
            {
                final int index = MotionEventCompat.getActionIndex(ev);
                mLastMotionX = MotionEventCompat.getX(ev, index);
                mActivePointerId = MotionEventCompat.getPointerId(ev, index);
                break;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        boolean callSuper = false;

        final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;
        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
            {
                callSuper = true;
                mLastMotionX = ev.getX();
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                break;
            }
            case MotionEventCompat.ACTION_POINTER_DOWN:
            {
                callSuper = true;
                final int index = MotionEventCompat.getActionIndex(ev);
                mLastMotionX = MotionEventCompat.getX(ev, index);
                mActivePointerId = MotionEventCompat.getPointerId(ev, index);
                break;
            }
            case MotionEvent.ACTION_MOVE:
            {
                if (mActivePointerId != INVALID_POINTER_ID)
                {
                    // Scroll to follow the motion event
                    final int activePointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                    final float x = MotionEventCompat.getX(ev, activePointerIndex);
                    final float deltaX = mLastMotionX - x;
                    final int width = getWidth();
                    final int widthWithMargin = width + getPageMargin();
                    final int lastItemIndex = getAdapter().getCount() - 1;
                    final int currentItemIndex = getCurrentItem();
                    final float leftBound = Math.max(0, (currentItemIndex - 1) * widthWithMargin);
                    final float rightBound = Math.min(currentItemIndex + 1, lastItemIndex) * widthWithMargin;

                    if (currentItemIndex == 0)
                    {
                        if (leftBound == 0)
                        {
                            final float over = deltaX + mTouchSlop;
                            mOverScrollEffect.setPull(over / width);
                        }
                    } else if (lastItemIndex == currentItemIndex)
                    {
                        if (rightBound == lastItemIndex * widthWithMargin)
                        {
                            final float over = deltaX - mTouchSlop;
                            mOverScrollEffect.setPull(over / width);
                        }
                    }
                } else
                {
                    mOverScrollEffect.onRelease();
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            {
                callSuper = true;
                mActivePointerId = INVALID_POINTER_ID;
                mOverScrollEffect.onRelease();
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:
            {
                final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
                if (pointerId == mActivePointerId)
                {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastMotionX = ev.getX(newPointerIndex);
                    mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
                    callSuper = true;
                }
                break;
            }
        }

        if (mOverScrollEffect.isOverScrolling() && !callSuper)
        {
            return true;
        } else
        {
            try
            {
                return super.onTouchEvent(ev);
            } catch (Exception e)
            {
            }
            return false;
        }
    }

    @Override
    protected boolean getChildStaticTransformation(View child, Transformation t)
    {
        if (child.getWidth() == 0)
        {
            return false;
        }

        final boolean isFirst = isFirst();
        final boolean isLast = isLast();

        if (mOverScrollEffect.isOverScrolling() && (isFirst || isLast))
        {
            final float translateX = (float) (mOverScrollTranslation * Math.sin(Math.PI * Math.abs(mOverScrollEffect.mOverScroll)));

            if (isFirst)
            {
                this.setTranslationX(translateX);

            } else if (isLast)
            {
                this.setTranslationX(-translateX);
            }

            return true;
        } else if (mScrollPositionOffset > 0)
        {
            return true;
        }
        return false;
    }

    private boolean isFirst()
    {
        return mScrollPosition == 0;
    }

    private boolean isLast()
    {
        int count = getAdapter().getCount();
        return mScrollPosition == count - 1;
    }
}
