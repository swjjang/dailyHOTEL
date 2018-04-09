package com.twoheart.dailyhotel.widget;

/**
 * Created by android_sam on 2016. 8. 19..
 */

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;

import com.daily.base.util.ExLog;

public class DailyOverScrollViewPager extends ViewPager
{

    /**
     * 최대 X 이동 가능 값
     */
    private final static int DEFAULT_OVER_SCROLL_TRANSLATION = 200;
    private final static int DEFAULT_OVER_SCROLL_ANIMATION_DURATION = 300;

    private final static int INVALID_POINTER_ID = -1;

    private final OverScrollEffect mOverScrollEffect = new OverScrollEffect();

    OnPageChangeListener mScrollListener;
    private float mLastMotionX;
    private int mActivePointerId;
    int mScrollPosition;
    float mScrollPositionOffset;
    private int mTouchSlop; // Touch Event 가 Scroll Event로 인정받기위한 최소간격!

    private int mOverScrollTranslation;
    int mOverScrollAnimationDuration;
    private Rect mTempRect = new Rect();


    private class OverScrollEffect
    {
        float mOverScroll;
        Animator mAnimator;

        OverScrollEffect()
        {
        }

        /**
         * @param distance [0..1] 1 보다 크면 OverScroll
         */
        @Keep
        public void setPull(final float distance)
        {
            mOverScroll = distance;
            invalidateVisibleChildren();
        }

        void onRelease()
        {
            if (mAnimator != null && mAnimator.isRunning() == true)
            {
                mAnimator.addListener(new Animator.AnimatorListener()
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
                        if (mAnimator != null)
                        {
                            mAnimator.removeAllListeners();
                            mAnimator = null;
                        }

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

        void startAnimation(final float target)
        {
            mAnimator = ObjectAnimator.ofFloat(this, "Pull", mOverScroll, target);
            mAnimator.setInterpolator(new DecelerateInterpolator());

            final float scale = Math.abs(target - mOverScroll);
            mAnimator.setDuration((long) (mOverScrollAnimationDuration * scale));
            mAnimator.start();
        }

        boolean isOverScrolling()
        {
            if (isFirst() && mOverScroll < 0)
            {
                return true;
            }

            return getAdapter() != null && isLast() && mOverScroll > 0;
        }
    }

    public DailyOverScrollViewPager(Context context)
    {
        super(context);

        initLayout(context);
    }

    public DailyOverScrollViewPager(@NonNull Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
        setStaticTransformationsEnabled(true);
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);

        super.setOnPageChangeListener(new ViewPageChangeListener());

        mOverScrollTranslation = DEFAULT_OVER_SCROLL_TRANSLATION;
        mOverScrollAnimationDuration = DEFAULT_OVER_SCROLL_ANIMATION_DURATION;

        mScrollPosition = 0;
        mScrollPositionOffset = 0;
    }

    @Override
    public void setAdapter(PagerAdapter adapter)
    {
        super.setAdapter(adapter);

        mScrollPosition = 0;
        mScrollPositionOffset = 0;
    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener)
    {
        mScrollListener = listener;
    }

    void invalidateVisibleChildren()
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
        ViewPageChangeListener()
        {
        }

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
        try
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
        } catch (IllegalArgumentException e)
        {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        try
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

                        if (getAdapter().getCount() == 1)
                        {
                            if (deltaX < 0 - mTouchSlop)
                            {
                                final float over = deltaX + mTouchSlop;
                                mOverScrollEffect.setPull(over / width);
                            } else if (deltaX > mTouchSlop)
                            {
                                final float over = deltaX - mTouchSlop;
                                mOverScrollEffect.setPull(over / width);
                            }
                        } else if (currentItemIndex == 0)
                        {
                            if (leftBound == 0)
                            {
                                final float over = deltaX + mTouchSlop;
                                mOverScrollEffect.setPull(over / width);
                            } else
                            {
                                ExLog.d("index : " + currentItemIndex + ", leftBound : " + leftBound);
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
        } catch (IllegalArgumentException e)
        {
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

        final float translateX = (float) (mOverScrollTranslation * Math.sin(Math.PI * mOverScrollEffect.mOverScroll) * (-1));
        if (mOverScrollEffect.isOverScrolling() && (isFirst() || isLast()))
        {
            this.setTranslationX(translateX);

            return true;
        } else if (mScrollPositionOffset > 0)
        {
            if (translateX != 0)
            {
                this.setTranslationX(0);
            }
            return true;
        }
        return false;
    }

    boolean isFirst()
    {
        return mScrollPosition == 0;
    }

    boolean isLast()
    {
        int count = getAdapter().getCount();
        return mScrollPosition == count - 1;
    }
}
