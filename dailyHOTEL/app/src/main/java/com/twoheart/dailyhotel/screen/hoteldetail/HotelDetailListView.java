package com.twoheart.dailyhotel.screen.hoteldetail;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class HotelDetailListView extends ListView
{
    private boolean mScrollable = true;

    public HotelDetailListView(Context context)
    {
        super(context);

        initLayout(context);
    }

    public HotelDetailListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public HotelDetailListView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout(context);
    }

    public HotelDetailListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
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
