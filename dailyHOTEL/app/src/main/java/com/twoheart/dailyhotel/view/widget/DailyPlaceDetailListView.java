package com.twoheart.dailyhotel.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class DailyPlaceDetailListView extends ListView
{
    private boolean mScrollable = true;

    public DailyPlaceDetailListView(Context context)
    {
        super(context);

        initLayout(context);
    }

    public DailyPlaceDetailListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public DailyPlaceDetailListView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout(context);
    }

    public DailyPlaceDetailListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
    }

    public void setScrollEnabled(boolean enable)
    {
        mScrollable = enable;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        return mScrollable != false && super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return mScrollable != false && super.onTouchEvent(event);
    }
}
