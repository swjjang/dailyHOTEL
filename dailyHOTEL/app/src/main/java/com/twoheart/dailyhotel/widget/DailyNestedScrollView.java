package com.twoheart.dailyhotel.widget;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by android_sam on 2017. 3. 8..
 */

public class DailyNestedScrollView extends NestedScrollView
{
    private float mDistanceX;
    private float mDistanceY;
    private float mLastX;
    private float mLastY;

    public DailyNestedScrollView(Context context)
    {
        super(context);
    }

    public DailyNestedScrollView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public DailyNestedScrollView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
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
