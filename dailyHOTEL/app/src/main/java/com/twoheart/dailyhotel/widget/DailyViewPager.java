package com.twoheart.dailyhotel.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class DailyViewPager extends ViewPager
{
    private boolean enabled;

    public DailyViewPager(Context context)
    {
        super(context);
        enabled = true;
    }

    public DailyViewPager(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        enabled = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (enabled == true)
        {
            return super.onTouchEvent(event);
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event)
    {
        if (enabled == true)
        {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }

    public void setPagingEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
}
