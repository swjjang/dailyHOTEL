package com.twoheart.dailyhotel.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;

import com.twoheart.dailyhotel.R;

public class DailyLineIndicator extends View
{
    private final PageListener mPageListener = new PageListener();
    public ViewPager.OnPageChangeListener mOnPageChangeListener;

    private ViewPager mViewpager;

    private int mTabCount;

    private int mCurrentPosition = 0;
    private float mCurrentPositionOffset = 0f;

    private Paint mRectPaint;

    private int mIndicatorColor = 0xFFFFFFFF;
    private int mIndicatorBackgroundColor = 0x66FFFFFF;

    private int[][] mMeasureList;

    public DailyLineIndicator(Context context)
    {
        this(context, null);
    }

    public DailyLineIndicator(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public DailyLineIndicator(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        setWillNotDraw(false);

        DisplayMetrics dm = getResources().getDisplayMetrics();

        // get custom attrs
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.dailyLineIndicator);

        mIndicatorColor = typedArray.getColor(R.styleable.dailyLineIndicator_indicatorColor, getResources().getColor(R.color.white));
        mIndicatorBackgroundColor = typedArray.getColor(R.styleable.dailyLineIndicator_indicatorBackgroundColor, getResources().getColor(R.color.white_a40));

        typedArray.recycle();

        mRectPaint = new Paint();
        mRectPaint.setAntiAlias(true);
        mRectPaint.setStyle(Paint.Style.FILL);
    }

    public void setViewPager(ViewPager pager)
    {
        this.mViewpager = pager;

        if (pager.getAdapter() == null)
        {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }

        pager.setOnPageChangeListener(mPageListener);

        notifyDataSetChanged();
    }

    public void setmOnPageChangeListener(ViewPager.OnPageChangeListener listener)
    {
        this.mOnPageChangeListener = listener;
    }

    public void notifyDataSetChanged()
    {
        mTabCount = mViewpager.getAdapter().getCount();

        mMeasureList = getMeasureWidthList(mTabCount);

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @SuppressWarnings("deprecation")
            @SuppressLint("NewApi")
            @Override
            public void onGlobalLayout()
            {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
                {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else
                {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                mCurrentPosition = mViewpager.getCurrentItem();
            }
        });

    }

    private int[][] getMeasureWidthList(int tabCount)
    {
        if (tabCount == 0)
        {
            return null;
        }

        if (tabCount == 1)
        {
            return new int[][]{{getWidth(), 0}};
        }

        int[][] measureList = new int [tabCount][2];

        int delta = getWidth();

        for (int i = tabCount; i > 0; i--)
        {
            int oneWidth = delta / i;

            measureList[i - 1][0] = oneWidth;
            delta = delta - oneWidth;

            measureList[i - 1][1] = delta;
        }

        return measureList;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if (isInEditMode() || mTabCount == 0)
        {
            return;
        }

        final int height = getHeight();

        // draw indicator background line
        mRectPaint.setColor(mIndicatorBackgroundColor);
        canvas.drawRect(0, 0, getRight(), height, mRectPaint);

        // draw indicator line
        mRectPaint.setColor(mIndicatorColor);

        if (mCurrentPosition < 0 || mCurrentPosition >= mTabCount) {
            return;
        }

        int width = mMeasureList[mCurrentPosition][0];
        float lineLeft = mMeasureList[mCurrentPosition][1];
        float lineRight = lineLeft + width;

        // if there is an offset, start interpolating left and right coordinates between current and next tab
        if (mCurrentPositionOffset > 0f && mCurrentPosition < mTabCount - 1)
        {
            final float nextTabLeft = mMeasureList[mCurrentPosition + 1][1];
            final float nextTabRight = nextTabLeft + mMeasureList[mCurrentPosition + 1][0];

            lineLeft = (mCurrentPositionOffset * nextTabLeft + (1f - mCurrentPositionOffset) * lineLeft);
            lineRight = (mCurrentPositionOffset * nextTabRight + (1f - mCurrentPositionOffset) * lineRight);
        }

        canvas.drawRect(lineLeft, 0, lineRight, height, mRectPaint);
    }

    private class PageListener implements ViewPager.OnPageChangeListener
    {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {

            mCurrentPosition = position;
            mCurrentPositionOffset = positionOffset;

            invalidate();

            if (mOnPageChangeListener != null)
            {
                mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state)
        {
            if (mOnPageChangeListener != null)
            {
                mOnPageChangeListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position)
        {
            if (mOnPageChangeListener != null)
            {
                mOnPageChangeListener.onPageSelected(position);
            }
        }
    }

    public void setIndicatorColor(int indicatorColor)
    {
        this.mIndicatorColor = indicatorColor;
        invalidate();
    }

    public void setIndicatorColorResource(int resId)
    {
        this.mIndicatorColor = getResources().getColor(resId);
        invalidate();
    }

    public int getIndicatorColor()
    {
        return this.mIndicatorColor;
    }

    public void setIndicatorBackgroundColorResource(int resId)
    {
        this.mIndicatorBackgroundColor = getResources().getColor(resId);
        invalidate();
    }

    public void setIndicatorBackgroundColor(int indicatorBackgroundColor)
    {
        this.mIndicatorBackgroundColor = indicatorBackgroundColor;
        invalidate();
    }

    public int getIndicatorBackgroundColor()
    {
        return this.mIndicatorBackgroundColor;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state)
    {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        mCurrentPosition = savedState.currentPosition;
        requestLayout();
    }

    @Override
    public Parcelable onSaveInstanceState()
    {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.currentPosition = mCurrentPosition;
        return savedState;
    }

    static class SavedState extends BaseSavedState
    {
        int currentPosition;

        public SavedState(Parcelable superState)
        {
            super(superState);
        }

        private SavedState(Parcel in)
        {
            super(in);
            currentPosition = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags)
        {
            super.writeToParcel(dest, flags);
            dest.writeInt(currentPosition);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>()
        {
            @Override
            public SavedState createFromParcel(Parcel in)
            {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size)
            {
                return new SavedState[size];
            }
        };
    }

}
