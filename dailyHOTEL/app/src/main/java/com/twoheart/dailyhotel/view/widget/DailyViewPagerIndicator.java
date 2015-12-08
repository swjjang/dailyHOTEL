package com.twoheart.dailyhotel.view.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

public class DailyViewPagerIndicator extends View
{
    private static final int SELECTED_FACTOR = 2;
    private static final int SPACING_FACTOR = 4;

    private int mTotalCount;
    private Paint mPaint;
    private int mPosition;
    private float mRadius;
    private ViewPager mViewPager;

    public DailyViewPagerIndicator(Context context)
    {
        super(context);

        initLayout();
    }

    public DailyViewPagerIndicator(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout();
    }

    public DailyViewPagerIndicator(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout();
    }

    public DailyViewPagerIndicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);

        initLayout();
    }

    private void initLayout()
    {
        mRadius = 4f;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        float x, y;

        for (int i = 0; i < mTotalCount; i++)
        {
            x = (i * SELECTED_FACTOR * mRadius) + (i * (mRadius * SPACING_FACTOR)) + mRadius * SELECTED_FACTOR;
            y = mRadius * SELECTED_FACTOR;

            if (i == mPosition)
            {
                canvas.drawCircle(x, y, mRadius * SELECTED_FACTOR, mPaint);
            } else
            {
                canvas.drawCircle(x, y, mRadius, mPaint);
            }
        }
    }

    public void setPosition(int position)
    {
        mPosition = position;
        invalidate();
        requestLayout();
    }

    public void setTotalCount(int count)
    {
        mTotalCount = count;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int measuredWidth = (int) (mRadius * SELECTED_FACTOR + (mRadius * (SELECTED_FACTOR + SPACING_FACTOR)) * (mTotalCount - 1));
        measuredWidth += mRadius * SELECTED_FACTOR;

        int measuredHeight = (int) (mRadius * SELECTED_FACTOR) * 2;

        setMeasuredDimension(measuredWidth, measuredHeight);
    }
}
