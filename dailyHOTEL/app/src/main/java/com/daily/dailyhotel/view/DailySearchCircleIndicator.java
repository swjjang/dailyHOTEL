package com.daily.dailyhotel.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.daily.base.util.ScreenUtils;
import com.twoheart.dailyhotel.R;

/**
 * Created by iseung-won on 2017. 8. 9..
 */

public class DailySearchCircleIndicator extends View
{
    private static final int SELECTED_FACTOR = 2;
    private static final int SPACING_FACTOR = 4;

    private int mTotalCount;
    private Paint mPaint;
    private int mPosition;
    private float mRadius;

    public DailySearchCircleIndicator(Context context)
    {
        super(context);

        initLayout();
    }

    public DailySearchCircleIndicator(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout();
    }

    public DailySearchCircleIndicator(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout();
    }

    private void initLayout()
    {
        mRadius = ScreenUtils.dpToPx(getContext(), 3);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        if (mTotalCount == 1)
        {
            return;
        }

        float x, y;

        for (int i = 0; i < mTotalCount; i++)
        {
            x = (i * SELECTED_FACTOR * mRadius) + (i * (mRadius * SPACING_FACTOR)) + mRadius * SELECTED_FACTOR;
            y = mRadius * SELECTED_FACTOR;

            if (i == mPosition)
            {
                mPaint.setColor(getResources().getColor(R.color.default_background_cababab));
                canvas.drawCircle(x, y, mRadius * SELECTED_FACTOR, mPaint);
            } else
            {
                mPaint.setColor(getResources().getColor(R.color.default_line_cdcdcdd));
                canvas.drawCircle(x, y, mRadius * SELECTED_FACTOR, mPaint);
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
