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
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Util;

public class DailyLineIndicator extends RelativeLayout
{
    private final PageListener mPageListener = new PageListener();
    public ViewPager.OnPageChangeListener mOnPageChangeListener;

    private LinearLayout mTabsContainer;
    private ViewPager mViewpager;

    private DailyTextView mDescriptionTextView;

    private int mTabCount;

    private int mCurrentPosition = 0;
    private float mCurrentPositionOffset = 0f;

    private Paint mRectPaint;

    private int mIndicatorColor = 0xFFFFFFFF;
    private int mIndicatorBackgroundColor = 0x66FFFFFF;

    private int mIndicatorHeight = 2;

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

        View indicatorLayout = LayoutInflater.from(context).inflate(R.layout.layout_daily_line_indicator, null);

        RelativeLayout.LayoutParams textLayoutParams = new RelativeLayout.LayoutParams(//
            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        textLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        textLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        indicatorLayout.setLayoutParams(textLayoutParams);
        addView(indicatorLayout);

        mTabsContainer = (LinearLayout) indicatorLayout.findViewById(R.id.tabLayout);
        mDescriptionTextView = (DailyTextView) indicatorLayout.findViewById(R.id.descriptionTextView);

        mDescriptionTextView.setVisibility(View.INVISIBLE);

        DisplayMetrics dm = getResources().getDisplayMetrics();

        mIndicatorHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mIndicatorHeight, dm);

        // get custom attrs
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DailyLineIndicator);

        mIndicatorColor = a.getColor(R.styleable.DailyLineIndicator_indicatorColor, mIndicatorColor);
        mIndicatorBackgroundColor = a.getColor(R.styleable.DailyLineIndicator_indicatorBackgroundColor, mIndicatorBackgroundColor);
        mIndicatorHeight = a.getDimensionPixelSize(R.styleable.DailyLineIndicator_indicatorHeight, mIndicatorHeight);

        a.recycle();

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

        mTabsContainer.removeAllViews();

        mTabCount = mViewpager.getAdapter().getCount();

        if (mTabCount > 1)
        {
            for (int i = 0; i < mTabCount; i++)
            {
                addTab(i, new View(getContext()));
            }
        }

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

    private void addTab(final int position, View tab)
    {
//        tab.setFocusable(true);
//        tab.setOnClickListener(new OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                mViewpager.setCurrentItem(position);
//            }
//        });

        mTabsContainer.addView(tab, position, new LinearLayout.LayoutParams(0, 1, 1.0f));
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
        canvas.drawRect(mTabsContainer.getLeft(), height - mIndicatorHeight, mTabsContainer.getRight(), height, mRectPaint);

        // draw indicator line
        mRectPaint.setColor(mIndicatorColor);

        // default: line below current tab
        View currentTab = mTabsContainer.getChildAt(mCurrentPosition);
        if (currentTab == null)
        {
            return;
        }

        float lineLeft = currentTab.getLeft();
        float lineRight = currentTab.getRight();

        // if there is an offset, start interpolating left and right coordinates between current and next tab
        if (mCurrentPositionOffset > 0f && mCurrentPosition < mTabCount - 1)
        {
            View nextTab = mTabsContainer.getChildAt(mCurrentPosition + 1);
            final float nextTabLeft = nextTab.getLeft();
            final float nextTabRight = nextTab.getRight();

            lineLeft = (mCurrentPositionOffset * nextTabLeft + (1f - mCurrentPositionOffset) * lineLeft);
            lineRight = (mCurrentPositionOffset * nextTabRight + (1f - mCurrentPositionOffset) * lineRight);
        }

        canvas.drawRect(lineLeft, height - mIndicatorHeight, lineRight, height, mRectPaint);
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

    public void setIndicatorHeight(int indicatorLineHeightPx)
    {
        this.mIndicatorHeight = indicatorLineHeightPx;
        invalidate();
    }

    public int getIndicatorHeight()
    {
        return mIndicatorHeight;
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

    public void setImageInformation(String description)
    {
        if (Util.isTextEmpty(description) == false)
        {
            mDescriptionTextView.setVisibility(View.VISIBLE);
            mDescriptionTextView.setText(description);
        } else
        {
            mDescriptionTextView.setVisibility(View.INVISIBLE);
        }
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
