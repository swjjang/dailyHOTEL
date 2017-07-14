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

        mRadius = radius;
    }

    @Override
    protected void dispatchDraw(Canvas canvas)
    {
        if (mRoundPath != null && mRadius > 0.0f)
        {
            mRoundPath.reset();
            mRoundPath.addRoundRect(new RectF(0, 0, canvas.getWidth(), canvas.getHeight()), mRadius, mRadius, Path.Direction.CW);

            canvas.clipPath(mRoundPath);
        }

        super.dispatchDraw(canvas);
    }
}
