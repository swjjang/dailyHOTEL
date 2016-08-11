package com.twoheart.dailyhotel.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DailyPlaceDetailListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
        EdgeEffectColor.setEdgeGlowColor(this, getResources().getColor(R.color.default_over_scroll_edge));
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
