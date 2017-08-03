package com.twoheart.dailyhotel.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;

import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyTextView;

public class DailyDayStrikeTextView extends DailyTextView
{
    private Paint mStrikePaint;
    private int DP_1;
    private int DP_20;
    private int DP_12;
    private boolean mStrikeFlag;

    public DailyDayStrikeTextView(Context context)
    {
        super(context);

        initLayout();
    }

    public DailyDayStrikeTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout();
    }

    public DailyDayStrikeTextView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        initLayout();
    }

    private void initLayout()
    {
        DP_1 = ScreenUtils.dpToPx(getContext(), 1);
        DP_12 = ScreenUtils.dpToPx(getContext(), 12);
        DP_20 = ScreenUtils.dpToPx(getContext(), 20);

        mStrikePaint = getPaint();
        mStrikePaint.setStrokeWidth(DP_1);
        mStrikePaint.setStyle(Paint.Style.FILL);
        mStrikePaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void setStrikeFlag(boolean flag)
    {
        mStrikeFlag = flag;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if (mStrikeFlag == true)
        {
            drawStrike(canvas);
        }
    }

    private void drawStrike(Canvas canvas)
    {
        int width;

        switch (getText().length())
        {
            case 1:
                width = DP_12;
                break;

            case 2:
            default:
                width = DP_20;
                break;
        }

        Paint.FontMetrics fontMetrics = getPaint().getFontMetrics();
        int fontHeight = (int) (fontMetrics.bottom - fontMetrics.top + fontMetrics.leading);
        int height = getHeight() - getPaddingTop() - getPaddingBottom();
        final int gravity = getGravity() & Gravity.VERTICAL_GRAVITY_MASK;
        int y;

        switch (gravity)
        {
            case Gravity.TOP:
                y = getPaddingTop() + fontHeight / 2;
                break;

            case Gravity.BOTTOM:
                y = getHeight() - getPaddingBottom() - fontHeight / 2;
                break;

            default: // Gravity.CENTER_VERTICAL
                y = getPaddingTop() + (height - fontHeight) / 2;
                break;
        }

        final int x = (canvas.getWidth() - width) / 2;
        canvas.drawLine(x, y, x + width, y, mStrikePaint);
    }
}
