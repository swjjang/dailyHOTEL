package com.twoheart.dailyhotel.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.AbsListView;
import android.widget.EdgeEffect;
import android.widget.ListView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.ExLog;

import java.lang.reflect.Field;

public class DailyPlaceDetailListView extends ListView
{
    private boolean mScrollable = true;
    private Field mGlowTopScaleY, mGlowBottomScaleY;
    private Object mTopEdgeEffect, mBottomEdgeEffect;

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

        initReflectionClass();
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

    private void initReflectionClass()
    {
        Class reflectionClass = AbsListView.class;

        try
        {
            Field mTopGlow = reflectionClass.getDeclaredField("mEdgeGlowTop");
            mTopGlow.setAccessible(true);
            mTopEdgeEffect = mTopGlow.get(this);

            mGlowTopScaleY = EdgeEffect.class.getDeclaredField("mGlowScaleY");
            mGlowTopScaleY.setAccessible(true);

            Field mBottomGlow = reflectionClass.getDeclaredField("mEdgeGlowBottom");
            mBottomGlow.setAccessible(true);
            mBottomEdgeEffect = mBottomGlow.get(this);

            mGlowBottomScaleY = EdgeEffect.class.getDeclaredField("mGlowScaleY");
            mGlowBottomScaleY.setAccessible(true);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    public float getGlowTopScaleY()
    {
        try
        {
            return mGlowTopScaleY.getFloat(mTopEdgeEffect);
        } catch (Exception e)
        {
            return 0.0f;
        }
    }

    public float getGlowBottomScaleY()
    {
        try
        {
            return mGlowBottomScaleY.getFloat(mBottomEdgeEffect);
        } catch (Exception e)
        {
            return 0.0f;
        }
    }
}
