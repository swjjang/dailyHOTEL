package com.daily.base.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class DailyTouchableFrameView extends FrameLayout
{
    OnTouchListener mOnTouchListener;

    public DailyTouchableFrameView(Context context)
    {
        super(context);
    }

    public DailyTouchableFrameView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public DailyTouchableFrameView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DailyTouchableFrameView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_UP:
                if (mOnTouchListener != null)
                {
                    mOnTouchListener.onTouch(this, event);
                }
                break;
        }

        return super.dispatchTouchEvent(event);
    }

    @Override
    public void setOnTouchListener(OnTouchListener listener)
    {
        mOnTouchListener = listener;
    }
}