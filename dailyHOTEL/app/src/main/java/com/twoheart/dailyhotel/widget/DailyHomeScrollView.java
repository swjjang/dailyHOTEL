package com.twoheart.dailyhotel.widget;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * 홈 화면용 ScrollView - DailyScrollView 와 다른 점은.... onInterceptTouchEvent 의 처리차이
 * onInterceptTouchEvent 에서 최근 본 업장등의 가로 스크롤 대응 추가 됨
 */
public class DailyHomeScrollView extends ScrollView
{
    private OnScrollChangedListener mOnScrollChangedListener;
    private boolean mIsScrollable = true;

    private float mDistanceX;
    private float mDistanceY;
    private float mLastX;
    private float mLastY;

    public interface OnScrollChangedListener
    {
        void onScrollChanged(ScrollView scrollView, int l, int t, int oldl, int oldt);
    }

    public DailyHomeScrollView(Context context)
    {
        super(context);
    }

    public DailyHomeScrollView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public DailyHomeScrollView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public DailyHomeScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setOnScrollChangedListener(OnScrollChangedListener listener)
    {
        mOnScrollChangedListener = listener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt)
    {
        if (mOnScrollChangedListener != null)
        {
            mOnScrollChangedListener.onScrollChanged(this, l, t, oldl, oldt);
        }

        super.onScrollChanged(l, t, oldl, oldt);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        switch (ev.getAction() & MotionEventCompat.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
                // if we can scroll pass the event to the superclass
                if (mIsScrollable)
                {
                    return super.onTouchEvent(ev);
                }
                // only continue to handle the touch event if scrolling enabled
                return mIsScrollable; // mIsScrollable is always false at this point
            default:
                return super.onTouchEvent(ev);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        if (mIsScrollable == false)
        {
            return false;
        } else
        {
            switch (ev.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                {
                    mDistanceX = mDistanceY = 0f;
                    mLastX = ev.getX();
                    mLastY = ev.getY();

                    // This is very important line that fixes
                    computeScroll();
                    break;
                }
                case MotionEvent.ACTION_MOVE:
                {
                    final float curX = ev.getX();
                    final float curY = ev.getY();
                    mDistanceX += Math.abs(curX - mLastX);
                    mDistanceY += Math.abs(curY - mLastY);
                    mLastX = curX;
                    mLastY = curY;

                    if (mDistanceX > mDistanceY)
                    {
                        return false;
                    }
                }
            }

            return super.onInterceptTouchEvent(ev);
        }
    }

    public void setScrollingEnabled(boolean enabled)
    {
        mIsScrollable = enabled;
    }

    public boolean isScrollable()
    {
        return mIsScrollable;
    }
}