package com.twoheart.dailyhotel.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.AbsListView;
import android.widget.EdgeEffect;
import android.widget.ScrollView;

import com.daily.base.util.ExLog;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

import java.lang.reflect.Field;

/**
 * Created by android_sam on 2017. 6. 2..
 */

public class DailyPlaceDetailScrollView extends ScrollView
{
    private boolean mScrollable = true;

    private float mDistanceX;
    private float mDistanceY;
    private float mLastX;
    private float mLastY;

    private Field mGlowTopScaleY;
    private Field mGlowBottomScaleY;
    private Object mTopEdgeEffect;
    private Object mBottomEdgeEffect;

    private OnScrollChangedListener mOnScrollChangedListener;

    public interface OnScrollChangedListener
    {
        void onScrollChanged(ScrollView scrollView, int l, int t, int oldl, int oldt);
    }

    public DailyPlaceDetailScrollView(Context context)
    {
        super(context);

        initLayout(context);
    }

    public DailyPlaceDetailScrollView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public DailyPlaceDetailScrollView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DailyPlaceDetailScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
        EdgeEffectColor.setEdgeGlowColor(this, getResources().getColor(R.color.default_over_scroll_edge));

        initReflectionClass();
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

    public void setScrollEnabled(boolean enable)
    {
        mScrollable = enable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        switch (ev.getAction() & MotionEventCompat.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
                // if we can scroll pass the event to the superclass
                if (mScrollable)
                {
                    return super.onTouchEvent(ev);
                }
                // only continue to handle the touch event if scrolling enabled
                return mScrollable; // mIsScrollable is always false at this point
            default:
                return super.onTouchEvent(ev);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        if (mScrollable == false)
        {
            return false;
        } else
        {
            switch (ev.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                {
                    mDistanceX = mDistanceY = 0f;
                    mLastX = ev.getX();
                    mLastY = ev.getY();

                    // This is very important line that fixes
                    computeScroll();
                    break;
                }
                case MotionEvent.ACTION_MOVE:
                {
                    final float curX = ev.getX();
                    final float curY = ev.getY();
                    mDistanceX += Math.abs(curX - mLastX);
                    mDistanceY += Math.abs(curY - mLastY);
                    mLastX = curX;
                    mLastY = curY;

                    if (mDistanceX > mDistanceY)
                    {
                        return false;
                    }
                }
            }

            return super.onInterceptTouchEvent(ev);
        }
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
