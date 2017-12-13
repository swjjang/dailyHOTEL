package com.daily.base.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;

import com.daily.base.util.VersionUtils;

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
