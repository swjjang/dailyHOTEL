package com.daily.base.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.daily.base.util.ExLog;

public class DailyLinearLayout extends LinearLayout
{
    public DailyLinearLayout(Context context)
    {
        super(context);
    }

    public DailyLinearLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public DailyLinearLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public DailyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        long startTime = System.currentTimeMillis();

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        ExLog.d("pinkred : " + (System.currentTimeMillis() - startTime));
    }
}
