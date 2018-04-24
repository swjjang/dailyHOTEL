package com.daily.base.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;

import com.daily.base.util.VersionUtils;

public class DailyRoundedConstraintLayout extends ConstraintLayout
{
    private Path mRoundPath;
    private float mRadius;
    private RectF mRectF;

    public DailyRoundedConstraintLayout(Context context)
    {
        super(context);

        initLayout();
    }

    public DailyRoundedConstraintLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout();
    }

    public DailyRoundedConstraintLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        initLayout();
    }

    private void initLayout()
    {
        if (VersionUtils.isOverAPI18() == false)
        {
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        }

        mRectF = new RectF();
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
    public void dispatchDraw(Canvas canvas)
    {
        if (mRoundPath != null && mRoundPath.isEmpty() == true)
        {
            // 추후에 패딩 안쪽으로 라운드 만들려면 추가
            //            Rect rect = canvas.getClipBounds();
            //            rect.set(rect.left + getPaddingLeft(), rect.top + getPaddingTop(), rect.right - getPaddingRight(), rect.bottom - getPaddingBottom());
            mRectF.set(canvas.getClipBounds());

            mRoundPath.addRoundRect(mRectF, mRadius, mRadius, Path.Direction.CW);
        }

        if (mRadius > 0.0f)
        {
            canvas.clipPath(mRoundPath);
        }

        super.dispatchDraw(canvas);
    }
}
