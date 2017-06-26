package com.daily.base.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.daily.base.util.ExLog;

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
            try
            {
                return super.onTouchEvent(event);
            } catch (IllegalArgumentException e)
            {
                ExLog.e(e.toString());
            }
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event)
    {
        if (enabled == true)
        {
            try
            {
                return super.onTouchEvent(event);
            } catch (IllegalArgumentException e)
            {
                ExLog.e(e.toString());
            }
        }

        return false;
    }

    public void setPagingEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
}
