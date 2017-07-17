package com.daily.base.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;

public class DailyRoundedConstraintLayout extends ConstraintLayout
{
    private Path mRoundPath;
    private float mRadius;

    public DailyRoundedConstraintLayout(Context context)
    {
        super(context);
    }

    public DailyRoundedConstraintLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public DailyRoundedConstraintLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public void setRound(float radius)
    {
        if (mRoundPath == null)
        {
            mRoundPath = new Path();
        }

        mRoundPath.reset();

        mRadius = radius;
    }

    public void setRound(float left, float top, float right, float bottom, float radius)
    {
        setRound(radius);

        mRoundPath.reset();
        mRoundPath.addRoundRect(new RectF(left, top, right, bottom), radius, radius, Path.Direction.CW);
    }

    @Override
    protected void dispatchDraw(Canvas canvas)
    {
        if (mRoundPath != null && mRoundPath.isEmpty() == true)
        {
            mRoundPath.addRoundRect(new RectF(canvas.getClipBounds()), mRadius, mRadius, Path.Direction.CW);
        }

        if (mRadius > 0.0f)
        {
            canvas.clipPath(mRoundPath);
        }

        super.dispatchDraw(canvas);
    }
}
