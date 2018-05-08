package com.daily.base.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.OverScroller;

import com.daily.base.R;
import com.daily.base.util.ExLog;

import java.lang.reflect.Field;

public class DailyNestedScrollView extends android.support.v4.widget.NestedScrollView
{
    private boolean mIsScrollingEnabled = true;

    private float mDistanceX;
    private float mDistanceY;
    private float mLastX;
    private float mLastY;
    private int maxHeight;

    public DailyNestedScrollView(Context context)
    {
        super(context);
    }

    public DailyNestedScrollView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        setMaxHeight(context, attrs);
    }

    public DailyNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        setMaxHeight(context, attrs);
    }

    private void setMaxHeight(Context context, AttributeSet attrs)
    {
        if (context == null || attrs == null)
        {
            return;
        }

        maxHeight = context.obtainStyledAttributes(attrs, R.styleable.app).getDimensionPixelSize(R.styleable.app_maxHeight, -1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        if (maxHeight > 0)
        {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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
        mIsScrollingEnabled = enabled;

        setNestedScrollingEnabled(enabled);
    }

    public boolean isScrollingEnabled()
    {
        return mIsScrollingEnabled;
    }

    public void abortScrolling()
    {
        try
        {
            Field field = android.support.v4.widget.NestedScrollView.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            OverScroller scroller = (OverScroller) field.get(this);
            scroller.abortAnimation();
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }
}