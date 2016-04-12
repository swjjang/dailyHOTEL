package com.twoheart.dailyhotel.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class DailyScrollView extends ScrollView
{
    private OnScrollChangedListener mOnScrollChangedListener;

    public interface OnScrollChangedListener
    {
        void onScrollChanged(ScrollView scrollView, int l, int t, int oldl, int oldt);
    }

    public DailyScrollView(Context context)
    {
        super(context);
    }

    public DailyScrollView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public DailyScrollView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public DailyScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setOnScrollChangedListener(OnScrollChangedListener listener)
    {
        mOnScrollChangedListener = listener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt)
    {
        if (mOnScrollChangedListener != null)
        {
            mOnScrollChangedListener.onScrollChanged(this, l, t, oldl, oldt);
        }

        super.onScrollChanged(l, t, oldl, oldt);
    }
}