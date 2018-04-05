package com.daily.base.widget;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;

public class DailyConstraintLayout extends ConstraintLayout
{
    public DailyConstraintLayout(Context context)
    {
        super(context);
    }

    public DailyConstraintLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public DailyConstraintLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
