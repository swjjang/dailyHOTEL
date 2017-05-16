package com.daily.base.widget;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class DailyNestedScrollView extends android.support.v4.widget.NestedScrollView
{
    private boolean mIsScrollingEnabled = true;

    public DailyNestedScrollView(Context context)
    {
        super(context);
    }

    public DailyNestedScrollView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public DailyNestedScrollView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        switch (ev.getAction() & MotionEventCompat.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
                // if we can scroll pass the event to the superclass
                if (mIsScrollingEnabled == true)
                {
                    return super.onTouchEvent(ev);
                }
                // only continue to handle the touch event if scrolling enabled
                return mIsScrollingEnabled; // mIsScrollable is always false at this point
            default:
                return super.onTouchEvent(ev);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        if (mIsScrollingEnabled == false)
        {
            return false;
        } else
        {
            return super.onInterceptTouchEvent(ev);
        }
    }

    public void setScrollingEnabled(boolean enabled)
    {
        mIsScrollingEnabled = enabled;

        setNestedScrollingEnabled(enabled);
    }

    public boolean isScrollingEnabled()
    {
        return mIsScrollingEnabled;
    }
}