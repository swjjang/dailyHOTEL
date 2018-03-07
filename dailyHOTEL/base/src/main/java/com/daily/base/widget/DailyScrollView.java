package com.daily.base.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class DailyScrollView extends ScrollView
{
    private OnScrollChangedListener mOnScrollChangedListener;
    private boolean mIsScrollable = true;
    private boolean mIsChangeLayout = true;

    public interface OnScrollChangedListener
    {
        void onScrollChanged(ScrollView scrollView, int l, int t, int oldl, int oldt);
    }

    public DailyScrollView(Context context)
    {
        super(context);
    }

    public DailyScrollView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public DailyScrollView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DailyScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
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
            return super.onInterceptTouchEvent(ev);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        if (mIsChangeLayout == false)
        {
            return;
        }

        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        if (mIsChangeLayout == false)
        {
            return;
        }

        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void setScrollingEnabled(boolean enabled)
    {
        mIsScrollable = enabled;
    }

    public boolean isScrollable()
    {
        return mIsScrollable;
    }

    public void setChangeLayoutEnabled(boolean enabled)
    {
        mIsChangeLayout = enabled;
    }
}